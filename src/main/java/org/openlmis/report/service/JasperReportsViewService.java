/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.report.service;

import static org.openlmis.report.i18n.JasperMessageKeys.ERROR_JASPER_REPORT_FORMAT_UNKNOWN;
import static org.openlmis.report.i18n.JasperMessageKeys.ERROR_JASPER_REPORT_GENERATION;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.util.Map;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.openlmis.report.domain.JasperTemplate;
import org.openlmis.report.exception.JasperReportViewException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JasperReportsViewService {

  @Autowired
  private DataSource replicationDataSource;

  /**
   * Create Jasper Report View.
   * Create Jasper Report (".jasper" file) from bytes from Template entity.
   * Set 'Jasper' exporter parameters, JDBC data source, web application context, url to file.
   *
   * @param jasperTemplate template that will be used to create a view
   * @param params  map of parameters
   * @return created jasper view.
   * @throws JasperReportViewException if there will be any problem with creating the view.
   */
  public byte[] getJasperReportsView(JasperTemplate jasperTemplate,
      Map<String, Object> params) throws JasperReportViewException {

    try {
      try (Connection connection = replicationDataSource.getConnection()) {
        ObjectInputStream inputStream = new ObjectInputStream(
            new ByteArrayInputStream(jasperTemplate.getData()));

        JasperPrint jasperPrint = JasperFillManager
            .fillReport((JasperReport) inputStream.readObject(), params, connection);

        return prepareReport(jasperPrint, params);
      }
    } catch (IllegalArgumentException iae) {
      throw new JasperReportViewException(iae,
          ERROR_JASPER_REPORT_FORMAT_UNKNOWN, iae.getMessage());
    } catch (Exception e) {
      throw new JasperReportViewException(e, ERROR_JASPER_REPORT_GENERATION);
    }
  }

  private byte[] prepareReport(JasperPrint jasperPrint, Map<String, Object> params)
      throws JRException {
    return getJasperExporter((String) params.get("format"), jasperPrint).exportReport();
  }

  private JasperExporter getJasperExporter(String format, JasperPrint jasperPrint) {
    switch (format) {
      case "pdf":
        return new JasperPdfExporter(jasperPrint);
      case "csv":
        return new JasperCsvExporter(jasperPrint);
      case "xls":
        return new JasperXlsExporter(jasperPrint);
      case "html":
        return new JasperHtmlExporter(jasperPrint);
      default:
        throw new IllegalArgumentException(format);
    }
  }
}

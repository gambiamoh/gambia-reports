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

package org.openlmis.report.dto.external.fulfillment;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum OrderStatusDto {
  CREATING,
  ORDERED,
  FULFILLING,
  SHIPPED,
  RECEIVED,
  TRANSFER_FAILED,
  IN_ROUTE,
  READY_TO_PACK;

  private static final Map<OrderStatusDto, String> TRANSLATIONS =
      Collections.unmodifiableMap(new HashMap<OrderStatusDto, String>() {{
          put(CREATING, "Criando");
          put(ORDERED, "Pediu");
          put(FULFILLING, "A executar pedido");
          put(SHIPPED, "Enviado");
          put(RECEIVED, "Recebido");
          put(TRANSFER_FAILED, "Transferência sem êxito");
          put(IN_ROUTE, "A Caminho");
          put(READY_TO_PACK, "Pronto para embalar");
        }
      });

  /**
   * Find a correct {@link OrderStatusDto} instance based on the passed string. The method ignores
   * the case.
   *
   * @param arg string representation of one of order status.
   * @return instance of {@link OrderStatusDto} if the given string matches status; otherwise null.
   */
  public static OrderStatusDto fromString(String arg) {
    for (OrderStatusDto status : values()) {
      if (equalsIgnoreCase(arg, status.name())) {
        return status;
      }
    }

    return null;
  }

  public String getTranslation() {
    return TRANSLATIONS.get(this);
  }

  public static String getTranslation(String status) {
    return Objects.requireNonNull(fromString(status)).getTranslation();
  }
}

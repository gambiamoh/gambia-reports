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

package org.openlmis.report.domain;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.UUID;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openlmis.util.View;

@MappedSuperclass
public abstract class BaseEntity {
  protected static final String TEXT_COLUMN_DEFINITION = "text";
  protected static final String UUID_COLUMN_DEFINITION = "pg-uuid";
  protected static final String BOOLEAN_COLUMN_DEFINITION = "boolean";

  @Id
  @GeneratedValue(generator = "uuid-gen")
  @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
  @JsonView(View.BasicInformation.class)
  @Type(type = UUID_COLUMN_DEFINITION)
  @Getter
  @Setter
  protected UUID id;
}

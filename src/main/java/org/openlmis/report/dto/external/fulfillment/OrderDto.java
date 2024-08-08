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

import static java.util.Collections.emptySet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.dto.external.referencedata.FacilityDto;
import org.openlmis.report.dto.external.referencedata.GeographicZoneDto;
import org.openlmis.report.dto.external.referencedata.ProcessingPeriodDto;
import org.openlmis.report.dto.external.referencedata.ProgramDto;
import org.openlmis.report.dto.external.referencedata.UserDto;
import org.openlmis.report.dto.external.requisition.RequisitionStatusDto;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

  public static final Integer DISTRICT_LEVEL = 3;
  public static final Integer REGION_LEVEL = 2;

  private UUID id;

  private UUID externalId;

  private Boolean emergency;

  private FacilityDto facility;

  private ProcessingPeriodDto processingPeriod;

  private ZonedDateTime createdDate;

  private UserDto createdBy;

  private ProgramDto program;

  private FacilityDto requestingFacility;

  private FacilityDto receivingFacility;

  private FacilityDto supplyingFacility;

  private String orderCode;

  private OrderStatusDto status;

  private BigDecimal quotedCost;

  private List<OrderLineItemDto> orderLineItems;

  private List<StatusMessageDto> statusMessages;

  private List<StatusChangeDto> statusChanges;

  /**
   * Get status change with given status.
   * @return status change
   */
  @JsonIgnore
  public StatusChangeDto getStatusChangeByStatus(RequisitionStatusDto status) {
    return Optional.of(statusChanges).orElse(new ArrayList<>()).stream()
            .filter(statusChange -> status.equals(statusChange.getStatus())
    ).findFirst().orElse(null);
  }

  /**
   * Get zone of the facility that has the district level.
   * @return district of the facility.
   */
  @JsonIgnore
  public GeographicZoneDto getThirdLevelFacility() {
    return getFacility().getZoneByLevelNumber(DISTRICT_LEVEL);
  }

  /**
   * Get zone of the facility that has the region level.
   * @return region of the facility.
   */
  @JsonIgnore
  public GeographicZoneDto getSecondLevelFacility() {
    return getFacility().getZoneByLevelNumber(REGION_LEVEL);
  }

  /**
   * Get status change that is AUTHORIZED.
   * @return authorized status change.
   */
  @JsonIgnore
  public StatusChangeDto getAuthorizedStatusChange() {
    return Optional.ofNullable(getStatusChangeByStatus(RequisitionStatusDto.AUTHORIZED))
            .orElse(new StatusChangeDto());
  }

  /**
   * Get status change that is APPROVED.
   * @return approved status change.
   */
  @JsonIgnore
  public StatusChangeDto getApprovedStatusChange() {
    return Optional.ofNullable(getStatusChangeByStatus(RequisitionStatusDto.APPROVED))
            .orElse(new StatusChangeDto());
  }

  /**
   * Get status changes that have APPROVED or IN_APPROVAL status
   * and were done after last AUTHORIZE change.
   *
   * @return approved status change.
   */
  @JsonIgnore
  public Set<StatusChangeDto> getInApprovalStatusChanges() {
    Optional<StatusChangeDto> lastAuthorization = Optional.of(statusChanges)
        .orElse(new ArrayList<>()).stream()
        .filter(statusChange -> RequisitionStatusDto.AUTHORIZED.equals(statusChange.getStatus()))
        .sorted()
        .reduce((first, second) -> second);

    if (lastAuthorization.isPresent()) {
      ZonedDateTime lastAuthorizationDate = lastAuthorization.get().getCreatedDate();
      return Optional.of(statusChanges).orElse(new ArrayList<>()).stream()
          .filter(statusChange -> RequisitionStatusDto.IN_APPROVAL.equals(statusChange.getStatus()))
          .filter(statusChange -> statusChange.getCreatedDate().isAfter(lastAuthorizationDate))
          .collect(Collectors.toCollection(TreeSet::new));
    }
    return emptySet();
  }

  /**
   * Get status change that is RELEASED.
   * @return released status change.
   */
  @JsonIgnore
  public StatusChangeDto getReleasedStatusChange() {
    return Optional.ofNullable(getStatusChangeByStatus(RequisitionStatusDto.RELEASED))
            .orElse(new StatusChangeDto());
  }
}

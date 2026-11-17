/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2020 VillageReach
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

package org.openlmis.report.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openlmis.report.domain.JasperTemplate;
import org.openlmis.report.domain.ReportCategory;
import org.openlmis.report.dto.JasperTemplateDto;
import org.openlmis.report.dto.external.fulfillment.OrderDto;
import org.openlmis.report.dto.external.fulfillment.StatusChangeDto;
import org.openlmis.report.dto.external.referencedata.UserDto;
import org.openlmis.report.dto.external.requisition.RequisitionStatusDto;
import org.openlmis.report.exception.JasperReportViewException;
import org.openlmis.report.exception.NotFoundMessageException;
import org.openlmis.report.repository.JasperTemplateRepository;
import org.openlmis.report.service.JasperReportsViewService;
import org.openlmis.report.service.JasperTemplateService;
import org.openlmis.report.service.PermissionService;
import org.openlmis.report.service.fulfillment.OrderService;
import org.openlmis.report.service.referencedata.UserReferenceDataService;
import org.openlmis.report.utils.AuthenticationHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

@SuppressWarnings("PMD.TooManyMethods")
public class JasperTemplateControllerTest {

  private static final String ORDER = "order";

  @Mock
  private ReportCategory reportCategory;

  @Mock
  private PermissionService permissionService;

  @Mock
  private JasperTemplateService jasperTemplateService;

  @Mock
  private JasperReportsViewService jasperReportsViewService;

  @Mock
  private JasperTemplateRepository jasperTemplateRepository;

  @Mock
  private AuthenticationHelper authenticationHelper;

  @Mock
  private OrderService orderService;

  @Mock
  private UserReferenceDataService userReferenceDataService;

  @InjectMocks
  private JasperTemplateController jasperTemplateController;

  private JasperTemplate jasperTemplate;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    jasperTemplate = new JasperTemplate();
    jasperTemplate.setId(UUID.randomUUID());
    jasperTemplate.setName("name");
    jasperTemplate.setRequiredRights(new ArrayList<>());
    jasperTemplate.setCategory(reportCategory);

    ReflectionTestUtils.setField(jasperTemplateController,
        "dateTimeFormat", "dd/MM/yyyy HH:mm:ss");
    ReflectionTestUtils.setField(jasperTemplateController, "dateFormat", "dd/MM/yyyy");
    ReflectionTestUtils.setField(jasperTemplateController, "groupingSeparator", ",");
    ReflectionTestUtils.setField(jasperTemplateController, "groupingSize", "3");
  }

  @Test
  public void shouldGetTemplate() {
    doNothing().when(permissionService).canViewReports();
    when(jasperTemplateRepository.findById(jasperTemplate.getId()))
        .thenReturn(Optional.of(jasperTemplate));

    JasperTemplateDto templateDto = jasperTemplateController.getTemplate(jasperTemplate.getId());
    assertEquals(templateDto.getId(), jasperTemplate.getId());
  }

  @Test(expected = NotFoundMessageException.class)
  public void shouldThrowNotFoundMessageExceptionIfTemplateIsNotFound() {
    doNothing().when(permissionService).canViewReports();
    jasperTemplateController.getTemplate(jasperTemplate.getId());
  }

  @Test
  public void reportShouldHaveCorrectNameAndContentTypeInHeaders()
      throws JasperReportViewException {
    doNothing().when(permissionService).canViewReports();
    doNothing().when(permissionService).validatePermissions();

    UserDto currentUser = new UserDto();
    currentUser.setId(UUID.randomUUID());

    when(jasperTemplateService.mapReportImagesToTemplate(any())).thenReturn(new HashMap<>());
    when(jasperTemplateService.mapRequestParametersToTemplate(any(), any()))
        .thenReturn(new HashMap<>());
    when(jasperTemplateRepository.findById(jasperTemplate.getId()))
        .thenReturn(Optional.of(jasperTemplate));
    when(jasperReportsViewService.getJasperReportsView((byte[]) any(), any()))
        .thenReturn(new byte[] {0, 1, 2, 3, 4});
    when(authenticationHelper.getCurrentUser())
        .thenReturn(currentUser);

    MockHttpServletRequest request = new MockHttpServletRequest();
    ResponseEntity<byte[]> response = jasperTemplateController.generateReport(request,
        jasperTemplate.getId(), "xlsx", "en");
    assertTrue(response.getHeaders().toString().contains("name.xlsx"));
    assertTrue(response.getHeaders().toString().contains("Content-Type:\"application/"
        + "vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8\""));

    request = new MockHttpServletRequest();
    response = jasperTemplateController.generateReport(request,
        jasperTemplate.getId(), "xlsx", null);
    assertTrue(response.getHeaders().toString().contains("name.xlsx"));
    assertTrue(response.getHeaders().toString().contains("Content-Type:\"application/"
        + "vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8\""));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldPassOrderAndLineItemsToOrderTemplate() throws JasperReportViewException {
    jasperTemplate.setName("Order");
    UUID orderId = UUID.randomUUID();
    Map<String, Object> params = new HashMap<>();
    params.put(ORDER, orderId.toString());
    OrderDto order = new OrderDto();
    order.setOrderLineItems(new ArrayList<>());
    UserDto currentUser = new UserDto();
    currentUser.setId(UUID.randomUUID());
    currentUser.setFirstName("John");
    currentUser.setLastName("Doe");
    mockReportGeneration(params, currentUser);
    when(orderService.findOne(orderId)).thenReturn(order);

    jasperTemplateController.generateReport(new MockHttpServletRequest(),
        jasperTemplate.getId(), "pdf", "en");

    ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
    verify(jasperReportsViewService).getJasperReportsView(any(JasperTemplate.class),
        captor.capture());
    assertEquals(currentUser.printName(), captor.getValue().get("user"));
    assertEquals(order, captor.getValue().get(ORDER));
    assertEquals(order.getOrderLineItems(), captor.getValue().get("datasource"));
  }

  @Test
  public void shouldNotLoadOrderForOtherTemplates() throws JasperReportViewException {
    UserDto currentUser = new UserDto();
    currentUser.setId(UUID.randomUUID());
    mockReportGeneration(new HashMap<>(), currentUser);

    jasperTemplateController.generateReport(new MockHttpServletRequest(),
        jasperTemplate.getId(), "pdf", "en");

    verify(orderService, never()).findOne(any(UUID.class));
    verify(userReferenceDataService, never()).findOne(any(UUID.class));
  }

  @Test
  public void shouldFillStatusChangeAuthorsForOrderTemplate() throws JasperReportViewException {
    UserDto authorizer = user("Ann", "Smith");
    UserDto approver = user("Bob", "Jones");
    UserDto releaser = user("Carl", "Brown");
    when(userReferenceDataService.findOne(authorizer.getId())).thenReturn(authorizer);
    when(userReferenceDataService.findOne(approver.getId())).thenReturn(approver);
    when(userReferenceDataService.findOne(releaser.getId())).thenReturn(releaser);
    OrderDto order = orderWithStatusChanges(
        statusChange(RequisitionStatusDto.AUTHORIZED, authorizer.getId()),
        statusChange(RequisitionStatusDto.APPROVED, approver.getId()),
        statusChange(RequisitionStatusDto.RELEASED, releaser.getId()));

    OrderDto printed = printOrder(order);

    assertEquals("Ann Smith", printed.getAuthorizedStatusChange().getAuthor().printName());
    assertEquals("Bob Jones", printed.getApprovedStatusChange().getAuthor().printName());
    assertEquals("Carl Brown", printed.getReleasedStatusChange().getAuthor().printName());
  }

  @Test
  public void shouldLookUpEachAuthorOnce() throws JasperReportViewException {
    UserDto author = user("Ann", "Smith");
    when(userReferenceDataService.findOne(author.getId())).thenReturn(author);
    OrderDto order = orderWithStatusChanges(
        statusChange(RequisitionStatusDto.AUTHORIZED, author.getId()),
        statusChange(RequisitionStatusDto.APPROVED, author.getId()));

    OrderDto printed = printOrder(order);

    verify(userReferenceDataService, times(1)).findOne(author.getId());
    assertEquals("Ann Smith", printed.getAuthorizedStatusChange().getAuthor().printName());
    assertEquals("Ann Smith", printed.getApprovedStatusChange().getAuthor().printName());
  }

  @Test
  public void shouldSkipStatusChangesWithoutAuthorId() throws JasperReportViewException {
    OrderDto order = orderWithStatusChanges(
        statusChange(RequisitionStatusDto.AUTHORIZED, null));

    OrderDto printed = printOrder(order);

    verify(userReferenceDataService, never()).findOne(nullable(UUID.class));
    assertNull(printed.getAuthorizedStatusChange().getAuthor());
  }

  @Test
  public void shouldLeaveAuthorEmptyWhenUserIsNotFound() throws JasperReportViewException {
    UUID missingUserId = UUID.randomUUID();
    when(userReferenceDataService.findOne(missingUserId)).thenReturn(null);
    OrderDto order = orderWithStatusChanges(
        statusChange(RequisitionStatusDto.AUTHORIZED, missingUserId),
        statusChange(RequisitionStatusDto.APPROVED, missingUserId));

    OrderDto printed = printOrder(order);

    verify(userReferenceDataService, times(1)).findOne(missingUserId);
    assertNull(printed.getAuthorizedStatusChange().getAuthor());
    assertNull(printed.getApprovedStatusChange().getAuthor());
  }

  @Test
  public void shouldNotFailOrderPrintWhenOrderHasNoStatusChanges()
      throws JasperReportViewException {
    OrderDto order = new OrderDto();
    order.setOrderLineItems(new ArrayList<>());
    order.setStatusChanges(null);

    printOrder(order);

    verify(userReferenceDataService, never()).findOne(any(UUID.class));
  }

  @SuppressWarnings("unchecked")
  private OrderDto printOrder(OrderDto order) throws JasperReportViewException {
    jasperTemplate.setName("Order");
    UUID orderId = UUID.randomUUID();
    Map<String, Object> params = new HashMap<>();
    params.put(ORDER, orderId.toString());
    UserDto currentUser = new UserDto();
    currentUser.setId(UUID.randomUUID());
    mockReportGeneration(params, currentUser);
    when(orderService.findOne(orderId)).thenReturn(order);

    jasperTemplateController.generateReport(new MockHttpServletRequest(),
        jasperTemplate.getId(), "pdf", "en");

    ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
    verify(jasperReportsViewService).getJasperReportsView(any(JasperTemplate.class),
        captor.capture());
    return (OrderDto) captor.getValue().get(ORDER);
  }

  private OrderDto orderWithStatusChanges(StatusChangeDto... statusChanges) {
    OrderDto order = new OrderDto();
    order.setOrderLineItems(new ArrayList<>());
    order.setStatusChanges(new ArrayList<>(Arrays.asList(statusChanges)));
    return order;
  }

  private StatusChangeDto statusChange(RequisitionStatusDto status, UUID authorId) {
    return new StatusChangeDto(status, authorId, ZonedDateTime.now(), null);
  }

  private UserDto user(String firstName, String lastName) {
    UserDto user = new UserDto();
    user.setId(UUID.randomUUID());
    user.setFirstName(firstName);
    user.setLastName(lastName);
    return user;
  }

  private void mockReportGeneration(Map<String, Object> params, UserDto currentUser)
      throws JasperReportViewException {
    doNothing().when(permissionService).canViewReports();
    doNothing().when(permissionService).validatePermissions();
    when(jasperTemplateService.mapReportImagesToTemplate(any())).thenReturn(new HashMap<>());
    when(jasperTemplateService.mapRequestParametersToTemplate(any(), any())).thenReturn(params);
    when(jasperTemplateRepository.findById(jasperTemplate.getId()))
        .thenReturn(Optional.of(jasperTemplate));
    when(jasperReportsViewService.getJasperReportsView(any(JasperTemplate.class), anyMap()))
        .thenReturn(new byte[] {0, 1, 2});
    when(authenticationHelper.getCurrentUser()).thenReturn(currentUser);
  }
}

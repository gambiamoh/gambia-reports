INSERT INTO report.template_parameters
(id, datatype, displayname, name, selectexpression, selectproperty, displayproperty, required, templateid)
VALUES
  ('524200da-34c9-48df-a6fb-ff151727b841', 'java.lang.String', 'Receiving Facility', 'receiving_facility', '/api/facilities', 'id', 'name', false, '7196b57f-6b41-418f-a1e6-f49f41551f40'),
  ('624200eb-34c9-48df-a6fb-ff151727b841', 'java.lang.String', 'Product', 'orderable', '/api/orderables', 'id', 'fullProductName', false, '7196b57f-6b41-418f-a1e6-f49f41551f40'),
  ('72420fdb-34c9-48df-a6fb-ff151727b841', 'java.lang.String', 'Program', 'program', '/api/programs', 'id', 'name', true, '7196b57f-6b41-418f-a1e6-f49f41551f40');
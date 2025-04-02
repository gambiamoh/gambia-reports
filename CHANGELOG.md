1.0.1 / wip
==================

* Updated dev, rsyslog and service-base image versions
* Added lombok annotation processor dependency
* Upgraded javers to 5.13.2
* Added Dashboard reports functionality
  * Allows permitted users to add, manage and view Dashboard reports (currently PowerBI and Superset)
  * Both Dashboard and Jasper Reports are grouped now by Report Categories
  * Requires rights, which were added in the [referencedata service 15.2.9](https://github.com/OpenLMIS/openlmis-referencedata/tree/rel-15.2.9)
  * `/api/reports/dashboardReports`
    * `GET /{id}`, `GET`, `GET /availableReports`, `DELETE /{id}`, `POST`, `PUT /{id}`
  * `/api/reports/reportCategories`
    * `GET /{id}`, `GET`, `POST`, `PUT /{id}`, `DELETE`

1.0.0 / 2025-01-13
==================

Initial release of gambia-reports service

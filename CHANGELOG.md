Upcoming Version / WIP
==================

1.1.3 / 2025-12-04
==================
- [OPSD-63](https://openlmis.atlassian.net/browse/OPSD-63) Periodic Stock On Hand Summary pulling correct data but duplicating records

1.1.2 / 2025-09-23
==================

- [OPSD-65](https://openlmis.atlassian.net/browse/OPSD-65)
    - Item Ledger Report
      - Fixed typos
      - Changed naming of some columns for better alignment
      - Added possibility to filter by parent zone
      - Added data pickers in place of text data fields

1.1.1 / 2025-09-19
==================

- [OPSD-52](https://openlmis.atlassian.net/browse/OPSD-52)
  - Expired & Near-Expiring Item Reports: Added a new filter for Geographic Zone
- [OPSD-54](https://openlmis.atlassian.net/browse/OPSD-54)
  - Item Ledger Report: Stock on hand is now retrieved based on the selected date

1.1.0 / 2025-05-16
==================

* Updated dev, rsyslog and service-base image versions
* Added lombok annotation processor dependency
* Upgraded javers to 5.13.2
* Added Dashboard reports functionality
  * Allows permitted users to add, manage and view Dashboard reports (currently PowerBI and Superset)
  * Both Dashboard and Jasper Reports are grouped now by Report Categories
  * `/api/reports/dashboardReports`
    * `GET /{id}`, `GET`, `GET /availableReports`, `DELETE /{id}`, `POST`, `PUT /{id}`
  * `/api/reports/reportCategories`
    * `GET /{id}`, `GET`, `POST`, `PUT /{id}`, `DELETE`
  * **Requires rights, which were added in the [referencedata service 15.2.9](https://github.com/OpenLMIS/openlmis-referencedata/tree/rel-15.2.9)**
* Added support for XLSX report generation
* Added Point in time Stock on Hand summary report
* Added Periodic Stock on Hand summary report
* User ID has been passed for report generation in order to restrict displayed data inside the report
* Improved test coverage

1.0.0 / 2025-01-13
==================

Initial release of gambia-reports service

# attendance-management
1.Access H2 Database

URL → http://localhost:8080/h2-console

JDBC URL → jdbc:h2:mem:attdb

Username → sa

Password → (blank)
/********************/
By default, this project uses H2 in-memory database.

Schema and tables are auto-created by JPA (spring.jpa.hibernate.ddl-auto=update).

No external setup required. Data will reset when the app restarts.
/**************/

2.github:https://github.com/sandeepverma2002/attendance-management.git

3.Employee APIs

Create Employee → POST /api/employees/create

Get All Employees → GET /api/employees/getall
4.Attendance APIs

Mark Attendance (Punch In/Out) → POST /api/attendance/mark

Get Attendance Report → GET /api/attendance/report

/******************************/
Name:sandeep-kumar

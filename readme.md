To reproduce the issue:

* start the services in docker-compose-dependencies.yml
* start the application
* call POST on http://localhost:8094/actuator/refresh with empty body {}

You will see the AMQP connections taken down and restarted with ERROR level log messages in the log.

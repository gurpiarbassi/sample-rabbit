To reproduce the issue:

1 - start the services in docker-compose-dependencies.yml
2 - start the application
3 - call POST on http://localhost:8094/actuator/refresh with empty body {}

You will see the AMQP connections taken down and restarted with ERROR level log messages in the log.
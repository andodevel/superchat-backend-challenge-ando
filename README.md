# Superchat - Backend Technical Challenge
Author: [An Do](ando.devel@gmail.com)

-----------------------------------------

## Assumptions
I develop this challenge with some assumptions in mind:
1. Only local dev environment supported, no production build.
2. No realtime chat supported.
3. Webhook sends anonymous message.


## LOCAL UP AND RUNNING
To ease the development, external tools this application dependents on start in docker containers.
However, the quarkus application itself run directly in localhost using Maven.

See [START_AND TEST_GUIDE](START_AND_TEST.md)

### Logging and debugging
DEBUG log files are generated under _logs/_ folder.

[Adminer](https://www.adminer.org/) is deployed at http://localhost:8082/. 
You could manage Postgres DB with its simple UI. Use below configuration to connect:
- username=superuser
- password=superuser
- host=superchat-postgres
- database=superchat_database
- schema=public

## Open for discussion




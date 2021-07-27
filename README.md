# Superchat - Backend Technical Challenge
Author: [An Do](ando.devel@gmail.com)

-----------------------------------------
I try to design and implement the challenge with microservice design in mind.
However, the setup and configuration of microservices quickly grows
and draws too much effort that distract me from real business logic.
Nevertheless, I decided to take a hybrid approach:
1. Module packages under `de.superchat` could be viewed as microservices on their own.
2. Database tables are designed to be used in their own service context boundary.
The only exception is auth and user services share `user` table.
3. Internal communication are done by REST client and context propagation.
4. No API gateway. I intended to add Nginx as API routing but because we keep code in 
one place and run in same Quarkus instance. There is no more need.

## Assumptions
I assume a couple of things during the development:
1. Only local dev environment supported, no production build.
2. No realtime chat supported.
3. I actually created `source` table with external webhook.
   However, I end up to not support creation and chatting with external users.
   The main focus for webhook is to _"receive messages from an external service via a webhook"_,
   not communicate with them.
   
## Tech stack
1. Language: `Java`(sorry, not `Kotlin`!)
2. Framework: `Quarkus`
3. Database: `Postgres`
4. ORM: `Hibernate`
5. Build tool: `Maven`
6. DevOps: `Docker`

## LOCAL UP AND RUNNING
To ease the development, dependencies(Postgres, Admininer) start in docker containers.
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
# Superchat - Backend Technical Challenge
Author: [An Do](ando.devel@gmail.com)

-----------------------------------------

## Assumptions
I develop this challenge with some assumptions in mind:
1. Only local dev environment supported, no production build.
2. No realtime chat supported.
3. Webhook sends anonymous message.

## Installation
1. JDK 11
2. Docker and docker compose

## LOCAL UP AND RUNNING

To ease the development, external tools this application dependents on start in docker containers. 
However, the quarkus application itself run directly in localhost using Maven.

cd to the repository directory and follow below instruction.

### Make the running script executable
Note that you only need to do this once
 ```bash
chmod +x ./dev.sh
 ```

### Start 
Start docker containers and quarkus in background.
 ```bash
./dev.sh -r
 ```  
_-r_ option is to initialize/reset dev environment. 
Next time, if you do not change anything, _-r_ could be omitted.
 ```bash
./dev.sh
 ```

### Stop
Specify _-s_ option to stop running docker containers and quarkus.
 ```bash
./dev.sh -s
 ```  

## Test the application
The application exposes 4 services:
* `/api/auth` Authentication service
* `/api/users` User service
* `/api/messages` Message service
* `/api/webhooks` Webhook service

Database is initialized with some test users:
1. superchatadmin/superchatadmin
2. andodevel/andodevel

### From the CLI
You can try these endpoints with http client `curl`.

Exchange username and password to JWT access token.
The application only support token bearer security model.
```bash
curl -i -X GET http://localhost:8080/api/auth/login
```

### Using Postman
Postman is great option for non-CLI testing.


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




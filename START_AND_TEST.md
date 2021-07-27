# LOCAL START AND TESTING GUIDE

## Installation
1. JDK 11
2. Docker and Docker compose

cd to the repository directory and follow below instruction.

### Make the running script executable
Note that you only need to do this once
 ```bash
chmod +x ./dev.sh
 ```

### Start 
Start Docker containers in background and Quarkus on foreground(able to stop by SIGINT).
 ```bash
./dev.sh -r
 ```  
_-r_ option is to initialize/reset dev environment. 
Next time, if you do not change anything, _-r_ could be omitted.
 ```bash
./dev.sh
 ```

### Stop
Specify _-s_ option to stop running Docker containers and quarkus.
 ```bash
./dev.sh -s
 ```  

## Test the application
There are service endpoints to solve the challenge

METHOD | END POINT| AUTHENTICATION REQUIRED | DESCRIPTION
--- | --- | --- | ---
POST |`/api/users` | N | Create contacts given their personal information (Name, E-Mail, etc)
GET |`/api/users` | Y | List all contacts
POST |`/api/messages` | Y | Send a message to a contact
GET |`/api/messages` | Y | List all previous conversations
POST |`/api/webhooks/{id}/messages` | N | Receive messages from an external service via a webhook

Database is initialized with some test users:
1. superchatadmin/superchatadmin
2. andodevel/andodevel

### Using Postman
Postman is great option for non-CLI testing.

I have exported my [Postman test collection](postman_v2.1_export.json).
Please import and just followed the ordered tests.

### Using OpenAPI Swagger UI
An instance of Swagger UI is deployed at http://localhost:8080/q/swagger-ui/.

*NOTE*: The very first thing before explore the API is token exchange via `/auth/login` service,
then set this token as Bearer for every API endpoint secured.
If you use my Postman collection to test, it already automate the process of set this header.
Just run the `1. Exchange username/password for JWT access token` test case.

### Substitution feature
Substitution are support for placeholder of:
1. `@<username>` to be replaced with `<firstname> <lastname>`
2. `BTC<number>` to be replaced with `$<USDT>`

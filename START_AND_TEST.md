# LOCAL START AND TESTING GUIDE

## Installation
1. JDK 11
2. Docker and docker compose


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

### Using Postman
Postman is great option for non-CLI testing.




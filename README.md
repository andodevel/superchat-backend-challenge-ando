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

## Local up and running 

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
### From  the CLI
The application exposes 3 endpoints:
* `/api/users`
* `/api/messages`
* `/api/webhooks`

You can try these endpoints with an http client (`curl`, `Postman`, etc).
Here you have some examples to check the security configuration:

```bash
curl -i -X GET http://localhost:8080/api/users  # 'public'
```

## Open issues



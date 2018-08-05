# User Service API
## Start
To start up the application use
```shell
mvn spring-boot:run
```
When it's up you can visit http://localhost:8080/swagger-ui.html to browse the endpoints

## Usage examples
### POST User Request (register a new user)
POST : /users
```JSON
{
  "id": 1,
  "lastname": "bezzanou",
  "ipv4": "43.172.130.209"
}
```

### PUT User Request (update an existing user)
PUT : /users
```JSON
{
  "id": 1,
  "lastname": "bezzanou",
  "ipv4": "43.172.130.209"
}
```

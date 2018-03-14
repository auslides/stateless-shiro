# stateless-shiro
Shiro used in REST Web Serivce

Usage:
Launch：
```
mvn clean package spring-boot:run
```
or run Application.main() in your IDE.

Initialization: Filled in users for test
````
PUT http://localhost:8080/users/init
````

Get a list of users：
```
GET http://localhost:8080/users
```
Return：401 Unauthorized
```
Body: {"timestamp":1482019369406,"status":401,"error":"Unauthorized","message":"No message available","path":"/users"}
```

Login：
```
POST http://localhost:8080/users/auth
Content-Type: application/json
Body: {"username":"balala@gmail.com","password":"1111"}
```
Response for successfully logged in：
```
{"message":"ok","email":"balala@gmail.com","status":200,"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJndW9mZW5nemhAZ21haWwuY29tIiwiZXhwIjoxNDg0NTgyNDAwfQ.BAr84eeVU0Thq4Y5bAc4gNdJw8l8nwjN1Vvrvmf1M94"}
```

Authentication failed：
```
{"failureReason":"invalidData","message":"unauthorized","status":401}
```

Get a list of users：
```
GET http://localhost:8080/users
```
Header
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJndW9mZW5nemhAZ21haWwuY29tIiwiZXhwIjoxNDg0NTgyNDAwfQ.BAr84eeVU0Thq4Y5bAc4gNdJw8l8nwjN1Vvrvmf1M94
Accept: application/json
```
Log out
```
DELETE http://localhost:8080/users/logout
```
Header
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJndW9mZW5nemhAZ21haWwuY29tIiwiZXhwIjoxNDg0NTgyNDAwfQ.BAr84eeVU0Thq4Y5bAc4gNdJw8l8nwjN1Vvrvmf1M94
```

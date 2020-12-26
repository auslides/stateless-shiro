# stateless-shiro
Shiro used in REST Web Serivce

### Launch
```
mvn clean package spring-boot:run
```
or run Application.main() in your IDE.

Initialization: Filled in users for test
````
curl -H "Content-Type: application/json" -X PUT http://localhost:8080/users/init
````

### Get a list of users
```
curl http://localhost:8080/users
```
Return：401 Unauthorized
```
Body: {"timestamp":1482019369406,"status":401,"error":"Unauthorized","message":"No message available","path":"/users"}
```

### Login
```
curl -H "Accept: application/json" -H "Content-Type: application/json" -X POST -d "{'username':'balala@gmail.com','password':'1111'}" http://localhost:8080/users/auth
```
Response for successfully logged in：
```
{"message":"ok","email":"balala@gmail.com","status":200,"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiYWxhbGFAZ21haWwuY29tIiwiZXhwIjoxNjExNTkwNDAwfQ.hFy5UzQ9J3sUIt2PC79u4JtQM2q57z7PwhzV39loIBs"}
```

Authentication failed：
```json
{"failureReason":"invalidData","message":"unauthorized","status":401}
```

### Get a list of users again
```
curl -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiYWxhbGFAZ21haWwuY29tIiwiZXhwIjoxNjExNTkwNDAwfQ.hFy5UzQ9J3sUIt2PC79u4JtQM2q57z7PwhzV39loIBs" -X GET http://localhost:8080/users
```
Returns:
```json
[{"id":null,"email":"dopper@gmail.com","name":"Dopper","active":true,"password":"$shiro1$SHA-256$500000$ZSH+0wy6o2657wepeJsCyg==$FjTsLhJl9M+z4qsAMxF5afSzpp1Vo5FmjiklO/dujnk=","roles":[{"id":null,"name":"DO_SOMETHING","description":null,"permissions":[{"id":null,"name":"DO_SOMETHING","description":null}]}]},{"id":null,"email":"balala@gmail.com","name":"Balala","active":true,"password":"$shiro1$SHA-256$500000$WtO6/UO62knKgYWkjDQeiA==$qimvEhdEXxmcI5CfPJ5vqIh4pGXoCPQSNgJ51Q8OLy0=","roles":[{"id":null,"name":"ADMIN","description":null,"permissions":[{"id":null,"name":"VIEW_ALL_USERS","description":null}]}]}]
```
### Log out
```
curl -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiYWxhbGFAZ21haWwuY29tIiwiZXhwIjoxNjExNTkwNDAwfQ.hFy5UzQ9J3sUIt2PC79u4JtQM2q57z7PwhzV39loIBs" -X DELETE http://localhost:8080/users/logout
```

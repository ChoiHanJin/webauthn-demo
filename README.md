Source code for the blog post: https://golb.hplar.ch/2019/08/webauthn.html


### Online Demo: https://webauthn-omed.hplar.ch/
---
최한진 추가 시작
# webauthn 분석
OS: `Ubuntu 16.04.4`

## server
```
server
│   mvnw
│   ...
│
└─── src
    │
    └─── main
        │   
        └─── java
        │   │
        │   └───...
        │
        └─── resouces
            │   application.properties
            │
            └─── db
                │
                └───...
```

##### 서버 시작 전 해야할 일
1. MariaDB 설치
MariaDB를 제외한 다른 DB를 사용해도 되지만 그렇게 하면 서버 설정을 바꿔야한다.
`https://mariadb.org/download/` MariaDB 홈페이지에서 MariaDB를 설치한다.

2. MariaDB 설정
콘솔창에 아래와 같이 입력하여 MariaDB 콘솔에 접속한다.
```
$ mysql -uroot -pYYYY
```
YYYY는 계정 비밀번호이다.

아래와 같은 커맨드로 DB를 만들고 DB에 접속 가능한 유저를 만든다.
```
create database db_name;
create user 'user_name'@'xxx.xxx.xxx.xxx' identified by 'user_password';
grant all privileges on db_name.* to 'user_name'@'xxx.xxx.xxx.xxx';
flush privileges;
```
`db_name`은 새로 생성할 DB의 이름이다.
`user_name`에 원하는 user name을 등록한다.
`xxx.xxx.xxx.xxx`에 DB에 접속할 IP를 입력한다.(잘 모르겠다면 127.0.0.1을 입력한다.)
`user_password`에 원하는 비밀번호를 입력한다.

아래와 같은 커맨드로 MariaDB의 유저 목록을 볼 수 있다.
```
select host, user from mysql.user;
````

3. server 설정
`/server/src/main/resouces/application.properties` 파일의 6~8번 라인을 다음과 같이 수정한다.
```
spring.datasource.url=jdbc:mariadb://localhost:3306/db_name?serverTimezone=UTC&useMysqlMetadata=true
spring.datasource.username=user_name
spring.datasource.password=user_password
```
`db_name`, `user_name`, `user_password`를 위에서 설정한 값과 동일하게 설정한다.

##### 서버 구동
`/server` 디렉토리에서 `sudo ./mvnw spring-boot:run`을 입력한다.

## client
```
client
│   angular.json
│   proxy.conf 
│   ...
│
└─── src
    │   ...
    │
    └─── app
        │   app-routing.module.ts
        │   auth.guard.ts
        │   auth.service.ts
        │   ...
        │
        └─── home
        │       home.page.ts
        │       ...
        │
        └─── login
        │       login.page.ts
        │       ...
        │
        └─── registration
                registration.page.ts
                ...
```


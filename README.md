Source code for the blog post: https://golb.hplar.ch/2019/08/webauthn.html


### Online Demo: https://webauthn-omed.hplar.ch/
---

# webauthn 분석
테스트 환경  
OS: Ubuntu 18.04.5 LTS  
DB port: 3306  
server port: 8080  
client port: 8100  
이 문서의 루트 디렉토리 `/`는 `webauthn-demo` 디렉토리를 의미한다.

## Server
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
        │   └─── ...
        │
        └─── resouces
            │   application.properties
            │
            └─── db
                │
                └─── ...
```

##### 서버 시작 전 해야할 일
1. MariaDB 설치

MariaDB를 제외한 다른 DB를 사용해도 되지만 그렇게 하면 서버 설정을 바꿔야한다.  
https://mariadb.org/download/#mariadb-repositories MariaDB 홈페이지에서 MariaDB Repositories를 이용하여 MariaDB를 설치한다.

예시)  
Choose a distribution: `18.04 LTS "bionic"`  
Choose a MariaDB Server version: `10.5 [Stable]`  
Mirror: `Yongbok.net - South Korea`  
※최신버전을 설치하지 않으면 에러난다.


2. MariaDB 설정

콘솔창에 아래와 같이 입력하여 MariaDB 콘솔에 접속한다.
```
$ sudo mysql -uroot -pYYYY
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
`xxx.xxx.xxx.xxx`에 DB에 접속할 IP를 입력한다.(잘 모르겠다면 localhost를 입력한다.)  
`user_password`에 원하는 비밀번호를 입력한다.

아래와 같은 커맨드로 MariaDB의 유저 목록을 볼 수 있다.
```
select host, user from mysql.user;
````

3. server 설정

`/server/src/main/resouces/application.properties` 파일의 6 ~ 8번 라인을 다음과 같이 수정한다.
```
spring.datasource.url=jdbc:mariadb://localhost:3306/db_name?serverTimezone=UTC&useMysqlMetadata=true
spring.datasource.username=user_name
spring.datasource.password=user_password
```
`db_name`, `user_name`, `user_password`를 위에서 설정한 값과 동일하게 설정한다.


4. maven 설치  
```
$ sudo apt install maven
```

##### 서버 구동
`/server` 디렉토리에서 `$ sudo mvn spring-boot:run`을 입력하여 서버를 구동한다.

## Client
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

##### 클라이언트 시작 전 해야할 일
1. Node.js 설치  
https://github.com/nodesource/distributions/blob/master/README.md#deb 을 참고하여 Node.js를 설치한다.

2. Ionic 설치  
https://ionicframework.com/docs/intro/cli 을 참고하여 Ionic을 설치한다.

3. node_module 설치
`/client` 디렉토리에서 `$ sudo npm install`을 입력하여 `node_module`을 설치한다.

##### 클라이언트 구동
`/client` 디렉토리에서 `$ ionic serve --external`을 입력하여 클라이언트를 구동한다.

## Testing
WebAuthn browser API는 `localhost`를 제외하고는 HTTPS를 통한 연결에서만 접근 가능하다.  휴대폰을 통해 테스트를 하기 위해서는 SSL을 이용하여 서버를 열어야 한다.  
이를 위한 여러가지 방법이 있지만 2가지 방법을 소개한다.

### 1.ngrok 이용
[ngrok](https://ngrok.com, "ngrok link")은 `localhost`와 ngrok 서버를 터널링하여 HTTPS URL로 웹 페이지를 이용할 수 있게 한다.  
ngrok 홈페이지에서 회원가입을 한 후 실행파일을 다운받아 터미널에서 다운받은 폴더로 이동한 뒤 `$ ngrok http -host-header=rewrite [client port]`를 실행한다.  
이 때 `[client port]`는 `ionic`을 실행한 포트이고 기본값은 8100이다.  

그리고 `/server/src/main/resources/application.properties` 파일의 14 ~ 17번 라인을 다음과 같이 수정한다.
```
app.relying-party-id=[your ngrok URL].ngrok.io
app.relying-party-name=Example Application
app.relying-party-icon=https://[your ngrok URL].ngrok.io/assets/logo.png
app.relying-party-origins=https://[your ngrok URL].ngrok.io
```
`[your ngrok URL]`은 ngrok을 실행시켰을 때 받은 URL주소에 있는 값이다.

### 2. SSL 인증서 받기
[Let's Encrypt](https://letsencrypt.org, "letsencrypt link")에서 SSL 인증서를 발급받아 사용하는 것이다. 이 방법을 사용하기 위해서는 사이트의 URL이 필요하다. URL을 발급받기 전인 테스트 단계라서 회사 인터넷인 `Kouno_GIGA_2.4`에 연결하고 iptime 공유기 관리 페이지에서 DDNS와 포트 포워딩을 설정하여 진행하였다.

1. DDNS 설정  
iptime 관리 페이지에서 `고급 설정 - 특수기능 - DDNS 설정` 메뉴에 들어간다. 호스트 이름과 사용자 ID를 입력하여 DDNS를 등록하면 호스트 이름이 `Kouno_GIGA_2.4` 공유기의 외부 아이피와 연결된다.

2. 포트 포워딩  
iptime 관리 페이지에서 `고급 설정 - NAT/라우터 관리 - 포트포워드 설정` 메뉴에 들어간다. 외부 포트 80(HTTP), 443(HTTPS)를 클라이언트를 구동시킬 컴퓨터의 내부 IP에서 `ionic`을 실행한 포트(기본값 8100)로 포워딩하는 규칙을 만든다.

3. certbot으로 SSL 인증서 발급  
[certbot](https://certbot.eff.org/, "certbot link")에서 "My HTTP website is running `None of the above` on `Ubuntu 18.04 LTS (bionic)`"을 선택하여 나오는 창에서 default 설정을 유지한 채로 안내에 따라 SSL 인증서를 발급 받는다.  
발급받은 인증서의 위치는 `/etc/letsencrypt/live/[your host name]`에 있다.(이 때의 루트 디렉토리 `/`은 운영체제의 루트 디렉토리이다. `[your host name]`은 DDNS를 설정한 호스트 이름이다.)  
발급받은 인증서중에서 `privkey.pem`과 `fullchain.pem` 파일을 `/client/ssl`로 옮긴다. 이 때 관리자 권한으로 옮겨야 한다.

4. `/client/angular.json` 파일 수정  
`/client/angular.json` 파일의 `["project"]["app"]["architect"]["serve"]["option"]`에 `"ssl": true, "sslKey": "./ssl/privkey.pem", "sslCert": "./ssl/fullchain.pem"`을 추가한다.

5. 클라이언트 구동  
SSL 인증서를 발급받고 클라이언트를 구동할 때는 `/client` 디렉토리에서 `$ sudo ionic serve --external --ssl`을 입력하여 클라이언트를 구동한다.

위 2가지 방법을 이용할 때 클라이언트를 구동하면 disable host error가 발생한다. 이를 피하기 위해 `/client/angular.json` 파일의 `["project"]["app"]["architect"]["serve"]["option"]`에 `"disableHostCheck": true`를 추가한다.  

이제 휴대폰으로 할당받은 ngrok 주소 또는 iptime에서 발급받은 주소에 접속하여 테스트를 진행하면 된다.
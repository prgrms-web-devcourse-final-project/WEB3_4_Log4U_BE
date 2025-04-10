## 개발 환경 설정

[AWS API SERVER](http://ec2-13-209-127-186.ap-northeast-2.compute.amazonaws.com:8080)

[카카오 로그인](http://ec2-13-209-127-186.ap-northeast-2.compute.amazonaws.com:8080/oauth2/authorization/kakao)

[네이버 로그인](http://ec2-13-209-127-186.ap-northeast-2.compute.amazonaws.com:8080/oauth2/authorization/naver)

[구글 로그인](http://ec2-13-209-127-186.ap-northeast-2.compute.amazonaws.com:8080/oauth2/authorization/google)

---

* 루트 디렉토리(WEB3_4_Log4U_BE)에서 다음 명령 실행
* 개발용 DB 컨테이너 실행

```
# postgresql mysql 모두 실행
docker-compose up -d

# postgresql 만 실행
docker-compose up -d postgres

```

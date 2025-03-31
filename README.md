## 개발 환경 설정

* 루트 디렉토리(WEB3_4_Log4U_BE)에서 다음 명령 실행


* 개발용 MYSQL 빌드

```
# 이미지 빌드
cd docker
docker build -t log4u-mysql .

# 최초 실행 1(볼륨 존재)
docker run -d --name log4u-mysql -p 3307:3306 -v {file}:/var/lib/mysql log4u-mysql

# 최초 실행 2(볼륨 없이)
docker run -d --name log4u-mysql -p 3307:3306  log4u-mysql

# 이미 존재할 경우 
docker start log4u-mysql

```

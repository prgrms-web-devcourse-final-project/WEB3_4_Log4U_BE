# MYSQL
FROM mysql:8.0.33

# 변수 설정
ENV TZ=Asia/Seoul \
    MYSQL_ROOT_PASSWORD=root \
    MYSQL_USER=dev \
    MYSQL_PASSWORD=devcos4-team08 \
    MYSQL_DATABASE=log4u

# 데이터 파일을 컨테이너에 연결
VOLUME ["/var/lib/mysql"]

# MySQL 3306 포트
EXPOSE 3306

# MYSQL 실행
CMD ["mysqld"]

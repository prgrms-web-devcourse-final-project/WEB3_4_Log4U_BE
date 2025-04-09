#!/bin/bash

PROJECT_ROOT="/home/ubuntu/app"
JAR_FILE="$PROJECT_ROOT/spring-log4u.jar"

APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

# Docker Compose로 postgres 실행
echo "$TIME_NOW > Docker Compose postgres 서비스 실행" >> $DEPLOY_LOG
docker-compose up -d postgres

# Docker Compose 서비스 확인
POSTGRES_STATUS=$(docker ps --filter "name=log4u_postgres" --format "{{.Status}}")
if [[ "$POSTGRES_STATUS" == *"Up"* ]]; then
    echo "$TIME_NOW > PostgreSQL 서비스가 성공적으로 실행되었습니다." >> $DEPLOY_LOG
else
    echo "$TIME_NOW > PostgreSQL 서비스 실행 실패!" >> $DEPLOY_LOG
    exit 1
fi

# build 파일 복사
echo "$TIME_NOW > $JAR_FILE 파일 복사" >> $DEPLOY_LOG
cp $PROJECT_ROOT/build/libs/Log4U-0.0.1-SNAPSHOT.jar $JAR_FILE

# jar 파일 실행
echo "$TIME_NOW > $JAR_FILE 파일 실행" >> $DEPLOY_LOG
nohup java -Dspring.profiles.active="prod, prod-secret" -jar $JAR_FILE > $APP_LOG 2> $ERROR_LOG &

CURRENT_PID=$(pgrep -f $JAR_FILE)
echo "$TIME_NOW > 실행된 프로세스 아이디 $CURRENT_PID 입니다." >> $DEPLOY_LOG

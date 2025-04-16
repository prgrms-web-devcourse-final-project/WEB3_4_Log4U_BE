# 1. OpenJDK 기반 이미지 사용
FROM eclipse-temurin:21-jdk

# 2. JAR 복사 (경로는 실제 빌드 결과물 위치에 따라 다름)
COPY build/libs/*.jar app.jar

# 3. 실행 명령
ENTRYPOINT ["java", "-jar", "/app.jar"]

plugins {
    java
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "6.0.1.5171"
    jacoco
    checkstyle
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
//    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("mysql:mysql-connector-java:8.0.33")

    implementation ("org.antlr:antlr4-runtime:4.10.1")


    // PostgreSQL + PostGIS
    implementation("org.postgresql:postgresql:42.7.3") // 최신 버전 확인
    implementation ("org.hibernate.orm:hibernate-spatial:6.2.7.Final") // 최신 Hibernate 6
    implementation("org.hibernate.orm:hibernate-core:6.2.7.Final")

    // Geometry 관련
    implementation ("org.locationtech.jts:jts-core:1.18.2")

    compileOnly("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    runtimeOnly("com.h2database:h2")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // JWT 관련
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.1")

    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // mysql
    runtimeOnly("com.mysql:mysql-connector-j")

    // AWS
    implementation(platform("software.amazon.awssdk:bom:2.31.11"))
    implementation("software.amazon.awssdk:s3")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
        csv.required = false
    }
}

checkstyle {
    configFile = file("${rootDir}/naver-checkstyle-rules.xml")
    configProperties["suppressionFile"] = "${rootDir}/naver-checkstyle-suppressions.xml"
    toolVersion = "9.2"
}

sonar {
    properties {
        property("sonar.projectKey", "prgrms-web-devcourse-final-project_WEB3_4_Log4U_BE")
        property("sonar.organization", "prgrms-web-devcourse-final-project")
        property("sonar.host.url", "https://sonarcloud.io")

        // Jacoco 리포트가 존재하는지 확인 후 적용
        val jacocoReportPath = file("build/reports/jacoco/test/jacocoTestReport.xml")
        if (jacocoReportPath.exists()) {
            property("sonar.coverage.jacoco.xmlReportPaths", jacocoReportPath.absolutePath)
        }

        // Checkstyle 리포트가 존재하는지 확인 후 적용
        val checkstyleReportPath = file("build/reports/checkstyle/main.xml")
        if (checkstyleReportPath.exists()) {
            property("sonar.java.checkstyle.reportPaths", checkstyleReportPath.absolutePath)
        }

        // 환경 변수 `BRANCH_NAME`이 없을 경우 "main"으로 설정
        val branchName = System.getenv("BRANCH_NAME") ?: "main"
        property("sonar.branch.name", branchName)
    }
}
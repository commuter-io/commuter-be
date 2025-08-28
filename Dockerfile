# OpenJDK 17 slim 이미지 사용 (프로젝트에 맞게 조정)
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# gradle 빌드 후 생성된 JAR 파일 복사
COPY build/libs/backend-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]


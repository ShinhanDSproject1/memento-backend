# 베이스 이미지로 Java 17 버전을 사용
FROM openjdk:17-jdk-slim

# apt-get 패키지 관리자를 업데이트하고 curl을 설치
RUN apt-get update && apt-get install -y curl

# JAR 파일이 위치할 경로를 변수로 지정
ARG JAR_FILE=build/libs/*.jar

# 위에서 지정한 JAR 파일을 app.jar 라는 이름으로 Docker 이미지 안으로 복사
COPY ${JAR_FILE} app.jar

# 컨테이너가 시작될 때 실행될 명령어
ENTRYPOINT ["java","-jar","/app.jar"]
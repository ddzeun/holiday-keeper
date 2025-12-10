#!/bin/bash

APP_NAME="holidaykeeper-app"

echo "-=-=-=-= 🚀 로컬 개발 환경(H2)을 빌드하고 실행합니다 🚀 -=-=-=-="

echo "1️⃣ 기존 컨테이너 종료 및 삭제"
docker-compose down

echo "2️⃣ 프로젝트 빌드 (JAR 파일 생성)"
./gradlew clean build -x test

echo "3️⃣ JAR 파일 복사"
JAR_FILE=$(find build/libs -name "*-SNAPSHOT.jar" ! -name "*-plain.jar")
cp $JAR_FILE ./app.jar

echo "4️⃣ Docker Compose로 서비스 시작"
docker-compose up -d --build

echo ""
echo "✅ 서비스가 시작되었습니다!"
echo "👀 실행중인 컨테이너 확인:"
docker ps
echo ""
echo "📜 애플리케이션 로그를 실시간으로 확인합니다 (Ctrl+C로 종료):"
docker logs -f $APP_NAME
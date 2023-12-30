#!/bin/bash
BUILD_JAR=$(ls /home/ec2-user/app/build/libs/*.jar)
JAR_NAME=$(basename $BUILD_JAR)
DEPLOY_LOG=/home/ec2-user/deploy.log
DEPLOY_ERR_LOG=/home/ec2-user/deploy_err.log

echo "> build 파일 복사" >> DEPLOY_LOG
DEPLOY_PATH=/home/ec2-user/
cp $BUILD_JAR $DEPLOY_PATH

echo "> 실행 중인 애플리케이션 pid 확인" >> DEPLOY_LOG
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 실행 중인 애플리케이션이 없으므로 종료하지 않음" >> DEPLOY_LOG
else
  echo "> kill -15 $CURRENT_PID" >> DEPLOY_LOG
  kill -15 $CURRENT_PID
  sleep 10
fi

echo "> 환경 변수 설정" >> DEPLOY_LOG
source ~/.bashrc

DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo "> 새로운 애플리케이션 배포" >> DEPLOY_LOG
nohup java -jar $DEPLOY_JAR >> DEPLOY_LOG 2>DEPLOY_ERR_LOG &
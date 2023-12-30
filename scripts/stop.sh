#!/bin/bash
BUILD_JAR=$(ls /home/ec2-user/safe-wallet/build/libs/*.jar)
JAR_NAME=$(basename $BUILD_JAR)
DEPLOY_LOG=/home/ec2-user/deploy.log

echo "> 실행 중인 애플리케이션 pid 확인" >> $DEPLOY_LOG
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 실행 중인 애플리케이션이 없으므로 종료하지 않음" >> $DEPLOY_LOG
else
  echo "> kill -15 $CURRENT_PID" >> $DEPLOY_LOG
  kill -15 $CURRENT_PID
  sleep 10
fi
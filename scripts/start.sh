#!/bin/bash
BUILD_JAR=$(ls /home/ec2-user/safe-wallet/build/libs/*.jar)
JAR_NAME=$(basename $BUILD_JAR)
DEPLOY_PATH=/home/ec2-user/
DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
DEPLOY_LOG=/home/ec2-user/deploy.log
DEPLOY_ERR_LOG=/home/ec2-user/deploy_err.log

echo "> build 파일 복사" >> $DEPLOY_LOG
cp $BUILD_JAR $DEPLOY_PATH

echo "> 환경 변수 설정" >> $DEPLOY_LOG
source ~/.bashrc

echo "> 새로운 애플리케이션 배포" >> $DEPLOY_LOG
nohup java -jar $DEPLOY_JAR >> $DEPLOY_LOG 2>$DEPLOY_ERR_LOG &
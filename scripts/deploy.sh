REPOSITORY=/home/ubuntu/app
cd $REPOSITORY

APP_NAME=blabla
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep '.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

CURRENT_DEV_PID=$(lsof -ti:8080)
CURRENT_PROD_PID=$(lsof -ti:8081)

if [ -z $CURRENT_DEV_PID ]
then
  echo "> 현재 구동중인 [ dev ]용 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_DEV_PID"
  kill -15 $CURRENT_DEV_PID
  sleep 5
fi

if [ -z $CURRENT_PROD_PID ]
then
  echo "> 현재 구동중인 [ prod ]용 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PROD_PID"
  kill -15 $CURRENT_PROD_PID
  sleep 5
fi

echo "> $JAR_PATH 배포 - dev"
nohup java -jar \
      -Dspring.profiles.active=dev \
      build/libs/$JAR_NAME \
      > $REPOSITORY/nohup.out 2>&1 &
      
echo "> $JAR_PATH 배포 - prod"
nohup java -jar \
      -Dspring.profiles.active=prod \
      build/libs/$JAR_NAME \
      > $REPOSITORY/nohup.out 2>&1 &

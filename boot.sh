#!/bin/bash
sudo docker stop $(sudo docker ps -aq)
sudo docker rm $(sudo docker ps -aq)
sudo docker rmi temporaryfilestorage_receiver_to_gdrive
sudo docker rmi temporaryfilestorage_kafka_sender
sudo docker rmi temporaryfilestorage_kafka_receiver
./copy_properties.sh

cd ReceiverToGDrive
./gradlew build
cd ..

cd KafkaSender
./gradlew build
cd ..

cd KafkaReceiver
./gradlew build
cd ..

sudo docker-compose up

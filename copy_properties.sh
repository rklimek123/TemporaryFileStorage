#!/bin/bash

for direc in */
do
	mkdir -p ${direc}src/main/resources
	cp ./application.properties ${direc}src/main/resources/application.properties
done

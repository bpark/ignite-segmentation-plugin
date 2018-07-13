#!/usr/bin/env bash

cd /var/workspace

sudo docker build -t ignite-custom .

sudo /usr/local/bin/docker-compose up -d

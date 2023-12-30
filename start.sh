#!/bin/sh

echo 'starting containers...'

#docker network create dockernetwork

docker run -d --name some-postgres --network=dockernetwork -p 5432:5432 -e POSTGRES_USER=vil_app -e POSTGRES_PASSWORD=secret -e POSTGRES_DB=voornameninliedjes postgres:15.4-bookworm
docker run -d --name logstash --network=dockernetwork -p 5000:5000 -v ./logstash/logstash.conf:/usr/share/logstash/pipeline/logstash.conf docker.elastic.co/logstash/logstash:8.11.3
#docker run -d --name elasticsearch --network=dockernetwork -p 9200:9200 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:8.11.3
docker run -d --name kibana --network=dockernetwork --link elasticsearch -p 5601:5601 docker.elastic.co/kibana/kibana:8.11.3

echo 'containers started...'


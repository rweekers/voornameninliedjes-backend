#!/bin/sh

echo 'starting containers...'

docker run -d --name some-postgres -p 5432:5432 -e POSTGRES_USER=vil_app -e POSTGRES_PASSWORD=secret -e POSTGRES_DB=voornameninliedjes postgres:15.4-bookworm
docker run -d --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.10.0
docker run -d --name kibana --link elasticsearch -p 5601:5601 docker.elastic.co/kibana/kibana:7.10.0
docker run -d --name logstash -p 5000:5000 -v ./logstash/logstash.conf:/usr/share/logstash/pipeline/logstash.conf docker.elastic.co/logstash/logstash:7.15.0

echo 'containers started...'


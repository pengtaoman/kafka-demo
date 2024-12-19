################################## kafka ###############

https://github.com/conduktor/kafka-stack-docker-compose
docker-compose -f ./zk-single-kafka-single.yml up -d

https://github.com/provectus/kafka-ui
https://github.com/obsidiandynamics/kafdrop
docker run -d --rm -p 9010:9000 \
    -e KAFKA_BROKERCONNECT=localhost:9092 \
    -e SERVER_SERVLET_CONTEXTPATH="/" \
    obsidiandynamics/kafdrop

kafka-topics --create --topic my-topic --bootstrap-server 10.211.55.71:9092 --partitions 3 --replication-factor 2

#查看 Topic 的各分区的 Offset
kafka-run-class kafka.tools.GetOffsetShell --broker-list 127.0.0.1:9092 --topic my-topic --time -1

kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --list

kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --describe --group <consumer_group>


kafka-topics --bootstrap-server 127.0.0.1:9092  --topic my-topic --delete

#查看主题信息
kafka-topics --bootstrap-server 127.0.0.1:9092 --describe --topic my-topic

[appuser@kafka1 ~]$ kafka-topics --bootstrap-server 127.0.0.1:9092 --describe --topic my-topic
Topic: my-topic    TopicId: sI8BWWGdTI2aY2qCnt-DZw    PartitionCount: 3    ReplicationFactor: 2    Configs:
    Topic: my-topic    Partition: 0    Leader: 2    Replicas: 2,1    Isr: 2,1
    Topic: my-topic    Partition: 1    Leader: 1    Replicas: 1,2    Isr: 1,2
    Topic: my-topic    Partition: 2    Leader: 2    Replicas: 2,1    Isr: 2,1

docker run -d  \
  -p 9092:9092 \
  -p 9093:9093 \
  --name broker \
  -e KAFKA_NODE_ID=1 \
  -e KAFKA_PROCESS_ROLES=broker,controller \
  -e KAFKA_LISTENERS=PLAINTEXT://10.211.55.71:9092,CONTROLLER://10.211.55.71:9093 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://10.211.55.71:9092 \
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT \
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@10.211.55.71:9093 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
  -e KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS=0 \
  -e KAFKA_NUM_PARTITIONS=3 \
  apache/kafka:3.7.2

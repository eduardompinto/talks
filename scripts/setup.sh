#!/bin/sh

echo "Waiting for localstack to be ready"
sleep 20

echo "---------------------------"

echo "Setting up SQS Queues"


aws sqs create-queue --endpoint-url=http://localstack:4576 --queue-name test-queue

echo "Setting up kinesis"

aws kinesis create-stream --endpoint-url=http://localstack:4568 --shard-count 1 --stream-name test-stream

echo "---------------------------"

echo "AWS Resources created on Localstack"

#!/bin/sh

echo "Waiting for localstack to be ready"
sleep 2

echo "---------------------------"

echo "Setting up SQS Queues"

until aws --endpoint-url=http://localstack:4576 sqs list-queues; do
	>&2 echo "SQS is unavailable - sleeping"
		sleep 2
done

aws sqs create-queue --endpoint-url=http://localstack:4576 --queue-name test-queue

echo "---------------------------"

echo "AWS Resources created on Localstack"

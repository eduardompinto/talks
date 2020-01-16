docker run --rm --network=talks_aws \
-e AWS_ACCESS_KEY_ID=foo \
-e AWS_SECRET_ACCESS_KEY=bar \
-e AWS_DEFAULT_REGION=eu-central-1 \
mesosphere/aws-cli:1.14.5 kinesis --endpoint-url=http://localstack:4568 put-record \
--stream-name test-stream \
--data sampledatarecord \
--partition-key samplepartitionkey

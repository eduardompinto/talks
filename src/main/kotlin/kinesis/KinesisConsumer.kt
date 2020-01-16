package kinesis

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.kinesis.AmazonKinesis
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.ShutdownReason
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker
import com.amazonaws.services.kinesis.metrics.interfaces.MetricsLevel
import com.amazonaws.services.kinesis.model.Record


val credentials: AWSCredentialsProvider =
    AWSStaticCredentialsProvider(BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY))

fun getWorker(): Worker {
    val config = KinesisClientLibConfiguration("app_name", STREAM_NAME, credentials, "worker_id")
        .withRegionName(AWS_REGION)
        .withMetricsLevel(MetricsLevel.NONE)
        .withInitialPositionInStream(InitialPositionInStream.TRIM_HORIZON)

    return Worker.Builder()
        .recordProcessorFactory(getRecordProcessorFactory()).config(config)
        .kinesisClient(getKinesisClient())
        .dynamoDBClient(getDynamoClient())
        .build()
}

private fun getRecordProcessorFactory(): IRecordProcessorFactory {
    return IRecordProcessorFactory {
        object : IRecordProcessor {
            override fun shutdown(checkpointer: IRecordProcessorCheckpointer, reason: ShutdownReason) {

            }

            override fun initialize(shardId: String) {
            }

            override fun processRecords(records: List<Record>, checkpointer: IRecordProcessorCheckpointer) {
                records.forEach {
                    println(String(it.data.array()))
                }
            }
        }
    }
}

private fun getDynamoClient(): AmazonDynamoDB {
    return AmazonDynamoDBClientBuilder.standard().withCredentials(credentials).withEndpointConfiguration(
        AwsClientBuilder.EndpointConfiguration("http://localhost:4569", AWS_REGION)
    ).build()
}

private fun getKinesisClient(): AmazonKinesis {
    return AmazonKinesisClientBuilder.standard().withCredentials(credentials).withEndpointConfiguration(
        AwsClientBuilder.EndpointConfiguration("http://localhost:4568", AWS_REGION)
    ).build()
}

fun main() {
    // Important! You have to add: AWS_CBOR_DISABLE=true to your enviroment variables
    // https://github.com/localstack/localstack/issues/592
    getWorker().run()
}
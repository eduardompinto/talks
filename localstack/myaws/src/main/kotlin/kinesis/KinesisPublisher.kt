package kinesis

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.kinesis.producer.KinesisProducer
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration
import java.nio.ByteBuffer
import java.util.*


class KinesisPublisher {

    private val kinesis = KinesisProducer(
        KinesisProducerConfiguration()
            .setKinesisEndpoint(HOST)
            .setKinesisPort(PORT)
            .setCredentialsProvider(AWSStaticCredentialsProvider(BasicAWSCredentials(
                AWS_ACCESS_KEY,
                AWS_SECRET_KEY
            )))
            .setVerifyCertificate(false)
            .setRegion(AWS_REGION)
    )


    fun publish(entries: List<String>) = entries.map { entry: String ->
        print("Publishing $entry \n")
        val pr = kinesis.addUserRecord(
            STREAM_NAME,
            PARTITION_KEY,
            ByteBuffer.wrap(entry.toByteArray())
        )
        while (!pr.isDone) {

        }
        pr.get().attempts.forEach {
            print("${it.errorMessage} \n")
        }
    }

}

fun main() {
    KinesisPublisher().publish(entries = (1..200).map { "${UUID.randomUUID()}" })
}
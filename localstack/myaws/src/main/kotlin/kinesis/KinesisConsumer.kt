package kinesis

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput
import com.amazonaws.services.kinesis.metrics.impl.NullMetricsFactory
import java.io.FileWriter
import java.util.*
import java.util.concurrent.CompletableFuture

fun main() {
    val credentialsProvider = DefaultAWSCredentialsProviderChain()

    val consumerConfig = KinesisClientLibConfiguration("APP_NAME",
        STREAM_NAME, credentialsProvider, "WORKER_ID")
        .withRegionName(AWS_REGION)
        .withKinesisEndpoint(HOST)
        .withInitialPositionInStream(InitialPositionInStream.TRIM_HORIZON)
    val recordProcessor = object : IRecordProcessor {
        override fun shutdown(shutdownInput: ShutdownInput?) {
        }

        override fun initialize(initializationInput: InitializationInput?) {
        }

        override fun processRecords(processRecordsInput: ProcessRecordsInput?) {
            processRecordsInput?.records?.forEach { record ->
                FileWriter(UUID.randomUUID().toString()).use { fw ->
                    fw.write(record.data.toString())
                }
            } ?: throw RuntimeException("No process input")
        }
    }
    val factory = IRecordProcessorFactory {
        recordProcessor
    }
    val worker = Worker.Builder()
        .recordProcessorFactory(factory)
        .config(consumerConfig)
        .metricsFactory(NullMetricsFactory())
        .build()
        CompletableFuture.runAsync(worker)
    while (true) {}
}


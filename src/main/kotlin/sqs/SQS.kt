package sqs

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import com.amazonaws.services.sqs.model.DeleteMessageResult
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.PurgeQueueRequest
import com.amazonaws.services.sqs.model.PurgeQueueResult
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.amazonaws.services.sqs.model.SendMessageRequest
import com.amazonaws.services.sqs.model.SendMessageResult


class SQS(private val queueUrl: String) {

    private val sqs = AmazonSQSClientBuilder.standard()
        .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials("foo", "bar")))
        .withRegion(Regions.EU_CENTRAL_1)
        .build()!!

    fun sendMessage(body: String): SendMessageResult {
        val req = SendMessageRequest()
            .withQueueUrl(queueUrl)
            .withMessageBody(body)
        return sqs.sendMessage(req)
    }

    fun getMessages(n: Int): List<Message> {
        val maxMessages = when (n) {
            in 1..10 -> n
            else -> 10 // SQS only return a maximum of 10 messages
        }
        val request = ReceiveMessageRequest()
            .withMaxNumberOfMessages(maxMessages)
            .withQueueUrl(queueUrl)
        return sqs.receiveMessage(request).messages
    }

    fun delete(m: Message): DeleteMessageResult {
        return sqs.deleteMessage(queueUrl, m.receiptHandle)
    }

    fun purge(): PurgeQueueResult {
        return sqs.purgeQueue(PurgeQueueRequest(queueUrl))
    }

}

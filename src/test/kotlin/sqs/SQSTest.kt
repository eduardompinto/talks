package sqs

import com.amazonaws.services.sqs.model.AmazonSQSException
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.QueueDoesNotExistException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

@DisplayName("Test SQS API")
class SQSTest {

    @Nested
    @DisplayName("Given a valid queue")
    inner class TestValidQueue {

        private val sqs = SQS("http://localhost:4576/queue/test-queue")

        @BeforeEach
        fun purgeQueue() {
            sqs.purge()
        }

        @Test
        @DisplayName("when sends a message it should receive a messageID and an md5 of the message body")
        fun testSendMessage() {
            val message = "Happy case :)"
            val r = sqs.sendMessage(message)
            assertTrue(r.messageId.isNotBlank())
            assertTrue(r.mD5OfMessageBody.isNotBlank())
        }

        @Test
        @DisplayName("when purge it should receive a 2XX response")
        fun testPurge() {
            val r = sqs.purge()
            assertTrue(r.sdkHttpMetadata.httpStatusCode in 200..299)
        }

        @Test
        @DisplayName("when delete a valid message it should receive a 2XX response")
        fun testDelete() {
            sqs.purge()
            sqs.sendMessage("m")
            val message = sqs.getMessages(1).first()
            val r = sqs.delete(message)
            assertTrue(r.sdkHttpMetadata.httpStatusCode in 200..299)
            sqs.delete(message)
        }


        @Test
        @DisplayName("when delete a not existing message it should throw an AmazonSQSException")
        fun testDeleteNotExistingMessage() {
            runCatching {
                sqs.delete(Message())
            }.onSuccess {
                fail("Expecting an AmazonSQSException, but it was a successful call")
            }.onFailure { e ->
                when (e) {
                    is AmazonSQSException -> assertEquals(400, e.statusCode)
                    else -> fail("Expecting an AmazonSQSException, but got ${e.javaClass}")
                }
            }
        }

        @Test
        @DisplayName("when request 5 messages should bring 5 messages")
        fun testGetMessages() {
            val sentMessages = (1..5).map {
                val message = "$it"
                message to sqs.sendMessage(message).messageId
            }

            val receivedMessages = sqs.getMessages(5).map { it.body to it.messageId }
            assertEquals(sentMessages, receivedMessages)
        }
    }

    @Nested
    @DisplayName("Given a not existing queue")
    inner class TestNotExistingQueue {

        private val sqs = SQS("http://localhost:4576/queue/not-existing-queue")

        @Test
        @DisplayName("when tries to send a message it should throw a QueueDoesNotExistException")
        fun testSendMessage() {
            assertThrows<QueueDoesNotExistException> { sqs.sendMessage("message") }
        }

        @Test
        @DisplayName("when tries to purge it should throw a QueueDoesNotExistException")
        fun testPurge() {
            assertThrows<QueueDoesNotExistException> { sqs.purge() }
        }

        @Test
        @DisplayName("when tries to delete a message it should throw a QueueDoesNotExistException")
        fun testDelete() {
            assertThrows<QueueDoesNotExistException> { sqs.delete(Message()) }
        }

        @Test
        @DisplayName("when tries to get messages it should throw a QueueDoesNotExistException")
        fun testGetMessages() {
            assertThrows<QueueDoesNotExistException> { sqs.getMessages(1) }
        }
    }

}
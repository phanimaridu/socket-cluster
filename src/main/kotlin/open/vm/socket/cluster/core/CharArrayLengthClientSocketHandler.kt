package open.vm.socket.cluster.core

import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.Charset

class CharArrayLengthClientSocketHandler(val id : String, val delay : Long) : ClientSocketHandler {

    companion object {
        val HEADER_SIZE = 4;
        val LENGTH_PATTERN = "%0${HEADER_SIZE}d"
        val DEFAULT_CHARSET = Charset.forName("ISO-8859-1")
        val END_OF_TEXT_CHAR = '\u0003'
        private val LOGGER = LoggerFactory.getLogger(CharArrayLengthClientSocketHandler::class.java)
    }

    override fun handle(clientSocket: Socket) {
        InputStreamReader(clientSocket.getInputStream() , DEFAULT_CHARSET).use { reader ->
            OutputStreamWriter(clientSocket.getOutputStream(), DEFAULT_CHARSET).use { writer ->
                while (true){
                    val messageLength =  Integer.parseInt(String(readChars(reader, HEADER_SIZE)))
                    var payload = String(readChars(reader, messageLength - HEADER_SIZE))
                    LOGGER.debug("Received payload from client; clientSocketId=$id payload=$payload")
                    var replyPayload = "ACK"
                    Thread.sleep(delay)
                    writer.write(prepareMessage(replyPayload))
                    writer.flush()
                    LOGGER.debug("Sent reply to client; clientSocketId=$id replyPayload=$replyPayload")
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun readChars(reader: InputStreamReader, length: Int): CharArray {
        val chars = CharArray(length)
        val charsRead = reader.read(chars)
        if (charsRead == -1) throw IOException("Connection closed")
        return chars
    }

    private fun prepareMessage(payload: String): String {
        val payloadToWrite = payload + END_OF_TEXT_CHAR
        val header = String.format(LENGTH_PATTERN, payloadToWrite.length + HEADER_SIZE)
        return header + payloadToWrite
    }

}
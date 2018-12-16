package open.vm.socket.cluster.utils

import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.Charset

class TCPClient(private val host: String, private val port: Int) {

    companion object {
        private val END_OF_TEXT_CHAR = '\u0003'
        private val LENGTH_SIZE = 6
        private val LENGTH_PATTERN = "%0" + LENGTH_SIZE + "d"
        private val DEFAULT_CHARSET = Charset.forName("ISO-8859-1")
    }

    private var socket: Socket? = null
    private var reader: InputStreamReader? = null
    private var writer: OutputStreamWriter? = null

    @Throws(IOException::class)
    fun connect() {
        socket = Socket(host, port)
        socket?.keepAlive = true
        reader = InputStreamReader(socket!!.getInputStream(), DEFAULT_CHARSET)
        writer = OutputStreamWriter(socket!!.getOutputStream(), DEFAULT_CHARSET)
    }

    @Throws(IOException::class)
    fun disconnect() {
        reader?.close()
        writer?.close()
        reader = null
        writer = null
        socket = null
    }

    @Throws(IOException::class)
    @JvmOverloads
    fun ping(message: String = "PING"): String {
        if (socket != null) {
            write(message)
            return read()
        } else {
            throw IOException("Socket is null")
        }
    }


    @Throws(IOException::class)
    fun read(): String {
        val headerChars = readChars(reader!!, LENGTH_SIZE)
        val payloadLength = Integer.parseInt(String(headerChars)) - LENGTH_SIZE
        val payloadChars = readChars(reader!!, payloadLength)
        return String(payloadChars)
    }

    @Throws(IOException::class)
    fun write(payload: String) {
        writer!!.write(prepareMessage(payload))
        writer!!.flush()
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
        val header = String.format(LENGTH_PATTERN, payloadToWrite.length + LENGTH_SIZE)
        return header + payloadToWrite
    }

}

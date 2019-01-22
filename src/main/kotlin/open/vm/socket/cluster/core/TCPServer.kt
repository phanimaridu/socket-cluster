package open.vm.socket.cluster.core

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

//FIXME: Add maxConnections limit
//TODO: Switch to NIO Implementation
open class TCPServer(
        val port: Int,
        val handler: ClientSocketHandler,
        val meterRegistry : MeterRegistry,
        val name: String,
        val backlog: Int,
        val bindAddress: String,
        val maxConnections: Int
) {

    constructor(port: Int, handler: ClientSocketHandler,meterRegistry : MeterRegistry, name: String?,
                backlog: Int?, bindAddress: String?, maxConnections: Int?
    ) : this(
            port, handler, meterRegistry,
            name ?: "TS_$port",
            backlog ?: 50,
            bindAddress ?: "localhost",
            maxConnections ?: 50
    )

    //TODO:How to kotlin specific logger
    companion object {
        private var LOGGER = LoggerFactory.getLogger(TCPServer::class.java)
    }

    private val isLive = AtomicBoolean(false)
    private val serverSocket = ServerSocket(port, backlog, InetAddress.getByName(bindAddress))
    val liveSocketMap = ConcurrentHashMap<String, Socket>(maxConnections)


    @Throws(IOException::class)
    fun listen() {
        isLive.set(true)

        val serverWork = {
            while (isLive.get()) {
                //blocking op; waits for connections from client
                val clientSocket = serverSocket.accept()
                handleClientSocket(clientSocket)
            }
        }

        val serverThread = Thread(serverWork)
        serverThread.name = "${name}_THREAD"
        LOGGER.info("Starting TCP Server; port=$port")
        serverThread.start()
        LOGGER.info("TCP Server started successfully; port=$port")
    }

    @Throws(IOException::class)
    fun shutdown() {
        isLive.set(false)
        liveSocketMap.values.forEach{it.close()}
        serverSocket.close()
    }

    @Timed
    @Throws(IOException::class)
    private fun handleClientSocket(clientSocket: Socket) {
        val timestamp = Instant.now().epochSecond
        val clientSocketId = "${name}_CP_${clientSocket.port}_TS_$timestamp"

        val clientSocketWork = {
            var timer = Timer.start(meterRegistry)
            LOGGER.info("Created new client socket; clientSocketId=$clientSocketId")
            liveSocketMap[clientSocketId] = clientSocket

            try {
                handler.handle(clientSocket)
            }catch (e: Exception) {
                LOGGER.warn("Error(${e.message}) while accessing clientSocket; clientSocketId=$clientSocketId", e)
            } finally {
                clientSocket.close()
                liveSocketMap.remove(clientSocketId)
                LOGGER.info("Client socket closed; clientSocketId=$clientSocketId")
                timer.stop(meterRegistry.timer("udm.tcp.connection.requests", "name", name));
            }
        }

        val clientSocketThread = Thread(clientSocketWork)
        clientSocketThread.name = "${clientSocket.port}_THREAD"
        clientSocketThread.start()
    }

}
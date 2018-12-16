package open.vm.socket.cluster.core

import java.net.Socket

interface ClientSocketHandler {
    fun handle(clientSocket : Socket)
}
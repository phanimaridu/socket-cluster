package open.vm.socket.cluster

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableAutoConfiguration
@EnableAdminServer
class SocketServerApplication

fun main(args: Array<String>) {
    runApplication<SocketServerApplication>(*args)
}

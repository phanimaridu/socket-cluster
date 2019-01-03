package open.vm.socket.cluster

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableAutoConfiguration
class SocketServerApplication

fun main(args: Array<String>) {
    runApplication<SocketServerApplication>(*args)
}

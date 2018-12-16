package open.vm.socket.cluster.service

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import open.vm.socket.cluster.repository.ServerConfigRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.function.ToDoubleFunction

@Service
class MetricsService @Autowired constructor(
        private val registry : MeterRegistry,
        private val serverConfigRepository: ServerConfigRepository,
        private  val tcpInstanceService: TCPInstanceService) {

    private val USER_DEFINED_METRICS_PREFIX = "udm"

    init {
        serverConfigRepository.getServerConfigs().forEach { serverConfig ->
            var valueFunction = ToDoubleFunction<TCPInstanceService>{ service ->
                service.getLiveConnectionSize(serverConfig.name).toDouble()
            }
            registry.gauge("$USER_DEFINED_METRICS_PREFIX.tcp.connections", Tags.of("name", serverConfig.name), tcpInstanceService, valueFunction)
        }
    }
}
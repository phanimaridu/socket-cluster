package open.vm.socket.cluster.controller

import open.vm.socket.cluster.model.ServerConfigModel
import open.vm.socket.cluster.repository.ServerConfigRepository
import open.vm.socket.cluster.service.TCPInstanceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/servers")
class ServerController @Autowired constructor(
        private val serverConfigRepository : ServerConfigRepository,
        private val tcpInstanceService : TCPInstanceService
       ) {

    @GetMapping
    fun getServerConfigs() : Collection<ServerConfigModel> {
        return serverConfigRepository.getServerConfigs()
    }

    @GetMapping("{name}")
    fun getServerConfigByName(@PathVariable("name") name: String) : ServerConfigModel {
        return serverConfigRepository.getServerConfig(name)
    }

    @PostMapping("{name}/instance")
    @ResponseStatus(HttpStatus.CREATED)
    fun createInstance(@PathVariable("name") name: String) {
        tcpInstanceService.createInstance(name)
    }

    @DeleteMapping("{name}/instance")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun deleteInsatnce(@PathVariable("name") name: String) {
        tcpInstanceService.deleteInstance(name)
    }

    @GetMapping("{name}/metrics")
    fun getMetrics(@PathVariable("name") name: String) : Map<String,String> {
        return tcpInstanceService.getLiveConnectionMap(name)
    }

    @GetMapping("/metrics")
    fun getAllMetrics() : Map<String,Map<String,String>> {
        return tcpInstanceService.getLiveConnectionMaps()
    }

}
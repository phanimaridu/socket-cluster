package open.vm.socket.cluster.service

import open.vm.socket.cluster.core.SimpleClientSocketHandler
import open.vm.socket.cluster.core.TCPServer
import open.vm.socket.cluster.repository.ServerConfigRepository
import open.vm.socket.cluster.utils.Util.Companion.getOrThrowNotFound
import open.vm.socket.cluster.utils.Util.Companion.throwIfExists
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TCPInstanceService @Autowired constructor(
        private val serverConfigRepository : ServerConfigRepository
){
    private var instanceMap : MutableMap<String, TCPServer?> = LinkedHashMap()

    @Value("\${udps.sim.delay}") var delay : Long = 200

    fun createInstance(name : String){
        var serverConfig = serverConfigRepository.getServerConfig(name)
        throwIfExists("TCPServer", name, instanceMap[name])
        var server = TCPServer(serverConfig.port, SimpleClientSocketHandler(name, delay), serverConfig.name, serverConfig.backlog, serverConfig.binding, serverConfig.maxConnections)
        server.listen()
        synchronized(instanceMap){
            instanceMap.put(server.name, server)
        }
    }

    fun deleteInstance(name : String){
        var instance = getOrThrowNotFound("TCPServer", name, instanceMap.get(name))
        instance.shutdown()
        synchronized(instanceMap){
            instanceMap.remove(name)
        }
    }

    fun getLiveConnectionMap(name : String) : Map<String, String> {
        var instance = getOrThrowNotFound("TCPServer", name, instanceMap.get(name))
        var metrics = LinkedHashMap<String,String>()
        metrics.put("liveConnectionCount", "${instance.liveSocketMap.size}")
        return metrics;
    }

    fun getLiveConnectionSize(name : String) :Int {
        return instanceMap.get(name)?.liveSocketMap?.size ?: -1
    }


    fun getLiveConnectionMaps() : Map<String,Map<String, String>> {
        var metrics = LinkedHashMap<String,Map<String, String>>()
        instanceMap.keys.forEach { key -> metrics.put(key, getLiveConnectionMap(key)) }
        return metrics
    }

    fun getLiveConnectionCounts() : Map<String,Int> {
        var metrics = LinkedHashMap<String,Int>()
        serverConfigRepository.getServerConfigs().forEach{ serverConfig ->
            var tcpServer = instanceMap.get(serverConfig.name)
            if(tcpServer != null){
                metrics.put(serverConfig.name, tcpServer.liveSocketMap.size)
            }else{
                metrics.put(serverConfig.name, -1)
            }
        }
        return metrics
    }

}
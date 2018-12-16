package open.vm.socket.cluster.repository

import com.fasterxml.jackson.databind.ObjectMapper
import open.vm.socket.cluster.model.ServerConfigModel
import open.vm.socket.cluster.utils.Util.Companion.getFileContent
import open.vm.socket.cluster.utils.Util.Companion.getOrThrowNotFound
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import org.springframework.util.ResourceUtils
import java.io.FileInputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Repository
class ServerConfigRepository @Autowired constructor(
        private val objectMapper: ObjectMapper,
        @Value("\${udps.serverConfigFilePath}") private var serverConfigFilePath: String
) {
    private var serverConfigMap: Map<String, ServerConfigModel> = getServerConfigMap(getServerConfigs(objectMapper, serverConfigFilePath))

    private fun getServerConfigs(objectMapper: ObjectMapper, serverConfigFilePath: String): List<ServerConfigModel> {
        var serverConfigs = getFileContent(serverConfigFilePath)
        val constructType = objectMapper.typeFactory.constructCollectionType(List::class.java, ServerConfigModel::class.java)
        return objectMapper.readValue(serverConfigs, constructType)
    }

    private fun getServerConfigMap(serverConfigs: List<ServerConfigModel>): Map<String, ServerConfigModel> {
        return serverConfigs.map { it.name to it }.toMap()
    }

    fun getServerConfigs(): Collection<ServerConfigModel> {
        return serverConfigMap.values
    }

    fun getServerConfig(name: String): ServerConfigModel {
        return getOrThrowNotFound("ServerConfig", name, serverConfigMap.get(name))
    }


}
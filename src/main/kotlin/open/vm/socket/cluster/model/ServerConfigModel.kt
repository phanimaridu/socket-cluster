package open.vm.socket.cluster.model

class ServerConfigModel(
        var type: String,
        var name: String,
        var port: Int,
        var binding: String?,
        var backlog: Int?,
        var maxConnections: Int?) {
}
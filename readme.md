### Into
 - A Skeleton for Creating, Managing & Monitoring  multiple TCP Servers.
 - TCP Configuration file can be passed using application.yml's serverConfigFilePath property
 - A Sample config file provided in resources. By default application uses this.
 - By default each client connection is handeled by separate thread. 
 - SimpleClientSocketHandler is provided as demo Client Socket Handler 
 - For Custom Socket Handling extend ClientSocketHandler Interface and configure it in TCPInstanceService
 
### Build & Run
~~~ sh
gradle clean build
java -jar build/libs/socket-cluster-0.0.1-SNAPSHOT.jar
~~~

### Managing TCP Instances
~~~ sh
# start tcp instance
curl -X POST http://localhost:8080/servers/{instalce_name}/instance

# stop tcp instance
curl -X DELETE http://localhost:8080/servers/{instalce_name}/instance

# get tcp cluster status
curl -X GET ttp://localhost:8080/servers/metrics
~~~

### Monitoring TCP Cluster
~~~ sh
# Use Spring boot Adming to monitor
http://localhost:8080/

# Prometheus metrics are connfigured to actuator 
# live connections per tcp instance can be obtained from udm..tcp.connections metric
~~~



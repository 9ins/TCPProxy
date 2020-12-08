# TCPProxy v1.0.0
---  
![title_image](./image/tcp.png)  

## Instroduction
TCPProxy is Open Source Project that can contributes to relay TCP connection or being role of Stalend-Alone / HA / Load-Balanced cluster Proxy Server.  
It is based on TCP Socket layer transportation which is usually used in most Application / Web / OS / Device of present day.  
The TCPProxy is written by Java but It can support most TCP connection of a Application written with any programming language.  
It's very working simple, instinctive, performanceful. Most of user can easily set configuration, deloying, execution.  
You can construct secure entry point proxy to connect spawn of servers with TCPProxy.  
Also can make Stand-Alone, HA or Load-Balanced Architecture with behinding your servers.  

## Structure
---  
![structure_image](./image/tcpproxy-structure.png)  
TCPProxy is run on JVM. It consist of proxy main frame, thread pool, sessions for services and configuration YAML config file.  
The main frame part performs to manage each Sessions and configuration. A session be charged to interact between clients and remotes(servers) is worked with a session mode specified in config.yml. e.g. STAND_ALONE, HIGI_AVAILABLE_FAIL_OVER, HIGI_AVAILABLE_FAIL_BACK, LOAD_BALANCE_ROUND_ROBIN, LOAD_BALANCE_SEPARATE_RATIO.  
Each sessions have a thread pool to process client's requests which is needed to interct with remotes(or servers).  
Actual transporting is processed at a channel in Session, Also it is worked as a thread with two way as send and receive channel.  

![structure1_image](./image/tcpproxy-structure1.png)  
TCPProxy can allow a lot of clients and can manage various kind of sessions.  
Various sessions is working and processing client requests concurrently. User can set a detail configuration in config.yml to manage or maintaining each sessions for performance.  

## Review config.yml
---
```yml
adminPassword: null
adminPort: 9292
adminUser: null
forbiddenRemote: [
  ]
proxyHost: localhost
sessionMapping:
  MySQL:
    allowedHosts: [127.0.0.1]
    threadPoolCoreSize: 10
    threadPoolMaxSize: 50
    threadPoolIdleSecond: 600
    threadPoolQueueSize: 30
    bufferSize: 0
    connectionTimeout: 3
    keepAlive: false
    proxyBindAddress: localhost
    proxyPort: 1212
    remoteHosts: [192.168.1.152:9022]
    sessionMode: STAND_ALONE
    standAloneRetry: 3
    loadBalanceRatio: 
    failedCircularRetry: 3
    soTimeout: 0
    tcpNoDelay: false
  Kafka:
    allowedHosts: [127.0.0.1]
    threadPoolCoreSize: 10
    threadPoolMaxSize: 50
    threadPoolIdleSecond: 600
    threadPoolQueueSize: 30
    bufferSize: 0
    connectionTimeout: 0
    keepAlive: false
    proxyBindAddress: localhost
    proxyPort: 1213
    remoteHosts: [192.168.1.152:8092]
    sessionMode: STAND_ALONE
    standAloneRetry: 3
    loadBalanceRatio: 
    failedCircularRetry: 3
    soTimeout: 0
    tcpNoDelay: false
  Oracle:
    allowedHosts: [127.0.0.1]
    threadPoolCoreSize: 10
    threadPoolMaxSize: 50
    threadPoolIdleSecond: 600
    threadPoolQueueSize: 30
    bufferSize: 0
    connectionTimeout: 3
    keepAlive: false
    proxyBindAddress: localhost
    proxyPort: 1214
    remoteHosts: [192.168.1.157:1522, 192.168.1.155:9090, 192.168.1.157:1521, 192.168.1.157:1521]
    sessionMode: LOAD_BALANCE_SEPARATE_RATIO
    standAloneRetry: 3
    loadBalanceRatio: 10:20:30:50
    failedCircularRetry: 6
    soTimeout: 0
    tcpNoDelay: false
```


## Configuration for each session mode
---  
![stand-alone_image](./image/stand-alone.png)  



![ha_image](./image/ha.png)  


![load-balanced_image](./image/load-balanced.png)  


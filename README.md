# ignite-segmentation-plugin
This is a simple segmentation plugin for Apache Ignite, as Apache Ignite does not provide any segmentation plugin implementation.

This plugin provides a basic segmentation implementation based on Java InetAddress isReachable implementation.

## Note
Please consider, that ```InetAddress.isReachable``` requires sudo permissions
to be executed correctly. Please test if the call is working in your environment before you use this resolver,
otherwise the segmentation test will always fail!

## Configuration

A sample configuration looks like this:

```xml
<bean id="flinkcache-cfg" class="org.apache.ignite.configuration.IgniteConfiguration">
  
  <property name="segmentationResolvers">
      <bean class="com.github.bpark.ignite.plugins.segmentation.ReachableSegmentationResolver">
          <property name="timeout" value="1000"/>
          <property name="networkInterface" value="eth0"/>
          <property name="consistency" value="QUORUM"/>
          <property name="topologyHosts">
              <list>
                  <value>node1</value>
                  <value>node2</value>
                  <value>node3</value>
                  <value>node4</value>
                  <value>node5</value>
              </list>
          </property>
      </bean>
  </property>
  
</bean>
```

| Parameter     | Description        | Default  |
| ------------- |---------------| ------|
| timeout       | The timeout in milliseconds after the connection test fails | 200 |
| ttl      | The maximum numbers of hops to try.      |   0 |
| network interface | Name of the network interface to use for the segmentation test      |    eth0 |
| consistency  | defines the number of reachable hosts to pass the segmentation test, possible values are ONE, QUORUM and ALL  |   QUORUM |
| topologyHosts  | list of hosts to check, the current node tests the reachability of each element in the list, the consistency is evaluated on the result based on this list | none, but required |


## Build

To build the plugin simply type

```mvn clean install```

## Install
Copy the ignite-segmentation-plugin-1.0.0-SNAPSHOT.jar to apache ignite's lib folder.

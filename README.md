# rabbitmq-java-client-issue-async-close
Demonstrate RabbitMQ Java client issue

https://groups.google.com/forum/#!topic/rabbitmq-users/RzMNO2lqvlw

```
mvn clean install
java -jar target/rabbitmq-java-client-issue-async-close-0.1-shaded.jar
```
With the RabbitMQ client 4.1.0 and NIO enabled (as the current state of source) it prints
```
2017-05-29 12:16:13,156 INFO  [main] Test Connecting...
2017-05-29 12:16:13,467 INFO  [main] Test Current connections: ["10.0.2.2:41158 -> 10.0.2.15:5672"]
2017-05-29 12:16:13,470 INFO  [main] Test Closing connection...
2017-05-29 12:16:13,526 INFO  [main] Test Current connections: ["10.0.2.2:41158 -> 10.0.2.15:5672"]
```

So despite the fact connection.close() was called and its Javadoc states 
> Close this connection and all its channels with the AMQP.REPLY_SUCCESS close code and message 'OK'. Waits for all the close operations to complete.

the connection is still visible by the broker.

Commenting out "useNio()" call changes output to:

```
2017-05-29 12:18:13,450 INFO  [main] Test Connecting...
2017-05-29 12:18:13,618 INFO  [main] Test Current connections: ["10.0.2.2:41178 -> 10.0.2.15:5672"]
2017-05-29 12:18:13,621 INFO  [main] Test Closing connection...
2017-05-29 12:18:13,673 INFO  [main] Test Current connections: []

```

so connection disappears before close() returns.

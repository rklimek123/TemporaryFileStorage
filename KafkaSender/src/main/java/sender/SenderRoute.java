package sender;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class SenderRoute extends RouteBuilder {

    @Value("${directory}")
    String directory;

    @Value("${kafka.address}")
    String kafkaAddress;

    @Value("${kafka.port}")
    String kafkaPort;

    @Value("${kafka.topic}")
    String kafkaTopic;

    @Value("${kafka.serializer}")
    String kafkaSerializer;

    @Value("${zookeeper.address}")
    String zookeeperAddress;

    @Value("${zookeeper.port}")
    String zookeeperPort;

    @Override
    public void configure() throws Exception {
        /*
        String topicName = new StringBuilder("topic=").append(kafkaTopic).toString();
        String kafkaServer = new StringBuilder("kafka:").append(kafkaAddress).append(":").append(kafkaPort).toString();
        String zooKeeperHost = new StringBuilder("zookeeperHost=").append(zookeeperAddress)
                                        .append("&zookeeperPort=").append(zookeeperPort).toString();
        String serializerClass = new StringBuilder("serializerClass=").append(kafkaSerializer).toString();

        String toKafka = new StringBuilder().append(kafkaServer).append("?")
                                            .append(topicName)
                                            .append("&").append(zooKeeperHost)
                                            .append("&").append(serializerClass)
                                            .toString();
*/
        String toKafka = new StringBuilder("kafka:").append(kafkaTopic).append("?")
                .append("brokers=").append(kafkaAddress).append(":").append(kafkaPort)
                .toString();

        from("file://{{directory}}?delete=true")
                .process(new FileMetadataHeaderProcessor())
                .log("Handing over file ${header.filename} to KafkaSender")
                .to("direct://uploadFile");

        from("direct://uploadFile")
                .process(new KafkaHeadersProcessor())
                .log("Sending file ${header.kafka.KEY} to Kafka")
                .to(toKafka);
    }
}

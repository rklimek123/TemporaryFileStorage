package receiverGDrive;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import org.apache.camel.component.google.drive.GoogleDriveConfiguration;
import org.apache.camel.component.google.drive.GoogleDriveComponent;

@Component
public class ReceiverToGDriveRoute extends RouteBuilder {

    @Value("${access.token}")
    String accessToken;

    @Value("${client.id}")
    String clientId;

    @Value("${client.secret}")
    String clientSecret;

    @Value("${limit.extension}")
    String limit_extension;

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
        String topicName = new StringBuilder("topic=").append(kafkaTopic).toString();
        String kafkaServer = new StringBuilder("kafka:").append(kafkaAddress).append(":").append(kafkaPort).toString();
        String zooKeeperHost = new StringBuilder("zookeeperHost=").append(zookeeperAddress)
                                        .append("&zookeeperPort=").append(zookeeperPort).toString();
        String serializerClass = new StringBuilder("serializerClass=").append(kafkaSerializer).toString();

        String fromKafka = new StringBuilder().append(kafkaServer).append("?").append(topicName)
                .append("&").append(zooKeeperHost)
                .append("&").append(serializerClass).toString();

        GoogleDriveConfiguration configuration = new GoogleDriveConfiguration();
        configuration.setAccessToken(accessToken);
        configuration.setClientId(clientId);
        configuration.setClientSecret(clientSecret);

        CamelContext context = super.getContext();
        GoogleDriveComponent component = new GoogleDriveComponent(context);
        component.setConfiguration(configuration);
        context.addComponent("google-drive", component);

        from(fromKafka)
                .process(new KafkaDataToFileProcessor())
                .log("Checking if file ${header.filename} has extension " + limit_extension)
                .log("proceed if ${header.extension} == " + limit_extension)
                .filter(simple("${header.extension} == " + limit_extension))
                .to("direct://uploadFile");

        from("direct://uploadFile")
                .process(new FileToGoogleDriveFileProcessor())
                .log("Sending file ${header.CamelGoogleDrive.content}")
                .to("google-drive://drive-files/insert");
    }
}

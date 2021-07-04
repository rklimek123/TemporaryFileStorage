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

    @Value("${kafka.address}")
    String kafkaAddress;

    @Value("${kafka.port}")
    String kafkaPort;

    @Value("${kafka.topic}")
    String kafkaTopic;

    @Value("${directory}")
    String directory;

    @Value("${limit.extension}")
    String limit_extension;

    @Override
    public void configure() throws Exception {
        String fromKafka = new StringBuilder("kafka:").append(kafkaTopic).append("?")
                .append("brokers=").append(kafkaAddress).append(":").append(kafkaPort)
                .toString();

        GoogleDriveConfiguration configuration = new GoogleDriveConfiguration();
        configuration.setAccessToken(accessToken);
        configuration.setClientId(clientId);
        configuration.setClientSecret(clientSecret);

        CamelContext context = super.getContext();
        GoogleDriveComponent component = new GoogleDriveComponent(context);
        component.setConfiguration(configuration);
        context.addComponent("google-drive", component);

        // Receive file from Kafka and save
        from(fromKafka)
            .process(new KafkaDataToFileProcessor(directory))
            .log("Got file from Kafka: ${header.kafka.KEY}");

        // ↓ Fragment copied from the previous GoogleDriveUploader project ↓
        // Get file on disk and filter extension
        from("file://{{directory}}?delete=true")
            .process(new FileMetadataHeaderProcessor())
            .log("Checking if file ${header.filename} has extension " + limit_extension)
            .filter(simple("${header.extension} == " + limit_extension))
            .to("direct://uploadFile");

        // Upload file to GoogleDrive
        from("direct://uploadFile")
            .process(new FileToGoogleDriveFileProcessor())
            .log("Sending file ${header.CamelGoogleDrive.content}")
            .to("google-drive://drive-files/insert");
   }
}

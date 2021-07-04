package receiver;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class KafkaReceiverRoute extends RouteBuilder {

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

        // Receive file from Kafka and save
        from(fromKafka)
            .process(new KafkaDataToFileProcessor(directory))
            .process(new FileMetadataHeaderProcessor())
            .log("Got file from Kafka: ${header.filename}")
            .log("Deleting the file if its extension is " + limit_extension)
            .filter(simple("${header.extension} == " + limit_extension))
            .to("direct://deleteFile");

        // Delete the file
        from("direct://deleteFile")
            .log("Deleting file ${header.filename}")
            .process(new DeleteFileProcessor())
            .log("Successfully deleted file ${header.filename}");
   }
}

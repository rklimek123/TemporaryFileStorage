package receiverGDrive;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class KafkaDataToFileProcessor implements Processor {

    public KafkaDataToFileProcessor() {}

    @Override
    public void process(Exchange exchange) throws Exception {
        File file = exchange.getIn().getBody(File.class);
        String filename = file.getName();
        int last_dot = filename.lastIndexOf('.');

        String extension;
        if (last_dot == -1) {
            extension = "";
        }
        else {
            extension = filename.substring(last_dot);
        }

        exchange.getIn().setHeader("filename", filename);
        exchange.getIn().setHeader("extension", extension);
    }
}

package receiverGDrive;

import java.io.File;
import java.io.FileWriter;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Value;

public class KafkaDataToFileProcessor implements Processor {

    private String directory;
    public KafkaDataToFileProcessor(String directory) { this.directory = directory; }

    @Override
    public void process(Exchange exchange) throws Exception {
        String data = exchange.getIn().getBody(String.class);
        String filename = exchange.getIn().getHeader("kafka.KEY").toString();

        String pathname = new StringBuilder(directory).append("/").append(filename).toString();
        File file = new File(pathname);
        file.createNewFile();

        FileWriter filewriter = new FileWriter(pathname);
        filewriter.write(data);
        filewriter.close();

        exchange.getIn().setBody(file);
    }
}

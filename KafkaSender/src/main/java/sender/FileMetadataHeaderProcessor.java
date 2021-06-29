package sender;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class FileMetadataHeaderProcessor implements Processor {

    public FileMetadataHeaderProcessor() {}

    @Override
    public void process(Exchange exchange) throws Exception {
        File file = exchange.getIn().getBody(File.class);
        String filename = file.getName();

        exchange.getIn().setHeader("filename", filename);
    }
}

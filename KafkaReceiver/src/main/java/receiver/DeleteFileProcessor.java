package receiver;

import java.io.File;
import java.io.FileWriter;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class DeleteFileProcessor implements Processor {

    public DeleteFileProcessor() {}

    @Override
    public void process(Exchange exchange) throws Exception {
        File file = exchange.getIn().getBody(File.class);
        file.delete();
        exchange.getIn().setBody(null);
    }
}

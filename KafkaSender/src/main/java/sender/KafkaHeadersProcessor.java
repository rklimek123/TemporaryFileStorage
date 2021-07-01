package sender;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class KafkaHeadersProcessor implements Processor {

    public KafkaHeadersProcessor() {}

    @Override
    public void process(Exchange exchange) throws Exception {
        File file = exchange.getIn().getBody(File.class);
        String filename = file.getName();

        exchange.getIn().setHeader("kafka.KEY", filename);
    }
}

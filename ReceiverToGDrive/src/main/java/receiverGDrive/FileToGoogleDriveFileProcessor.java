package receiverGDrive;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;

public class FileToGoogleDriveFileProcessor implements Processor {

    public FileToGoogleDriveFileProcessor() {}

    @Override
    public void process(Exchange exchange) throws Exception {
        java.io.File java_file = exchange.getIn().getBody(java.io.File.class);

        File fileMetadata = new File();
        fileMetadata.setTitle(java_file.getName());
        FileContent mediaContent = new FileContent(null, java_file);

        exchange.getIn().setHeader("CamelGoogleDrive.content", fileMetadata);
        exchange.getIn().setHeader("CamelGoogleDrive.mediaContent", mediaContent);
    }
}

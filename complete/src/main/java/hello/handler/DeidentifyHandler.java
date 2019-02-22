package hello.handler;

import com.google.cloud.WriteChannel;
import hello.service.GCSProvider;
import hello.service.GCSService;
import hello.service.DLPService;

import java.io.*;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component("DeidentifyHandler")
public class DeidentifyHandler {


    private static final Logger logger = LoggerFactory.getLogger(DeidentifyHandler.class);


    private String getCSVRecordAsString(CSVRecord record) {
        return String.join(",", (Iterable<String>) () -> record.iterator());
    }


    public Map<String, String> gcsCsvToGcsCsv(GCSService gcsService, DLPService dlpService, String sourceBucket, String sourcePath, String destBucket, String destPath) throws IOException, GeneralSecurityException {



        Map<String, String> map = new HashMap<>();
        map.put("plaintext", "gs://" + sourceBucket + "/" + sourcePath);
        map.put("deidentified", "gs://" + destBucket + "/" + destPath);

        InputStream download = gcsService.downloadNoUserEncryption(sourceBucket, sourcePath );

        try(Reader inputStreamReader = new InputStreamReader(download)){
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(inputStreamReader);
            CSVRecord outputRecord = null;
            ByteArrayInputStream targetStream = null;
            try (WriteChannel writer = gcsService.getWriter(destBucket, destPath)) {
                for (CSVRecord record : records) {
                    outputRecord = dlpService.deidentifyCSVRecord(record);
                    byte[] content = MessageFormat.format("{0}\n",
                            getCSVRecordAsString(record)
                    ).getBytes(UTF_8);
                    writer.write(ByteBuffer.wrap(content, 0, content.length));
                }

            } catch (Exception e) {
                logger.error("error writing to bucket " + destBucket + " and object " + destPath);
            }

        } catch (Exception e) {
            logger.error("error reading from bucket " + sourceBucket + " and object " + sourcePath);
        }
        return map;
    }
}

package obfuscate.service;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
// import com.sun.javafx.binding.StringFormatter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.*;

import obfuscate.common.StorageFactory;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component("GCSService")
public class GCSService {

    private static final String DUMMY_CSV_HEADER = "empId,empName,squad,division,country,salary\r\n";

    private static final Map<Integer, ArrayList<String>> fillMap;

    static {
        fillMap = new HashMap<>();
        fillMap.put(0, new ArrayList<>(Arrays.asList("Shane Warne", "Director", "Finance", "Australia", "120000" )));
        fillMap.put(1, new ArrayList<>(Arrays.asList("Richard Hadlee", "Executive", "Finance", "New Zealand", "180000" )));
        fillMap.put(2, new ArrayList<>(Arrays.asList("Mark Waugh", "Comptroller", "Finance", "Australia", "280000" )));
        fillMap.put(3, new ArrayList<>(Arrays.asList("David Boon", "Exectutive Director", "Finance", "Australia", "2000000" )));
    }

    private static final Logger logger = LoggerFactory.getLogger(GCSService.class);

    private final Storage storage;

    GCSService() {
        storage = StorageOptions.getDefaultInstance().getService();
    }

    public WriteChannel getWriter(String bucketName, String objName) throws IOException {
        GCSProvider provider = new GCSProvider(storage);
        return provider.getWriter(bucketName, objName);
    }

    public void createDummyCSV(String bucketName, String objName) {
        GCSProvider provider = new GCSProvider(storage);
        try (WriteChannel writer = provider.getWriter(bucketName, objName)) {
            byte[] header = DUMMY_CSV_HEADER.getBytes(UTF_8);
            writer.write(ByteBuffer.wrap(header, 0, header.length));
            for(int i=0; i<100000; ++i) {
                byte[] content = MessageFormat.format("{0},{1},{2},{3},{4},{5}\r\n",
                        Integer.toString(i % 4),
                        fillMap.get(i % 4).get(0),
                        fillMap.get(i % 4).get(1),
                        fillMap.get(i % 4).get(2),
                        fillMap.get(i % 4).get(3),
                        fillMap.get(i % 4).get(4)
                ).getBytes(UTF_8);
                writer.write(ByteBuffer.wrap(content, 0, content.length));
            }
        } catch (Exception e) {
            logger.warn("error writing to bucket " + bucketName + " and object " + objName);
        }
    }

    public InputStream downloadNoUserEncryption(String bucketName, String objectPath) throws IOException, GeneralSecurityException {
        return StorageFactory.downloadGoogleEncryptedObject(StorageFactory.getService(), bucketName, objectPath);
    }

}

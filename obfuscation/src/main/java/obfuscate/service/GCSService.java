package obfuscate.service;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
// import com.sun.javafx.binding.StringFormatter;
import obfuscate.dummydata.DummyCsvData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.*;

import obfuscate.common.StorageFactory;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component("GCSService")
public class GCSService {

    private static final Logger logger = LoggerFactory.getLogger(GCSService.class);

    private final Storage storage;

    GCSService() {
        storage = StorageOptions.getDefaultInstance().getService();
    }

    // For testing
    GCSService(Storage _storage) {
        storage = _storage;
    }

    public WriteChannel getWriter(String bucketName, String objName) throws IOException {
        GCSProvider provider = StorageFactory.gcsProviderFactory(storage);
        return provider.getWriter(bucketName, objName);
    }

    public void createDummyCSV(String bucketName, String objName, int rowRepetitions) {
        GCSProvider provider = StorageFactory.gcsProviderFactory(storage);
        DummyCsvData.generateDummyCsv(provider, bucketName, objName, rowRepetitions);
    }

    public InputStream downloadNoUserEncryption(String bucketName, String objectPath) throws IOException, GeneralSecurityException {
        return StorageFactory.downloadGoogleEncryptedObject(StorageFactory.getService(), bucketName, objectPath);
    }

}

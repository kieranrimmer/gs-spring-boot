package hello.service;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component("GCSService")
public class GCSService {

    private static final Logger logger = LogManager.getLogManager().getLogger(GCSService.class.getName());

    private final Storage storage;

    GCSService() {
        storage = StorageOptions.getDefaultInstance().getService();
    }

    public Map<String, String> writeToBucket(String bucketName, String objName) throws IOException {
        GCSProvider provider = new GCSProvider(storage);
        Map<String, String> map = new HashMap<>();
        map.put("bucket_name", bucketName);
        map.put("object_name", objName);
        provider.writer(bucketName, objName);
        return map;
    }


    public Map<String, String> writeToBucketInLoop(String bucketName, String objName) throws IOException {
        GCSProvider provider = new GCSProvider(storage);
        Map<String, String> map = new HashMap<>();
        map.put("bucket_name", bucketName);
        map.put("object_name", objName);
        try (WriteChannel writer = provider.getWriter(bucketName, objName)) {
            for(int i=0; i<100; ++i) {
                byte[] content = "Hello, World!\n".getBytes(UTF_8);
                writer.write(ByteBuffer.wrap(content, 0, content.length));
            }
        } catch (Exception e) {
            logger.warning("error writing to bucket " + bucketName + " and object " + objName);
        }
        return map;
    }



}

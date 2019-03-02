package obfuscate.dummydata;

import com.google.cloud.WriteChannel;
import obfuscate.service.GCSProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DummyCsvData {

    private static final Logger logger = LoggerFactory.getLogger(DummyCsvData.class);

    private static final String DUMMY_CSV_HEADER = "empId,empName,squad,division,country,salary\r\n";

    private static final Map<Integer, ArrayList<String>> fillMap;

    private static final int DEFAULT_DUMMY_ROW_COUNT = 100000;

    public static int getDefaultDummyRowCount() {
        return DEFAULT_DUMMY_ROW_COUNT;
    }

    static {
        fillMap = new HashMap<>();
        fillMap.put(0, new ArrayList<>(Arrays.asList("Shane Warne", "Director", "Finance", "Australia", "120000" )));
        fillMap.put(1, new ArrayList<>(Arrays.asList("Richard Hadlee", "Executive", "Finance", "New Zealand", "180000" )));
        fillMap.put(2, new ArrayList<>(Arrays.asList("Mark Waugh", "Comptroller", "Finance", "Australia", "280000" )));
        fillMap.put(3, new ArrayList<>(Arrays.asList("David Boon", "Exectutive Director", "Finance", "Australia", "2000000" )));
    }


    public static void generateDummyCsv(GCSProvider provider, String bucketName, String objName, int rowRepetitions) {
        try (WriteChannel writer = provider.getWriter(bucketName, objName)) {
            byte[] header = DUMMY_CSV_HEADER.getBytes(UTF_8);
            writer.write(ByteBuffer.wrap(header, 0, header.length));
            for(int i=0; i<rowRepetitions; ++i) {
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

}

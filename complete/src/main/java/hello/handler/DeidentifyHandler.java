package hello.handler;

import com.google.cloud.WriteChannel;
import hello.service.GCSProvider;
import hello.service.GCSService;
import hello.service.DLPService;

import java.io.*;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.*;

import hello.service.MacService;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component("DeidentifyHandler")
public class DeidentifyHandler {


    @Autowired
    @Qualifier("CryptoMACService")
    private MacService macService;


    private static final Logger logger = LoggerFactory.getLogger(DeidentifyHandler.class);


    private String getCSVRecordAsString(CSVRecord record) {
        return String.join(",", (Iterable<String>) () -> record.iterator());
    }

    private String getRecordSetHeaderAsString(CSVParser records) {
        return String.join(",", records.getHeaderMap().keySet());
    }


    public Map<String, String> gcsCsvToGcsCsv(GCSService gcsService, DLPService dlpService, String sourceBucket, String sourcePath, String destBucket, String destPath) throws IOException, GeneralSecurityException {



        Map<String, String> map = new HashMap<>();
        map.put("plaintext", "gs://" + sourceBucket + "/" + sourcePath);
        map.put("deidentified", "gs://" + destBucket + "/" + destPath);

        InputStream download = gcsService.downloadNoUserEncryption(sourceBucket, sourcePath );

        try(Reader inputStreamReader = new InputStreamReader(download)){
            CSVParser records = CSVFormat.DEFAULT.withFirstRecordAsHeader().withSkipHeaderRecord(false).parse(inputStreamReader);
            CSVRecord outputRecord = null;
            ByteArrayInputStream targetStream = null;
            try (WriteChannel writer = gcsService.getWriter(destBucket, destPath)) {
                byte[] headerContent = MessageFormat.format("{0},{1}\n",
                        getRecordSetHeaderAsString(records),
                        "empId Again"
                ).getBytes(UTF_8);
                writer.write(ByteBuffer.wrap(headerContent, 0, headerContent.length));
                for (CSVRecord record : records) {
                    outputRecord = dlpService.deidentifyCSVRecord(record);
                    byte[] content = MessageFormat.format("{0},{1}\n",
                            getCSVRecordAsString(record),
                            record.get("empId")
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

    public Map<String, String> hmacGcsCsvToGcsCsv(
            GCSService gcsService, DLPService dlpService,
            String sourceBucket, String sourcePath, String destBucket,
            String destPath) throws IOException, GeneralSecurityException {



        Map<String, String> map = new HashMap<>();
        map.put("plaintext", "gs://" + sourceBucket + "/" + sourcePath);
        map.put("deidentified", "gs://" + destBucket + "/" + destPath);

        InputStream download = gcsService.downloadNoUserEncryption(sourceBucket, sourcePath );

        String keyString = "FD5B357FAABEBD745A9E38AF";


        HmacUtils hmacUtils = macService.getHMACUtils((keyString + keyString).getBytes());

        Set<Integer> colNosSensitive = new HashSet<>();
        colNosSensitive.add(1);
        colNosSensitive.add(2);

        try(Reader inputStreamReader = new InputStreamReader(download)){
            CSVParser records = CSVFormat.DEFAULT.withFirstRecordAsHeader().withSkipHeaderRecord(false).parse(inputStreamReader);
            String outputRecord = null;
            ByteArrayInputStream targetStream = null;
            try (WriteChannel writer = gcsService.getWriter(destBucket, destPath)) {
                byte[] headerContent = MessageFormat.format("{0},{1}\n",
                        getRecordSetHeaderAsString(records),
                        "empId Again"
                ).getBytes(UTF_8);
                writer.write(ByteBuffer.wrap(headerContent, 0, headerContent.length));
                for (CSVRecord record : records) {
                    // record.
                    outputRecord = macService.getRedactedCSVRecordAsString(hmacUtils, record, colNosSensitive);
                    byte[] content = outputRecord.getBytes(UTF_8);
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

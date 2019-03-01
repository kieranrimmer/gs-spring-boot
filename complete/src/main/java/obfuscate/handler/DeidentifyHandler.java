package obfuscate.handler;

import com.google.cloud.WriteChannel;
import obfuscate.common.KMSFactory;
import obfuscate.dto.DeidentifyRequestPayload;
import obfuscate.dto.GCSObjectPayload;
import obfuscate.dto.KmsKeyWrapPayload;
import obfuscate.service.GCSService;

import java.io.*;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import obfuscate.service.MacService;
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

    ExecutorService executor = Executors.newFixedThreadPool(10);

    @Autowired
    @Qualifier("CryptoMACService")
    private MacService macService;

    private static final String SUCCESS_FLAG_SUFFIX = ".success_flag";
    private static final String SUCCESS_MESSAGE = "success\n";

    private static final Logger logger = LoggerFactory.getLogger(DeidentifyHandler.class);

    private String getRecordSetHeaderAsString(CSVParser records) {
        return String.join(",", records.getHeaderMap().keySet());
    }

    private void transferAndHmacWithinGCS(HmacUtils hmacUtils, GCSService gcsService, String sourceBucket, String sourcePath,
                        String destBucket, String destPath, String successFlagPath,
                        Set<Integer> colNosSensitive) throws IOException, GeneralSecurityException {

        InputStream download = gcsService.downloadNoUserEncryption(sourceBucket, sourcePath);

        try(Reader inputStreamReader = new InputStreamReader(download)){
            CSVParser records = CSVFormat.DEFAULT.withFirstRecordAsHeader().withSkipHeaderRecord(false).parse(inputStreamReader);
            String outputRecord = null;
            ByteArrayInputStream targetStream = null;
            try (WriteChannel writer = gcsService.getWriter(destBucket, destPath)) {
                byte[] headerContent = MessageFormat.format("{0}\n",
                        getRecordSetHeaderAsString(records)
                ).getBytes(UTF_8);
                writer.write(ByteBuffer.wrap(headerContent, 0, headerContent.length));
                for (CSVRecord record : records) {
                    outputRecord = macService.getRedactedCSVRecordAsString(hmacUtils, record, colNosSensitive) + "\n";
                    byte[] content = outputRecord.getBytes(UTF_8);
                    writer.write(ByteBuffer.wrap(content, 0, content.length));
                }

            } catch (Exception e) {
                logger.error("error writing to bucket " + destBucket + " and object " + destPath);
            }

        } catch (Exception e) {
            logger.error("error reading from bucket " + sourceBucket + " and object " + sourcePath);
        }
        try (WriteChannel writer = gcsService.getWriter(destBucket, successFlagPath)) {
            byte[] content = SUCCESS_MESSAGE.getBytes(UTF_8);
            writer.write(ByteBuffer.wrap(content, 0, content.length));
        } catch (Exception e) {
            logger.error("error writing to bucket " + destBucket + " and object " + successFlagPath);
        }
    }

    private void doHmacGcsTransfer(
                            HmacUtils hmacUtils, GCSService gcsService,
                            DeidentifyRequestPayload requestBody) throws IOException, GeneralSecurityException {

        String sourceBucket = requestBody.getSourceBucket();
        String sourcePath = requestBody.getSourceUrl();
        String destBucket = requestBody.getDestBucket();
        String destPath = requestBody.getDestUrl();
        Set<Integer> colNosSensitive = new HashSet<>(requestBody.getObfuscationColumns());

        String successFlagPath = destPath + SUCCESS_FLAG_SUFFIX;

        transferAndHmacWithinGCS(hmacUtils, gcsService, sourceBucket, sourcePath, destBucket, destPath, successFlagPath, colNosSensitive);

    }

    private void unwrappedHmacGcsCsvToGcsCsv(
            GCSService gcsService,
            DeidentifyRequestPayload requestBody,
            KmsKeyWrapPayload keyWrap) throws IOException, GeneralSecurityException {

        HmacUtils hmacUtils = macService.getHMACUtils(keyWrap.getPlaintext().getBytes());

        doHmacGcsTransfer(hmacUtils, gcsService, requestBody);
    }

    private void wrappedHmacGcsCsvToGcsCsv(
            GCSService gcsService,
            DeidentifyRequestPayload requestBody,
            KmsKeyWrapPayload keyWrap) throws IOException, GeneralSecurityException {

        String keyPlainText = KMSFactory.decrypt(keyWrap.getProjectId(), keyWrap.getLocationId(),
                keyWrap.getKeyRingId(), keyWrap.getCryptoKeyId(),
                keyWrap.getCiphertext());

        HmacUtils hmacUtils = macService.getHMACUtils(keyPlainText.getBytes());

        doHmacGcsTransfer(hmacUtils, gcsService, requestBody);
    }

    private Map<String, String> generateHmacGCSTransferResponseMap(DeidentifyRequestPayload requestBody) {
        Map<String, String> map = new HashMap<>();
        map.put("plaintext", "gs://" + requestBody.getSourceBucket() + "/" + requestBody.getSourceUrl());
        map.put("deidentified", "gs://" + requestBody.getDestBucket() + "/" + requestBody.getDestUrl());
        return map;
    }

    private Map<String, String> generateDummyCsvResponseMap(GCSObjectPayload requestBody) {
        Map<String, String> map = new HashMap<>();
        map.put("gcsObject", "gs://" + requestBody.getBucket() + "/" + requestBody.getUrl());
        return map;
    }

    public Map<String, String> asyncWrappedHmacGcsCsvToGcsCsv(
            GCSService gcsService,
            DeidentifyRequestPayload requestBody,
            KmsKeyWrapPayload keyWrap) {

        Runnable task = () -> {
            try {
                wrappedHmacGcsCsvToGcsCsv(gcsService, requestBody, keyWrap);
            } catch (Exception e) {
                logger.warn("Async CSV write error");
            }
        };
        executor.submit(task);

        return generateHmacGCSTransferResponseMap(requestBody);

    }

    public Map<String, String> asyncUnwrappedHmacGcsCsvToGcsCsv(
            GCSService gcsService,
            DeidentifyRequestPayload requestBody,
            KmsKeyWrapPayload keyWrap) {

        Runnable task = () -> {
            try {
                unwrappedHmacGcsCsvToGcsCsv(gcsService, requestBody, keyWrap);
            } catch (Exception e) {
                logger.warn("Async CSV write error");
            }
        };
        executor.submit(task);

        return generateHmacGCSTransferResponseMap(requestBody);

    }

    private void createDummyCSV(
            GCSService gcsService,
            GCSObjectPayload requestBody) {

        gcsService.createDummyCSV(requestBody.getBucket(), requestBody.getUrl());

    }


    public Map<String, String> asyncCreateDummyCSV(
            GCSService gcsService,
            GCSObjectPayload requestBody) {

        Runnable task = () -> {
            try {
                createDummyCSV(gcsService, requestBody);
            } catch (Exception e) {
                logger.warn("Async CSV write error");
            }
        };
        executor.submit(task);

        return generateDummyCsvResponseMap(requestBody);

    }

}

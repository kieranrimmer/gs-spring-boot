package obfuscate;

import com.fasterxml.jackson.annotation.JsonView;
import obfuscate.common.KMSFactory;
import obfuscate.dto.DeidentifyRequestPayload;
import obfuscate.dto.KmsKeyWrapPayload;
import obfuscate.dto.View;
import obfuscate.service.DLPService;
import obfuscate.service.GCSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import obfuscate.handler.DeidentifyHandler;

@RestController
@RequestMapping("/")
public class GCPObfuscateController {

    @Autowired
    private YAMLConfig myConfig;

    @Autowired
    GCSService gcsService;

    @Autowired
    DLPService dlpService;

    @Autowired
    DeidentifyHandler deidentifyHandler;
    
    @RequestMapping("")
    public String index() {
        return "Welcome to the `" + myConfig.getEnvironment() + "` environment.";
    }

    @RequestMapping(value = "writeGCSFRomCollection")
    public ResponseEntity<Map<String, String>> writeGCSFRomCollection() throws IOException {
        Map<String, String> map = gcsService.writeToBucket("kr-bucket-01-01", "crap-file.csv");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "writeGCSFRomCollectionLoop")
    public ResponseEntity<Map<String, String>> writeGCSFRomCollectionLoop() throws IOException {
        Map<String, String> map = gcsService.writeToBucketInLoop("kr-bucket-01-01", "crap-file.csv");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "createDummyCSV")
    public ResponseEntity<Map<String, String>> writeCSVFromLoop() throws IOException {
        Map<String, String> map = gcsService.createDummyCSV("kr-bucket-01-01", "dummy-csv.csv");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping("deidentifyDummyCSV")
    public ResponseEntity<Map<String, String>> deidentifyDummyCSV() throws IOException, GeneralSecurityException {
        Map<String, String> map = deidentifyHandler.gcsCsvToGcsCsv(gcsService, dlpService,
                "kr-bucket-01-01", "dummy-csv.csv",
                "kr-bucket-01-01", "obfuscated-csv.csv");


        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(path = "deidentifyHMAC", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> deidentifyWithKmsWrappedKey(@RequestBody @JsonView(View.ApiV1.class) DeidentifyRequestPayload requestBody) throws IOException, GeneralSecurityException {
        Map<String, String> map = deidentifyHandler.asyncHmacGcsCsvToGcsCsv(gcsService, dlpService,
                requestBody.getSourceBucket(), requestBody.getSourceUrl(),
                requestBody.getDestBucket(), requestBody.getDestUrl(), requestBody.getKeyWrap());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(path = "wrapDEK", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> encrypt(@RequestBody @JsonView(View.ApiV1.class) KmsKeyWrapPayload requestBody) throws IOException, GeneralSecurityException {
        String wrappedKey = KMSFactory.encrypt(requestBody.getProjectId(), requestBody.getLocationId(), requestBody.getKeyRingId(), requestBody.getCryptoKeyId(),
                requestBody.getPlaintext());
        Map<String, String> map = new HashMap<>();
        map.put("wrappedKey", wrappedKey);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(path = "unwrapDEK", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> decrypt(@RequestBody @JsonView(View.ApiV1.class) KmsKeyWrapPayload requestBody) throws IOException, GeneralSecurityException {
        String unwrappedKey = KMSFactory.decrypt(requestBody.getProjectId(), requestBody.getLocationId(), requestBody.getKeyRingId(), requestBody.getCryptoKeyId(),
                requestBody.getCiphertext());
        Map<String, String> map = new HashMap<>();
        map.put("unwrappedKey", unwrappedKey);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

}

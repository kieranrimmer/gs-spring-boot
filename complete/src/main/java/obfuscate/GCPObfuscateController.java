package obfuscate;

import com.fasterxml.jackson.annotation.JsonView;
import obfuscate.common.KMSFactory;
import obfuscate.dto.DeidentifyRequestPayload;
import obfuscate.dto.GCSObjectPayload;
import obfuscate.dto.KmsKeyWrapPayload;
import obfuscate.dto.View;
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
    DeidentifyHandler deidentifyHandler;

    // Required for unit testing
    public void setMyConfig(YAMLConfig config) {
        myConfig = config;
    }
    
    @RequestMapping("")
    public String index() {
        return "Welcome to the `" + myConfig.getEnvironment() + "` environment.";
    }

    @RequestMapping("/hello")
    public String helloMessage() {
        return "Welcome to the `" + myConfig.getEnvironment() + "` environment.";
    }

    @RequestMapping(value = "createDummyGcsCSV", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> createDummyCsv(@RequestBody @JsonView(View.ApiV1.class) GCSObjectPayload requestBody) throws IOException {
        Map<String, String> map = deidentifyHandler.asyncCreateDummyCSV(gcsService, requestBody);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(path = "deidentifyWrappedHMAC", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> deidentifyWithKmsWrappedKey(@RequestBody @JsonView(View.ApiV1.class) DeidentifyRequestPayload requestBody) throws IOException, GeneralSecurityException {
        Map<String, String> map = deidentifyHandler.asyncWrappedHmacGcsCsvToGcsCsv(gcsService, requestBody, requestBody.getKeyWrap());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(path = "deidentifyUnwrappedHMAC", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> deidentifyWithUnWrappedKey(@RequestBody @JsonView(View.ApiV1.class) DeidentifyRequestPayload requestBody) throws IOException, GeneralSecurityException {
        Map<String, String> map = deidentifyHandler.asyncUnwrappedHmacGcsCsvToGcsCsv(gcsService, requestBody, requestBody.getKeyWrap());
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

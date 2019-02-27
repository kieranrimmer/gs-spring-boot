package hello;

import hello.service.DLPService;
import hello.service.GCSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import hello.handler.DeidentifyHandler;

@RestController
@RequestMapping("/")
public class HelloController {

    @Autowired
    GCSService gcsService;

    @Autowired
    DLPService dlpService;

    @Autowired
    DeidentifyHandler deidentifyHandler;
    
    @RequestMapping("")
    public String index() {
        return "Greetings from Spring Boot!";
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

    @RequestMapping("deidentifyHMAC")
    public ResponseEntity<Map<String, String>> deidentifyWithKmsWrappedKey() throws IOException, GeneralSecurityException {
        Map<String, String> map = deidentifyHandler.hmacGcsCsvToGcsCsv(gcsService, dlpService,
                "kr-bucket-01-01", "dummy-csv.csv",
                "kr-bucket-01-01", "obfuscated-csv.csv");


        return new ResponseEntity<>(map, HttpStatus.OK);
    }

}

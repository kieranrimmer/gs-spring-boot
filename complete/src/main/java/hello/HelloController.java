package hello;

import hello.service.GCSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/")
public class HelloController {

    @Autowired
    GCSService gcsService;
    
    @RequestMapping("")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping(value = "/writeGCSFRomCollection")
    public ResponseEntity<Map<String, String>> writeGCSFRomCollection() throws IOException {
        Map<String, String> map = gcsService.writeToBucket("kr-bucket-01-01", "crap-file.csv");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/writeGCSFRomCollectionLoop")
    public ResponseEntity<Map<String, String>> writeGCSFRomCollectionLoop() throws IOException {
        Map<String, String> map = gcsService.writeToBucketInLoop("kr-bucket-01-01", "crap-file.csv");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
    
}

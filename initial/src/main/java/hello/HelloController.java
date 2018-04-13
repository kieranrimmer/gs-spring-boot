package hello;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
public class HelloController {
    
    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping(value = "/jsonSample", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<String> getJson() {
        String sampleJson = "{\"message\": \"this is json\", \"code\": 200}";
        return new ResponseEntity<String>(sampleJson, HttpStatus.OK);
    }
    
}

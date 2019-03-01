package obfuscate.service;

import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

@Component("DLPService")
public class DLPService {


    public CSVRecord deidentifyCSVRecord(CSVRecord record) {
        return record;
    }


    public CSVRecord reidentifyCSVRecord(CSVRecord record) {
        return record;
    }
}

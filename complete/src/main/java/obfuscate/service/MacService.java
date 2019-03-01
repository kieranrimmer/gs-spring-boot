package obfuscate.service;

import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.util.*;


@Component("CryptoMACService")
public class MacService {

    public HmacUtils getHMACUtils(byte[] key) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key);
    }

    private String generateHash(HmacUtils hmacUtils, String plaintext) {
        return hmacUtils.hmacHex(plaintext);
    }

    public Set<Integer> getColNumsToObfuscate(CSVParser parser, Set<String> colsToObfuscate) {
        Map<String, Integer> headerMap = parser.getHeaderMap();
        Set<Integer> colNumsToObfuscate = new HashSet<>();
        for (String _s: colsToObfuscate) {
            colNumsToObfuscate.add(headerMap.get(_s));

        }
        return colNumsToObfuscate;
    }

    public String getRedactedCSVRecordAsString(HmacUtils hmacUtils, CSVRecord record, Set<Integer> colNumsToObfuscate) {
        List<String> newRecord = new ArrayList<>(record.size());
        for (int i=0; i < record.size(); ++i) {
            newRecord.add(
                colNumsToObfuscate.contains(i) ? generateHash(hmacUtils, record.get(i)): record.get(i)
            );
        }
        return String.join(",", newRecord);
    }

}

package obfuscate.service;

import com.google.cloud.storage.Storage;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.*;

public class MacServiceTest {

    private MacService serviceUnderTest;

    private static String DUMMY_KEY_STRING = "2EF861AC0000702D2EF861AC0000702D2EF861AC0000702D2EF861AC0000702D";

    @Before
    public void setup() {

        serviceUnderTest = new MacService();

    }

    @Test
    public void testGetRedactedCSVRecordAsString() throws Exception {

        String dummyPlaintest = "David Boon";
        int dummySize = 5;

        Set<Integer> colSet = new HashSet<>();
        colSet.add(1);
        colSet.add(2);



        CSVRecord dummyRecord = mock(CSVRecord.class);
        when(dummyRecord.get(anyInt())).thenReturn(dummyPlaintest);
        when(dummyRecord.size()).thenReturn(dummySize);

        HmacUtils hmacUtils = serviceUnderTest.getHMACUtils(DUMMY_KEY_STRING.getBytes());
        String output = serviceUnderTest.getRedactedCSVRecordAsString(hmacUtils, dummyRecord, colSet);

        assert output != null;

    }

}

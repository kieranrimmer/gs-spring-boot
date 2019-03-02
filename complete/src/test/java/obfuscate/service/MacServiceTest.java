package obfuscate.service;

import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.*;

import static org.hamcrest.Matchers.*;

public class MacServiceTest {

    private MacService serviceUnderTest;

    private static String DUMMY_KEY_STRING = "2EF861AC0000702D2EF861AC0000702D2EF861AC0000702D2EF861AC0000702D";

    private static String DUMMY_PLAINTEXT = "David Boon";

    private static String EXPECTED_CIPHERTEXT = "17488d52f083a61841c7063366ca6b43e13065456c768f0d5079ea07dcafbc70";

    private static int DUMMY_ROW_SIZE = 5;

    private static Set<Integer> COL_SET_TO_ENCIPHER = new HashSet<>();

    private static Map<String, Integer> TEST_HEADER_MAP = new HashMap<>();

    private static Set<String> COLS_TO_FIND = new HashSet<>();

    static {

        COL_SET_TO_ENCIPHER.add(1);
        COL_SET_TO_ENCIPHER.add(2);

        TEST_HEADER_MAP.put("EmpId", 0);
        TEST_HEADER_MAP.put("account Id", 1);

        COLS_TO_FIND.add("account Id");

        TEST_HEADER_MAP.put("customer Id", 2);

        COLS_TO_FIND.add("customer Id");

        TEST_HEADER_MAP.put("descriptor", 3);
        TEST_HEADER_MAP.put("freeText", 4);
    }

    @Before
    public void setup() {

        serviceUnderTest = new MacService();

    }

    @Test
    public void testGetRedactedCSVRecordAsString() throws Exception {

        CSVRecord dummyRecord = mock(CSVRecord.class);
        when(dummyRecord.get(anyInt())).thenReturn(DUMMY_PLAINTEXT);
        when(dummyRecord.size()).thenReturn(DUMMY_ROW_SIZE);

        HmacUtils hmacUtils = serviceUnderTest.getHMACUtils(DUMMY_KEY_STRING.getBytes());
        String output = serviceUnderTest.getRedactedCSVRecordAsString(hmacUtils, dummyRecord, COL_SET_TO_ENCIPHER);

        String[] outputRow = output.split(",");

        int j = 0;
        for (int i=0; i < outputRow.length; ++i) {
            if (COL_SET_TO_ENCIPHER.contains(i)) {
                ++j;
                assertThat(outputRow[i], is(EXPECTED_CIPHERTEXT));
            } else {
                assertThat(outputRow[i], is(DUMMY_PLAINTEXT));
            }
        }
        assertThat(j, equalTo(COL_SET_TO_ENCIPHER.size()));

    }


    @Test
    public void testGetColNumbersFromColNames() throws Exception {

        CSVParser dummyParser = mock(CSVParser.class);

        when(dummyParser.getHeaderMap()).thenReturn(TEST_HEADER_MAP);

        Set<Integer> resultSet =  serviceUnderTest.getColNumsToObfuscate(dummyParser, COLS_TO_FIND);

        assertThat(resultSet.size(), equalTo(COLS_TO_FIND.size()));

        for (String colToFind: COLS_TO_FIND) {
            assert resultSet.contains(TEST_HEADER_MAP.get(colToFind));
        }

    }

}

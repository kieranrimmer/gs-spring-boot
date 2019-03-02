package obfuscate.handler;

import obfuscate.YAMLConfig;
import obfuscate.dto.GCSObjectPayload;
import obfuscate.dummydata.DummyCsvData;
import obfuscate.service.GCSService;
import obfuscate.service.MacService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { YAMLConfig.class, GCSService.class })
@ActiveProfiles("test")
public class DeidentifyHandlerTest {

    @Autowired
    private YAMLConfig yamlConfig;

    @Mock
    private MacService macService;

    private ExecutorService executor;

    @InjectMocks
    private DeidentifyHandler handlerUnderTest;

    private final int CSV_ROW_REPETITIONS = 100000;


    @Before
    public void setup() {

        // as per https://stackoverflow.com/questions/16170572/unable-to-mock-service-class-in-spring-mvc-controller-tests
        // this must be called for the @Mock annotations above to be processed
        // and for the mock service to be injected into the controller under
        // test.

        executor = Executors.newSingleThreadExecutor();

        MockitoAnnotations.initMocks(this);

        handlerUnderTest.setExecutor(executor);

    }

    private static GCSObjectPayload generateDummyGCSPayload(String bucket, String url) {
        GCSObjectPayload payload = new GCSObjectPayload();
        payload.setBucket(bucket);
        payload.setUrl(url);
        return payload;
    }


    @Test
    public void createDummyCsvTest() throws Exception {
        String testBucket = "testBucket";
        String testUrl = "testUrl";
        GCSService mockGcsService = mock(GCSService.class);
        GCSObjectPayload mockPayload = generateDummyGCSPayload(testBucket, testUrl);
        handlerUnderTest.asyncCreateDummyCSV(mockGcsService, mockPayload, CSV_ROW_REPETITIONS);
        verify(mockGcsService, times(1)).createDummyCSV(testBucket, testUrl, CSV_ROW_REPETITIONS);
    }


}

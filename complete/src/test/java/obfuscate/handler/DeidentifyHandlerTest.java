package obfuscate.handler;

import static org.hamcrest.Matchers.equalTo;
import static org.powermock.configuration.ConfigurationType.PowerMock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import obfuscate.GCPObfuscateController;
import obfuscate.YAMLConfig;
import obfuscate.dto.GCSObjectPayload;
import obfuscate.handler.DeidentifyHandler;
import obfuscate.service.DLPService;
import obfuscate.service.GCSService;
import obfuscate.service.MacService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.*;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { YAMLConfig.class, GCSService.class })
@ActiveProfiles("test")
public class DeidentifyHandlerTest {

    @Autowired
    YAMLConfig yamlConfig;

    @Mock
    MacService macService;

    ExecutorService executor;

    @InjectMocks
    DeidentifyHandler handlerUnderTest;


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


    @Test
    public void createDummyCsvTest() throws Exception {
        GCSService mockGcsService = mock(GCSService.class);
        GCSObjectPayload mockPayload = mock(GCSObjectPayload.class);
        // doNothing().when(gcsService.downloadNoUserEncryption(anyString(), anyString()));
        handlerUnderTest.asyncCreateDummyCSV(mockGcsService, mockPayload);
        verify(mockGcsService, times(1)).createDummyCSV(any(), any());
    }


}

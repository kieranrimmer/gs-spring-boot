package obfuscate;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import obfuscate.handler.DeidentifyHandler;
import obfuscate.service.MacService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { YAMLConfig.class })
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GCPObfuscateControllerTest {

    /*

    Useful references:

        https://stackoverflow.com/questions/16170572/unable-to-mock-service-class-in-spring-mvc-controller-tests
        https://dzone.com/articles/use-mockito-mock-autowired


     */

    private MockMvc mockMvc;

    @Autowired
    YAMLConfig yamlConfig;

    @Mock
    private MacService macService;

    @Mock
    DeidentifyHandler deidentifyHandler;


    @InjectMocks
    GCPObfuscateController controllerUnderTest;

    @Before
    public void setup() {

        // as per https://stackoverflow.com/questions/16170572/unable-to-mock-service-class-in-spring-mvc-controller-tests
        // this must be called for the @Mock annotations above to be processed
        // and for the mock service to be injected into the controller under
        // test.

        MockitoAnnotations.initMocks(this);

        controllerUnderTest.setConfig(yamlConfig);

        mockMvc = MockMvcBuilders.standaloneSetup(controllerUnderTest).build();

    }

    @Test
    public void getHelloString() throws Exception {
        int i=0;
        MvcResult result =  mockMvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Welcome to the `test` environment.")))
                .andReturn();
        // String resultString = result.getResponse().getContentAsString();
    }

    @Test
    public void postSillyWriteGCS() throws Exception {

        String jsonPayload = "{ \"gcsObject\": {\"bucket\": \"fake-bucket\", \"url\": \"fake-url\"}, \"rowCount\": 100000}";

        MvcResult result =  mockMvc.perform(MockMvcRequestBuilders.post("/createDummyGcsCSV")
                .content(jsonPayload)
                .header("Content-Type", "application/json")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("{}")))
                .andReturn();
    }


}

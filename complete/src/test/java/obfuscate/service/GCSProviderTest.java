package obfuscate.service;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.concurrent.Executors;

import org.powermock.api.mockito.PowerMockito;

import static org.mockito.Mockito.*;

import static org.powermock.configuration.ConfigurationType.PowerMock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BlobId.class, BlobInfo.class})
public class GCSProviderTest {

    private Storage storage;

    private GCSProvider serviceUnderTest;

    @Before
    public void setup() {

        // as per https://stackoverflow.com/questions/16170572/unable-to-mock-service-class-in-spring-mvc-controller-tests
        // this must be called for the @Mock annotations above to be processed
        // and for the mock service to be injected into the controller under
        // test.



        MockitoAnnotations.initMocks(this);

        storage = mock(Storage.class);
        serviceUnderTest = new GCSProvider(storage);

    }

    @Test
    public void testGetWriter() throws Exception {
        PowerMockito.mockStatic(BlobInfo.class);
        PowerMockito.mockStatic(BlobId.class);

        BlobId mockBlobId = mock(BlobId.class);
        BlobInfo mockBlobInfo = mock(BlobInfo.class);

        when(BlobId.of(any(), any())).thenReturn(mockBlobId);

        // BlobInfo blobInfo = BlobInfo.newBuilder(any(BlobId.class)).setContentType("text/plain").build();

        BlobInfo.Builder mockBuilder = mock(BlobInfo.Builder.class);
        when(BlobInfo.newBuilder(any(BlobId.class))).thenReturn(mockBuilder);

        when(mockBuilder.setContentType(any())).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockBlobInfo);

        serviceUnderTest.getWriter("bucketName", "blobName");

        verify(storage, times(1)).writer(any());






    }

}

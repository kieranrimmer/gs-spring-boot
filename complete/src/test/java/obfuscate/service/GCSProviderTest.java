package obfuscate.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.powermock.api.mockito.PowerMockito;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BlobId.class, BlobInfo.class})
public class GCSProviderTest {

    private Storage storage;

    private GCSProvider serviceUnderTest;

    @Before
    public void setup() {

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

        BlobInfo.Builder mockBuilder = mock(BlobInfo.Builder.class);
        when(BlobInfo.newBuilder(any(BlobId.class))).thenReturn(mockBuilder);

        when(mockBuilder.setContentType(any())).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockBlobInfo);

        serviceUnderTest.getWriter("bucketName", "blobName");

        verify(storage, times(1)).writer(mockBlobInfo);

    }

}

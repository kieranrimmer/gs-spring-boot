package obfuscate.service;

import com.google.cloud.WriteChannel;
import obfuscate.common.StorageFactory;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.powermock.api.mockito.PowerMockito;

import static org.mockito.Mockito.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest({StorageFactory.class})
public class GCSServiceTest {

    private Storage storage;

    private GCSService serviceUnderTest;

    @Before
    public void setup() {

        storage = mock(Storage.class);
        serviceUnderTest = new GCSService(storage);

    }

    @Test
    public void testCreateDummyCsv() throws Exception {

        PowerMockito.mockStatic(StorageFactory.class);

        GCSProvider dummyProvider = mock(GCSProvider.class);

        WriteChannel writer = mock(WriteChannel.class);

        when(StorageFactory.gcsProviderFactory(any())).thenReturn(dummyProvider);

        when(dummyProvider.getWriter(any(), any())).thenReturn(writer);

        serviceUnderTest.createDummyCSV("bucket-name", "object-name");

        // don't forget header row contributes a +1
        verify(writer, times(GCSService.getDefaultDummyRowCount() + 1)).write(any());

    }

}

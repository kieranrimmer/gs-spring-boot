package obfuscate.common;

import com.google.api.services.cloudkms.v1.CloudKMS;
import com.google.api.services.cloudkms.v1.model.DecryptResponse;
import com.google.api.services.cloudkms.v1.model.EncryptResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import static org.mockito.Mockito.*;

public class KMSFactoryTest {

    private CloudKMS mockCloudKms;

    private static final String TEST_DECRYPT_RESPONSE = "test_decryption_response";
    private static final String TEST_ENCRYPT_RESPONSE = "test_encryption_response";

    @Before
    public void setup() {

        // as per https://stackoverflow.com/questions/16170572/unable-to-mock-service-class-in-spring-mvc-controller-tests
        // this must be called for the @Mock annotations above to be processed
        // and for the mock service to be injected into the controller under
        // test.
        mockCloudKms = mock(CloudKMS.class);
        KMSFactory.setService(mockCloudKms);

    }

    @Test
    public void basicDecryptionTest() throws Exception {

        // Because Mockito cannot mock a final class
        DecryptResponse response = new DecryptResponse();
        response.setPlaintext(TEST_DECRYPT_RESPONSE);
        //

        CloudKMS.Projects mockProjects = mock(CloudKMS.Projects.class);
        when(mockCloudKms.projects()).thenReturn(mockProjects);

        CloudKMS.Projects.Locations mockLocations = mock(CloudKMS.Projects.Locations.class);
        when(mockProjects.locations()).thenReturn(mockLocations);

        CloudKMS.Projects.Locations.KeyRings mockKeyRings = mock(CloudKMS.Projects.Locations.KeyRings.class);
        when(mockLocations.keyRings()).thenReturn(mockKeyRings);

        CloudKMS.Projects.Locations.KeyRings.CryptoKeys mockCryptoKeys = mock(CloudKMS.Projects.Locations.KeyRings.CryptoKeys.class);
        when(mockKeyRings.cryptoKeys()).thenReturn(mockCryptoKeys);

        CloudKMS.Projects.Locations.KeyRings.CryptoKeys.Decrypt mockDecryptObject = mock(CloudKMS.Projects.Locations.KeyRings.CryptoKeys.Decrypt.class);
        when(mockCryptoKeys.decrypt(any(), any())).thenReturn(mockDecryptObject);

        when(mockDecryptObject.execute()).thenReturn(response);
        String decrypted = KMSFactory.decrypt("project-id", "location-id", "keyring-id",
                "crypto-key-id", "ciphertext");

        assertThat(decrypted, is(TEST_DECRYPT_RESPONSE));

    }

    @Test
    public void basicEncryptionTest() throws Exception {

        // Because Mockito cannot mock a final class
        EncryptResponse response = new EncryptResponse();
        response.setCiphertext(TEST_ENCRYPT_RESPONSE);
        //

        CloudKMS.Projects mockProjects = mock(CloudKMS.Projects.class);
        when(mockCloudKms.projects()).thenReturn(mockProjects);

        CloudKMS.Projects.Locations mockLocations = mock(CloudKMS.Projects.Locations.class);
        when(mockProjects.locations()).thenReturn(mockLocations);

        CloudKMS.Projects.Locations.KeyRings mockKeyRings = mock(CloudKMS.Projects.Locations.KeyRings.class);
        when(mockLocations.keyRings()).thenReturn(mockKeyRings);

        CloudKMS.Projects.Locations.KeyRings.CryptoKeys mockCryptoKeys = mock(CloudKMS.Projects.Locations.KeyRings.CryptoKeys.class);
        when(mockKeyRings.cryptoKeys()).thenReturn(mockCryptoKeys);

        CloudKMS.Projects.Locations.KeyRings.CryptoKeys.Encrypt mockEncryptObject = mock(CloudKMS.Projects.Locations.KeyRings.CryptoKeys.Encrypt.class);
        when(mockCryptoKeys.encrypt(any(), any())).thenReturn(mockEncryptObject);

        when(mockEncryptObject.execute()).thenReturn(response);
        String encrypted = KMSFactory.encrypt("project-id", "location-id", "keyring-id",
                "crypto-key-id", "ciphertext");

        assertThat(encrypted, is(TEST_ENCRYPT_RESPONSE));

    }

}

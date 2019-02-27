package hello.dto;

import com.fasterxml.jackson.annotation.JsonView;

public class KmsKeyWrapPayload {

    @JsonView(View.ApiV1.class)
    private String projectId;

    @JsonView(View.ApiV1.class)
    private String locationId;

    @JsonView(View.ApiV1.class)
    private String keyRingId;

    @JsonView(View.ApiV1.class)
    private String cryptoKeyId;

    @JsonView(View.ApiV1.class)
    private String plaintext;

    @JsonView(View.ApiV1.class)
    private String ciphertext;

    public String getProjectId() {
        return projectId;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getKeyRingId() {
        return keyRingId;
    }

    public String getCryptoKeyId() {
        return cryptoKeyId;
    }

    public String getPlaintext() {
        return plaintext;
    }

    public String getCiphertext() {
        return ciphertext;
    }

}

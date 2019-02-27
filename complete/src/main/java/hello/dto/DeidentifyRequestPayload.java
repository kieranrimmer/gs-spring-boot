package hello.dto;

import com.fasterxml.jackson.annotation.JsonView;

public class DeidentifyRequestPayload {

    @JsonView(View.ApiV1.class)
    private String sourceType;

    @JsonView(View.ApiV1.class)
    private String sourceBucket;

    @JsonView(View.ApiV1.class)
    private String sourceUrl;

    @JsonView(View.ApiV1.class)
    private String destType;

    @JsonView(View.ApiV1.class)
    private String destBucket;

    @JsonView(View.ApiV1.class)
    private String destUrl;

    @JsonView(View.ApiV1.class)
    private KmsKeyWrapPayload kmsKeyWrap;

    public String getSourceType() {
        return sourceType;
    }

    public String getSourceBucket() {
        return sourceBucket;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getDestType() {
        return destType;
    }

    public String getDestBucket() {
        return destBucket;
    }

    public String getDestUrl() {
        return destUrl;
    }

    public KmsKeyWrapPayload getKeyWrap() {
        return kmsKeyWrap;
    }

}

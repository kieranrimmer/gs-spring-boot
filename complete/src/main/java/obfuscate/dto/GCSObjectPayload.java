package obfuscate.dto;

import com.fasterxml.jackson.annotation.JsonView;

public class GCSObjectPayload {

    @JsonView(View.ApiV1.class)
    private String bucket;

    @JsonView(View.ApiV1.class)
    private String url;

    public String getBucket() {
        return bucket;
    }

    public String getUrl() {
        return url;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}

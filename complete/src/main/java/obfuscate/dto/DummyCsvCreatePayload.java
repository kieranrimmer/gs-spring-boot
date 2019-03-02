package obfuscate.dto;

import com.fasterxml.jackson.annotation.JsonView;

public class DummyCsvCreatePayload {

    @JsonView(View.ApiV1.class)
    private Integer rowCount;

    @JsonView(View.ApiV1.class)
    private GCSObjectPayload gcsObject;

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public GCSObjectPayload getGcsObject() {
        return gcsObject;
    }

    public void setGcsObject(GCSObjectPayload gcsObject) {
        this.gcsObject = gcsObject;
    }

}

package com.smarthome.client2.model.login;

//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class FamilyRelativeList {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("appellationName")
    @Expose
    private String appellationName;
    @SerializedName("appellationCode")
    @Expose
    private String appellationCode;

    /**
     *
     * @return
     * The id
     */
    public Long getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The appellationName
     */
    public String getAppellationName() {
        return appellationName;
    }

    /**
     *
     * @param appellationName
     * The appellationName
     */
    public void setAppellationName(String appellationName) {
        this.appellationName = appellationName;
    }

    /**
     *
     * @return
     * The appellationCode
     */
    public String getAppellationCode() {
        return appellationCode;
    }

    /**
     *
     * @param appellationCode
     * The appellationCode
     */
    public void setAppellationCode(String appellationCode) {
        this.appellationCode = appellationCode;
    }

}

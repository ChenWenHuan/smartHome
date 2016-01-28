package com.smarthome.client2.model.login;

//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class Token {

    @SerializedName("tokenCode")
    @Expose
    private String tokenCode;
    @SerializedName("refreshToken")
    @Expose
    private Boolean refreshToken;
    @SerializedName("expireIn")
    @Expose
    private String expireIn;

    /**
     *
     * @return
     * The tokenCode
     */
    public String getTokenCode() {
        return tokenCode;
    }

    /**
     *
     * @param tokenCode
     * The tokenCode
     */
    public void setTokenCode(String tokenCode) {
        this.tokenCode = tokenCode;
    }

    /**
     *
     * @return
     * The refreshToken
     */
    public Boolean getRefreshToken() {
        return refreshToken;
    }

    /**
     *
     * @param refreshToken
     * The refreshToken
     */
    public void setRefreshToken(Boolean refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     *
     * @return
     * The expireIn
     */
    public String getExpireIn() {
        return expireIn;
    }

    /**
     *
     * @param expireIn
     * The expireIn
     */
    public void setExpireIn(String expireIn) {
        this.expireIn = expireIn;
    }

}

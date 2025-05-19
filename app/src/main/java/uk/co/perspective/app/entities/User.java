package uk.co.perspective.app.entities;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("userID")
    private Integer userID;
    @SerializedName("displayName")
    private String displayName;
    @SerializedName("token")
    private String token;
    @SerializedName("companyName")
    private String companyName;


    public User() {
    }

    public User(Integer userID, String displayName, String token) {
        super();
        this.userID = userID;
        this.displayName = displayName;
        this.token = token;
        this.companyName = companyName;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}

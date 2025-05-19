package uk.co.perspective.app.entities;

import com.google.gson.annotations.SerializedName;

public class Person {

    @SerializedName("peopleID")
    private Integer peopleID;
    @SerializedName("displayName")
    private String displayName;
    @SerializedName("resourceID")
    private Integer resourceID;

    public Person() {
    }

    public Person(Integer peopleID, String displayName, Integer resourceID) {
        super();
        this.peopleID = peopleID;
        this.displayName = displayName;
        this.resourceID = resourceID;
    }

    public Integer getPeopleID() {
        return peopleID;
    }

    public void setPeopleID(Integer peopleID) {
        this.peopleID = peopleID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getResourceID() {
        return resourceID;
    }

    public void setResourceID(Integer resourceID) {
        this.resourceID = resourceID;
    }

}

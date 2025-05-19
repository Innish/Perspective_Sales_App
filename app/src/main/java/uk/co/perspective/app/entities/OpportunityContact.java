package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class OpportunityContact {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("opportunityID")
    @ColumnInfo(name = "opportunityID")
    private Integer opportunityID;

    @SerializedName("contactID")
    @ColumnInfo(name = "contactID")
    private Integer contactID;

    @SerializedName("localOpportunityID")
    @ColumnInfo(name = "localOpportunityID")
    private Integer localOpportunityID;

    @SerializedName("localContactID")
    @ColumnInfo(name = "localContactID")
    private Integer localContactID;

    public OpportunityContact()
    {

    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOpportunityID() {
        return opportunityID;
    }
    public void setOpportunityID(Integer opportunityID) {
        this.opportunityID = opportunityID;
    }
    public OpportunityContact withOpportunityID(Integer opportunityID) {
        this.opportunityID = opportunityID;
        return this;
    }

    public Integer getContactID() {
        return contactID;
    }
    public void setContactID(Integer contactID) {
        this.contactID = contactID;
    }
    public OpportunityContact withContactID(Integer contactID) {
        this.contactID = contactID;
        return this;
    }

    public Integer getLocalOpportunityID() {
        return localOpportunityID;
    }
    public void setLocalOpportunityID(Integer localOpportunityID) {
        this.localOpportunityID = localOpportunityID;
    }
    public OpportunityContact withLocalOpportunityID(Integer localOpportunityID) {
        this.localOpportunityID = localOpportunityID;
        return this;
    }

    public Integer getLocalContactID() {
        return localContactID;
    }
    public void setLocalContactID(Integer localContactID) {
        this.localContactID = localContactID;
    }
    public OpportunityContact withLocalContactID(Integer localContactID) {
        this.localContactID = localContactID;
        return this;
    }
}

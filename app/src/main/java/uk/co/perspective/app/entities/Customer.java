package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Customer implements Comparable<Customer> {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("customerID")
    @ColumnInfo(name = "customerID")
    private Integer customerID;

    @SerializedName("customerName")
    @ColumnInfo(name = "customerName")
    private String customerName;

    @SerializedName("customerReference")
    @ColumnInfo(name = "customerReference")
    private String customerReference;

    @SerializedName("customerType")
    @ColumnInfo(name = "customerType")
    private String customerType;

    @SerializedName("customerStatus")
    @ColumnInfo(name = "customerStatus")
    private String customerStatus;

    @SerializedName("parentCustomerName")
    @ColumnInfo(name = "parentCustomerName")
    private String parentCustomerName;

    @SerializedName("contactName")
    @ColumnInfo(name = "contactName")
    private String contactName;

    @SerializedName("generalTelephone")
    @ColumnInfo(name = "generalTelephone")
    private String generalTelephone;

    @SerializedName("mobile")
    @ColumnInfo(name = "mobile")
    private String mobile;

    @SerializedName("generalEmail")
    @ColumnInfo(name = "generalEmail")
    private String generalEmail;

    @SerializedName("notes")
    @ColumnInfo(name = "notes")
    private String notes;

    @SerializedName("createdByDisplayName")
    @ColumnInfo(name = "createdByDisplayName")
    private String createdByDisplayName;

    @SerializedName("updated")
    @ColumnInfo(name = "updated")
    private String updated;

    @ColumnInfo(name = "isArchived")
    private boolean isArchived;

    @ColumnInfo(name = "isChanged")
    private boolean isChanged;

    @ColumnInfo(name = "isNew")
    private boolean isNew;

    public Customer()
    {

    }

    @Ignore
    public Customer(int customerID, String customerName, String contactName)
    {
        this.customerID = customerID;
        this.customerName = customerName;
        this.contactName = contactName;
    }

    @Ignore
    public Customer(int customerID, String customerName)
    {
        this.customerID = customerID;
        this.customerName = customerName;
    }

    //Getters / Setters

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerID() {
        return customerID;
    }
    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }
    public Customer withCustomerID(Integer customerID) {
        this.customerID = customerID;
        return this;
    }

    //Customer Name

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public Customer withCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    //Customer Reference

    public String getCustomerReference() {
        return customerReference;
    }
    public void setCustomerReference(String customerReference) { this.customerReference = customerReference; }
    public Customer withCustomerReference(String customerReference) {
        this.customerReference = customerReference;
        return this;
    }

    //Customer Type

    public String getCustomerType() {
        return customerType;
    }
    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }
    public Customer withCustomerType(String customerType) {
        this.customerType = customerType;
        return this;
    }

    //Customer Status

    public String getCustomerStatus() {
        return customerStatus;
    }
    public void setCustomerStatus(String customerStatus) {
        this.customerStatus = customerStatus;
    }
    public Customer withCustomerStatus(String customerStatus) {
        this.customerStatus = customerStatus;
        return this;
    }

    //Parent Customer

    public String getParentCustomerName() {
        return parentCustomerName;
    }
    public void setParentCustomerName(String parentCustomerName) { this.parentCustomerName = parentCustomerName; }
    public Customer withParentCustomerName(String parentCustomerName) {
        this.parentCustomerName = parentCustomerName;
        return this;
    }

    //Contact Name

    public String getContactName() {
        return contactName;
    }
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    public Customer withContactName(String contactName) {
        this.contactName = contactName;
        return this;
    }

    //Telephone

    public String getGeneralTelephone() {
        return generalTelephone;
    }
    public void setGeneralTelephone(String generalTelephone) {
        this.generalTelephone = generalTelephone;
    }
    public Customer withGeneralTelephone(String generalTelephone) {
        this.generalTelephone = generalTelephone;
        return this;
    }

    //Mobile

    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public Customer withMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    //Email

    public String getGeneralEmail() {
        return generalEmail;
    }
    public void setGeneralEmail(String generalEmail) {
        this.generalEmail = generalEmail;
    }
    public Customer withGeneralEmail(String generalEmail) {
        this.generalEmail = generalEmail;
        return this;
    }

    //Notes

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public Customer withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    //Created By

    public String getCreatedByDisplayName() {
        return createdByDisplayName;
    }
    public void setCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
    }
    public Customer withCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
        return this;
    }

    //Updated

    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }
    public Customer withUpdated(String updated) {
        this.updated = updated;
        return this;
    }

    public boolean getIsArchived() { return isArchived; }
    public void setIsArchived(boolean isArchived) { this.isArchived = isArchived; }

    public boolean getIsChanged() {
        return isChanged;
    }
    public void setIsChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    public boolean isNew() {
        return isNew;
    }
    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public int compareTo(Customer o) {

        int lexicographicallyDifferent = 0;

        if (getCustomerName() != null) {
            lexicographicallyDifferent = getCustomerName().compareTo(o.getCustomerName());
        }

        return lexicographicallyDifferent;
    }
}

package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Contact {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("contactID")
    @ColumnInfo(name = "contactID")
    private Integer contactID;

    @SerializedName("localCustomerID")
    @ColumnInfo(name = "localCustomerID")
    private Integer localCustomerID;

    @SerializedName("customerID")
    @ColumnInfo(name = "customerID")
    private Integer customerID;

    @SerializedName("contactName")
    @ColumnInfo(name = "contactName")
    private String contactName;

    @SerializedName("customerName")
    @ColumnInfo(name = "customerName")
    private String customerName;

    @SerializedName("salutation")
    @ColumnInfo(name = "salutation")
    private String salutation;

    @SerializedName("jobTitle")
    @ColumnInfo(name = "jobTitle")
    private String jobTitle;

    @SerializedName("notes")
    @ColumnInfo(name = "notes")
    private String notes;

    @SerializedName("telephone")
    @ColumnInfo(name = "telephone")
    private String telephone;

    @SerializedName("mobile")
    @ColumnInfo(name = "mobile")
    private String mobile;

    @SerializedName("email")
    @ColumnInfo(name = "email")
    private String email;

    @SerializedName("updated")
    @ColumnInfo(name = "updated")
    private String updated;

    @SerializedName("createdByDisplayName")
    @ColumnInfo(name = "createdByDisplayName")
    private String createdByDisplayName;

    @ColumnInfo(name = "isArchived")
    private boolean isArchived;

    @ColumnInfo(name = "isChanged")
    private boolean isChanged;

    @ColumnInfo(name = "isNew")
    private boolean isNew;

    public Contact()
    {

    }

    @Ignore
    public Contact(int contactID, String contactName, String customerName, String jobTitle, String telephone, String mobile, String email)
    {
        this.contactID = contactID;
        this.contactName = contactName;
        this.customerName = customerName;
        this.jobTitle = jobTitle;
        this.telephone = telephone;
        this.mobile = mobile;
        this.email = email;
    }

    @Ignore
    public Contact(int contactID, String contactName)
    {
        this.contactID = contactID;
        this.contactName = contactName;
    }

    //Getters / Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    //ContacID

    public Integer getContactID() {
        return contactID;
    }

    public void setContactID(Integer contactID) {
        this.contactID = contactID;
    }

    public Contact withContactID(Integer contactID) {
        this.contactID = contactID;
        return this;
    }

    //LocalCustomerID

    public Integer getLocalCustomerID() {
        return localCustomerID;
    }

    public void setLocalCustomerID(Integer localCustomerID) {
        this.localCustomerID = localCustomerID;
    }

    public Contact withLocalCustomerID(Integer localCustomerID) {
        this.localCustomerID = localCustomerID;
        return this;
    }

    //CustomerID

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    public Contact withCustomerID(Integer customerID) {
        this.customerID = customerID;
        return this;
    }

    //Contact Name

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Contact withContactName(String contactName) {
        this.contactName = contactName;
        return this;
    }

    //Customer Name

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Contact withCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    //Customer Reference

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public Contact withSalutation(String salutation) {
        this.salutation = salutation;
        return this;
    }

    //Customer Type

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Contact withJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
        return this;
    }

    //Notes

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Contact withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    //Telephone

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Contact withTelephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    //Mobile

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Contact withMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    //Email

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Contact withEmail(String email) {
        this.email = email;
        return this;
    }

    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }
    public Contact withUpdated(String updated) {
        this.updated = updated;
        return this;
    }

    public String getCreatedByDisplayName() {
        return createdByDisplayName;
    }
    public void setCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
    }
    public Contact withCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
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
}

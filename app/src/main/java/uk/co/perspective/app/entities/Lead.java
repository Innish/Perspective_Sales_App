package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Lead implements Comparable<Lead> {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("leadID")
    @ColumnInfo(name = "leadID")
    private Integer leadID;

    @SerializedName("localCustomerID")
    @ColumnInfo(name = "localCustomerID")
    private Integer localCustomerID;

    @SerializedName("customerID")
    @ColumnInfo(name = "customerID")
    private Integer customerID;

    @SerializedName("customerName")
    @ColumnInfo(name = "customerName")
    private String customerName;

    @SerializedName("contactID")
    @ColumnInfo(name = "contactID")
    private Integer contactID;

    @SerializedName("rating")
    @ColumnInfo(name = "rating")
    private Integer rating;

    @SerializedName("contactName")
    @ColumnInfo(name = "contactName")
    private String contactName;

    @SerializedName("telephone")
    @ColumnInfo(name = "telephone")
    private String telephone;

    @SerializedName("email")
    @ColumnInfo(name = "email")
    private String email;

    @SerializedName("subject")
    @ColumnInfo(name = "subject")
    private String subject;

    @SerializedName("status")
    @ColumnInfo(name = "status")
    private String status;

    @SerializedName("value")
    @ColumnInfo(name = "value")
    private String value;

    @SerializedName("notes")
    @ColumnInfo(name = "notes")
    private String notes;

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

    public Lead()
    {

    }

    @Ignore
    public Lead(int leadID, String customerName, String status, String value, String subject, int rating, String notes, boolean isNew)
    {
        this.leadID = leadID;
        this.customerName = customerName;
        this.status = status;
        this.value = value;
        this.subject = subject;
        this.rating = rating;
        this.notes = notes;
        this.isNew = isNew;
    }

    @Ignore
    public Lead(int leadID, String subject)
    {
        this.leadID = leadID;
        this.subject = subject;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLeadID() {
        return leadID;
    }
    public void setLeadID(Integer leadID) {
        this.leadID = leadID;
    }
    public Lead withLeadID(Integer leadID) {
        this.leadID = leadID;
        return this;
    }

    public Integer getRating() {
        return rating;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    public Lead withRating(Integer rating) {
        this.rating = rating;
        return this;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Lead withStatus(String status) {
        this.status = status;
        return this;
    }

    //LocalCustomerID

    public Integer getLocalCustomerID() {
        return localCustomerID;
    }
    public void setLocalCustomerID(Integer localCustomerID) {
        this.localCustomerID = localCustomerID;
    }
    public Lead withLocalCustomerID(Integer localCustomerID) {
        this.localCustomerID = localCustomerID;
        return this;
    }

    public Integer getCustomerID() {
        return customerID;
    }
    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }
    public Lead withCustomerID(Integer customerID) {
        this.customerID = customerID;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public Lead withCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public Integer getContactID() {
        return contactID;
    }
    public void setContactID(Integer contactID) {
        this.contactID = contactID;
    }
    public Lead withContactID(Integer contactID) {
        this.contactID = contactID;
        return this;
    }

    public String getContactName() {
        return contactName;
    }
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    public Lead withcContactName(String contactName) {
        this.contactName = contactName;
        return this;
    }

    //Telephone

    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public Lead withTelephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    //Email

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Lead withEmail(String email) {
        this.email = email;
        return this;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public Lead withValue(String value) {
        this.value = value;
        return this;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public Lead withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public Lead withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }
    public Lead withUpdated(String updated) {
        this.updated = updated;
        return this;
    }

    public String getCreatedByDisplayName() {
        return createdByDisplayName;
    }
    public void setCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
    }
    public Lead withCreatedByDisplayName(String createdByDisplayName) {
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

    public boolean getIsNew() {
        return isNew;
    }
    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public int compareTo(Lead o) {

        int lexicographicallyDifferent = 0;

        if (getSubject() != null) {
            lexicographicallyDifferent = getSubject().compareTo(o.getSubject());
        }

        return lexicographicallyDifferent;
    }
}

package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Quote implements Comparable<Quote> {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("quoteID")
    @ColumnInfo(name = "quoteID")
    private Integer quoteID;

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

    @SerializedName("opportunityID")
    @ColumnInfo(name = "opportunityID")
    private Integer opportunityID;

    @SerializedName("contactName")
    @ColumnInfo(name = "contactName")
    private String contactName;

    @SerializedName("reference")
    @ColumnInfo(name = "reference")
    private String reference;

    @SerializedName("subject")
    @ColumnInfo(name = "subject")
    private String subject;

    @SerializedName("status")
    @ColumnInfo(name = "status")
    private String status;

    @SerializedName("currency")
    @ColumnInfo(name = "currency")
    private String currency;

    @SerializedName("exchangeRate")
    @ColumnInfo(name = "exchangeRate")
    private float exchangeRate;

    @SerializedName("quoteType")
    @ColumnInfo(name = "quoteType")
    private String quoteType;

    @SerializedName("targetDate")
    @ColumnInfo(name = "targetDate")
    private String targetDate;

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

    public Quote()
    {

    }

    @Ignore
    public Quote(int quoteID, String customerName, String status, String subject, int opportunityID, String quoteType, String notes, boolean isNew)
    {
        this.quoteID = quoteID;
        this.customerName = customerName;
        this.status = status;
        this.subject = subject;
        this.opportunityID = opportunityID;
        this.quoteType = quoteType;
        this.notes = notes;
        this.isNew = isNew;
    }

    @Ignore
    public Quote(int quoteID, String subject)
    {
        this.quoteID = quoteID;
        this.subject = subject;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuoteID() {
        return quoteID;
    }
    public void setQuoteID(Integer quoteID) {
        this.quoteID = quoteID;
    }
    public Quote withQuoteID(Integer quoteID) {
        this.quoteID = quoteID;
        return this;
    }

    public Integer getOpportunityID() {
        return opportunityID;
    }
    public void setOpportunityID(Integer opportunityID) {
        this.opportunityID = opportunityID;
    }
    public Quote withOpportunityID(Integer opportunityID) {
        this.opportunityID = opportunityID;
        return this;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Quote withStatus(String status) {
        this.status = status;
        return this;
    }

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public Quote withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public float getExchangeRate() {
        return exchangeRate;
    }
    public void setExchangeRate(float exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
    public Quote withExchangeRate(float exchangeRate) {
        this.exchangeRate = exchangeRate;
        return this;
    }

    public String getQuoteType() {
        return quoteType;
    }
    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }
    public Quote withQuoteType(String quoteType) {
        this.quoteType = quoteType;
        return this;
    }

    //LocalCustomerID

    public Integer getLocalCustomerID() {
        return localCustomerID;
    }
    public void setLocalCustomerID(Integer localCustomerID) {
        this.localCustomerID = localCustomerID;
    }
    public Quote withLocalCustomerID(Integer localCustomerID) {
        this.localCustomerID = localCustomerID;
        return this;
    }

    public Integer getCustomerID() {
        return customerID;
    }
    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }
    public Quote withCustomerID(Integer customerID) {
        this.customerID = customerID;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public Quote withCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public Integer getContactID() {
        return contactID;
    }
    public void setContactID(Integer contactID) {
        this.contactID = contactID;
    }
    public Quote withContactID(Integer contactID) {
        this.contactID = contactID;
        return this;
    }

    public String getContactName() {
        return contactName;
    }
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    public Quote withcContactName(String contactName) {
        this.contactName = contactName;
        return this;
    }

    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }
    public Quote withReference(String reference) {
        this.reference = reference;
        return this;
    }

    public String getTargetDate() {
        return targetDate;
    }
    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }
    public Quote withTargetDate(String targetDate) {
        this.targetDate = targetDate;
        return this;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public Quote withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public Quote withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }
    public Quote withUpdated(String updated) {
        this.updated = updated;
        return this;
    }

    public String getCreatedByDisplayName() {
        return createdByDisplayName;
    }
    public void setCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
    }
    public Quote withCreatedByDisplayName(String createdByDisplayName) {
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
    public int compareTo(Quote o) {

        int lexicographicallyDifferent = 0;

        if (getId() != null) {
            lexicographicallyDifferent = getId().compareTo(o.getId());
        }

        return lexicographicallyDifferent;
    }
}

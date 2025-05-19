package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Order implements Comparable<Order> {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("orderID")
    @ColumnInfo(name = "orderID")
    private Integer orderID;

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

    @SerializedName("closingDate")
    @ColumnInfo(name = "closingDate")
    private String closingDate;

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

    public Order()
    {

    }

    @Ignore
    public Order(int orderID, String customerName, String status, String subject, int opportunityID, String notes, boolean isNew)
    {
        this.orderID = orderID;
        this.customerName = customerName;
        this.status = status;
        this.subject = subject;
        this.opportunityID = opportunityID;
        this.notes = notes;
        this.isNew = isNew;
    }

    @Ignore
    public Order(int orderID, String subject)
    {
        this.orderID = orderID;
        this.subject = subject;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderID() {
        return orderID;
    }
    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }
    public Order withOrderID(Integer orderID) {
        this.orderID = orderID;
        return this;
    }

    public Integer getOpportunityID() {
        return opportunityID;
    }
    public void setOpportunityID(Integer opportunityID) {
        this.opportunityID = opportunityID;
    }
    public Order withOpportunityID(Integer opportunityID) {
        this.opportunityID = opportunityID;
        return this;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Order withStatus(String status) {
        this.status = status;
        return this;
    }

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public Order withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public float getExchangeRate() {
        return exchangeRate;
    }
    public void setExchangeRate(float exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
    public Order withExchangeRate(float exchangeRate) {
        this.exchangeRate = exchangeRate;
        return this;
    }

    //LocalCustomerID

    public Integer getLocalCustomerID() {
        return localCustomerID;
    }
    public void setLocalCustomerID(Integer localCustomerID) {
        this.localCustomerID = localCustomerID;
    }
    public Order withLocalCustomerID(Integer localCustomerID) {
        this.localCustomerID = localCustomerID;
        return this;
    }

    public Integer getCustomerID() {
        return customerID;
    }
    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }
    public Order withCustomerID(Integer customerID) {
        this.customerID = customerID;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public Order withCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public Integer getContactID() {
        return contactID;
    }
    public void setContactID(Integer contactID) {
        this.contactID = contactID;
    }
    public Order withContactID(Integer contactID) {
        this.contactID = contactID;
        return this;
    }

    public String getContactName() {
        return contactName;
    }
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    public Order withcContactName(String contactName) {
        this.contactName = contactName;
        return this;
    }

    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }
    public Order withReference(String reference) {
        this.reference = reference;
        return this;
    }

    public String getClosingDate() {
        return closingDate;
    }
    public void setClosingDate(String closingDate) {
        this.closingDate = closingDate;
    }
    public Order withClosingDate(String closingDate) {
        this.closingDate = closingDate;
        return this;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public Order withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public Order withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }
    public Order withUpdated(String updated) {
        this.updated = updated;
        return this;
    }

    public String getCreatedByDisplayName() {
        return createdByDisplayName;
    }
    public void setCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
    }
    public Order withCreatedByDisplayName(String createdByDisplayName) {
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
    public int compareTo(Order o) {

        int lexicographicallyDifferent = 0;

        if (getId() != null) {
            lexicographicallyDifferent = getId().compareTo(o.getId());
        }

        return lexicographicallyDifferent;
    }
}

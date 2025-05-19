package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Activity {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("journalEntryID")
    @ColumnInfo(name = "journalEntryID")
    private Integer journalEntryID;

    @SerializedName("localCustomerID")
    @ColumnInfo(name = "localCustomerID")
    private Integer localCustomerID;

    @SerializedName("customerID")
    @ColumnInfo(name = "customerID")
    private Integer customerID;

    @SerializedName("journalEntryType")
    @ColumnInfo(name = "journalEntryType")
    private String journalEntryType;

    @SerializedName("startDate")
    @ColumnInfo(name = "startDate")
    private String startDate;

    @SerializedName("endDate")
    @ColumnInfo(name = "endDate")
    private String endDate;

    @SerializedName("subject")
    @ColumnInfo(name = "subject")
    private String subject;

    @SerializedName("notes")
    @ColumnInfo(name = "notes")
    private String notes;

    @SerializedName("updated")
    @ColumnInfo(name = "updated")
    private String updated;

    @SerializedName("createdByDisplayName")
    @ColumnInfo(name = "createdByDisplayName")
    private String createdByDisplayName;

    @ColumnInfo(name = "isChanged")
    private boolean isChanged;

    @ColumnInfo(name = "isNew")
    private boolean isNew;

    public Activity()
    {

    }

    @Ignore
    public Activity(int journalEntryID, String journalEntryType, String startDate, String endDate, String subject, String notes, boolean isNew)
    {
        this.journalEntryID = journalEntryID;
        this.journalEntryType = journalEntryType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.subject = subject;
        this.notes = notes;
        this.isNew = isNew;
    }

    @Ignore
    public Activity(int journalEntryID, String journalEntryType)
    {
        this.journalEntryID = journalEntryID;
        this.journalEntryType = journalEntryType;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getJournalEntryID() {
        return journalEntryID;
    }
    public void setJournalEntryID(Integer journalEntryID) {
        this.journalEntryID = journalEntryID;
    }
    public Activity withJournalEntryID(Integer journalEntryID) {
        this.journalEntryID = journalEntryID;
        return this;
    }

    //LocalCustomerID

    public Integer getLocalCustomerID() {
        return localCustomerID;
    }
    public void setLocalCustomerID(Integer localCustomerID) {
        this.localCustomerID = localCustomerID;
    }
    public Activity withLocalCustomerID(Integer localCustomerID) {
        this.localCustomerID = localCustomerID;
        return this;
    }

    public Integer getCustomerID() {
        return customerID;
    }
    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }
    public Activity withCustomerID(Integer customerID) {
        this.customerID = customerID;
        return this;
    }

    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public Activity withStartDate(String startDate) {
        this.startDate = startDate;
        return this;
    }

    public String getJournalEntryType() {
        return journalEntryType;
    }
    public void setJournalEntryType(String journalEntryType) {
        this.journalEntryType = journalEntryType;
    }
    public Activity withJournalEntryType(String journalEntryType) {
        this.journalEntryType = journalEntryType;
        return this;
    }

    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public Activity withEndDate(String endDate) {
        this.endDate = endDate;
        return this;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public Activity withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public Activity withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }
    public Activity withUpdated(String updated) {
        this.updated = updated;
        return this;
    }

    public String getCreatedByDisplayName() {
        return createdByDisplayName;
    }
    public void setCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
    }
    public Activity withCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
        return this;
    }

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
}

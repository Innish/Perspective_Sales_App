package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Task implements Comparable<Task> {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("taskID")
    @ColumnInfo(name = "taskID")
    private Integer taskID;

    @SerializedName("localCustomerID")
    @ColumnInfo(name = "localCustomerID")
    private Integer localCustomerID;

    @SerializedName("customerID")
    @ColumnInfo(name = "customerID")
    private Integer customerID;

    @SerializedName("customerName")
    @ColumnInfo(name = "customerName")
    private String customerName;

    @SerializedName("dueDate")
    @ColumnInfo(name = "dueDate")
    private String dueDate;

    @SerializedName("subject")
    @ColumnInfo(name = "subject")
    private String subject;

    @SerializedName("notes")
    @ColumnInfo(name = "notes")
    private String notes;

    @SerializedName("complete")
    @ColumnInfo(name = "complete")
    private boolean complete;

    @SerializedName("updated")
    @ColumnInfo(name = "updated")
    private String updated;

    @SerializedName("createdByDisplayName")
    @ColumnInfo(name = "createdByDisplayName")
    private String createdByDisplayName;

    @SerializedName("isChanged")
    @ColumnInfo(name = "isChanged")
    private boolean isChanged;

    @SerializedName("isNew")
    @ColumnInfo(name = "isNew")
    private boolean isNew;

    public Task()
    {

    }

    @Ignore
    public Task(int taskID, String dueDate, String subject, String notes, boolean complete, boolean isNew)
    {
        this.taskID = taskID;
        this.dueDate = dueDate;
        this.subject = subject;
        this.notes = notes;
        this.complete = complete;
        this.isNew = isNew;
    }

    @Ignore
    public Task(int taskID, String subject)
    {
        this.taskID = taskID;
        this.subject = subject;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskID() {
        return taskID;
    }
    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }
    public Task withTaskID(Integer taskID) {
        this.taskID = taskID;
        return this;
    }

    //LocalCustomerID

    public Integer getLocalCustomerID() {
        return localCustomerID;
    }
    public void setLocalCustomerID(Integer localCustomerID) {
        this.localCustomerID = localCustomerID;
    }
    public Task withLocalCustomerID(Integer localCustomerID) {
        this.localCustomerID = localCustomerID;
        return this;
    }

    public Integer getCustomerID() {
        return customerID;
    }
    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }
    public Task withCustomerID(Integer customerID) {
        this.customerID = customerID;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public Task withCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getDueDate() {
        return dueDate;
    }
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
    public Task withDueDate(String dueDate) {
        this.dueDate = dueDate;
        return this;
    }


    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public Task withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public Task withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }
    public Task withUpdated(String updated) {
        this.updated = updated;
        return this;
    }

    public String getCreatedByDisplayName() {
        return createdByDisplayName;
    }
    public void setCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
    }
    public Task withCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
        return this;
    }

    public boolean getComplete() {
        return complete;
    }
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
    public Task withComplete(boolean complete) {
        this.complete = complete;
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

    @Override
    public int compareTo(Task o) {

        int lexicographicallyDifferent = 0;

        if (getDueDate() != null) {
            lexicographicallyDifferent = getDueDate().compareTo(o.getDueDate());
        }

        return lexicographicallyDifferent;
    }
}

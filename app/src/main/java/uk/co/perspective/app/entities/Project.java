package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Project implements Comparable<Project> {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("projectID")
    @ColumnInfo(name = "projectID")
    private Integer projectID;

    @SerializedName("localCustomerID")
    @ColumnInfo(name = "localCustomerID")
    private Integer localCustomerID;

    @SerializedName("customerID")
    @ColumnInfo(name = "customerID")
    private Integer customerID;

    @SerializedName("customerName")
    @ColumnInfo(name = "customerName")
    private String customerName;

    @SerializedName("projectName")
    @ColumnInfo(name = "projectName")
    private String projectName;

    @SerializedName("status")
    @ColumnInfo(name = "status")
    private String status;

    @SerializedName("reference")
    @ColumnInfo(name = "reference")
    private String reference;

    @SerializedName("details")
    @ColumnInfo(name = "details")
    private String details;

    @SerializedName("startDate")
    @ColumnInfo(name = "startDate")
    private String startDate;

    @SerializedName("endDate")
    @ColumnInfo(name = "endDate")
    private String endDate;

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

    public Project()
    {

    }

    @Ignore
    public Project(int projectID, String customerName, String projectName, String status, String details, String notes, boolean isNew)
    {
        this.projectID = projectID;
        this.customerName = customerName;
        this.projectName = projectName;
        this.status = status;
        this.details = details;
        this.notes = notes;
        this.isNew = isNew;
    }

    @Ignore
    public Project(int projectID, String projectName)
    {
        this.projectID = projectID;
        this.projectName = projectName;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectID() {
        return projectID;
    }
    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }
    public Project withProjectID(Integer projectID) {
        this.projectID = projectID;
        return this;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Project withStatus(String status) {
        this.status = status;
        return this;
    }

    //LocalCustomerID

    public Integer getLocalCustomerID() {
        return localCustomerID;
    }
    public void setLocalCustomerID(Integer localCustomerID) { this.localCustomerID = localCustomerID; }
    public Project withLocalCustomerID(Integer localCustomerID) {
        this.localCustomerID = localCustomerID;
        return this;
    }

    public Integer getCustomerID() {
        return customerID;
    }
    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }
    public Project withCustomerID(Integer customerID) {
        this.customerID = customerID;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public Project withCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }
    public Project withReference(String reference) {
        this.reference = reference;
        return this;
    }

    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }
    public Project withDetails(String details) {
        this.details = details;
        return this;
    }

    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public Project withStartDate(String startDate) {
        this.startDate = startDate;
        return this;
    }

    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public Project withEndDate(String endDate) {
        this.endDate = endDate;
        return this;
    }

    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public Project withProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public Project withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }
    public Project withUpdated(String updated) {
        this.updated = updated;
        return this;
    }

    public String getCreatedByDisplayName() {
        return createdByDisplayName;
    }
    public void setCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
    }
    public Project withCreatedByDisplayName(String createdByDisplayName) {
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
    public int compareTo(Project p) {

        int lexicographicallyDifferent = 0;

        if (getProjectName() != null) {
            lexicographicallyDifferent = getProjectName().compareTo(p.getProjectName());
        }

        return lexicographicallyDifferent;
    }
}

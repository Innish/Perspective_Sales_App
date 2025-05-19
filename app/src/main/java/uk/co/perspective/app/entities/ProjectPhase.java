package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class ProjectPhase implements Comparable<ProjectPhase> {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("phaseID")
    @ColumnInfo(name = "phaseID")
    private Integer phaseID;

    @SerializedName("projectID")
    @ColumnInfo(name = "projectID")
    private Integer projectID;

    @SerializedName("localProjectID")
    @ColumnInfo(name = "localProjectID")
    private Integer localProjectID;

    @SerializedName("phaseName")
    @ColumnInfo(name = "phaseName")
    private String phaseName;

    @SerializedName("description")
    @ColumnInfo(name = "description")
    private String description;

    @SerializedName("startDate")
    @ColumnInfo(name = "startDate")
    private String startDate;

    @SerializedName("endDate")
    @ColumnInfo(name = "endDate")
    private String endDate;

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

    public ProjectPhase()
    {

    }

    @Ignore
    public ProjectPhase(Integer localProjectID, String phaseName, String description)
    {
        this.localProjectID = localProjectID;
        this.phaseName = phaseName;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPhaseID() {
        return phaseID;
    }
    public void setPhaseID(Integer phaseID) {
        this.phaseID = phaseID;
    }

    public Integer getProjectID() {
        return projectID;
    }
    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }

    public Integer getLocalProjectID() {
        return localProjectID;
    }
    public void setLocalProjectID(Integer localProjectID) {
        this.localProjectID = localProjectID;
    }

    public String getPhaseName() {
        return phaseName;
    }
    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }


    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public ProjectPhase withDescription(String description) {
        this.description = description;
        return this;
    }

    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public ProjectPhase withStartDate(String startDate) {
        this.startDate = startDate;
        return this;
    }

    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public ProjectPhase withEndDate(String endDate) {
        this.endDate = endDate;
        return this;
    }

    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }
    public ProjectPhase withUpdated(String updated) {
        this.updated = updated;
        return this;
    }

    public String getCreatedByDisplayName() {
        return createdByDisplayName;
    }
    public void setCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
    }
    public ProjectPhase withCreatedByDisplayName(String createdByDisplayName) {
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
    public int compareTo(ProjectPhase p) {

        int lexicographicallyDifferent = 0;

        if (getPhaseName() != null) {
            lexicographicallyDifferent = getPhaseName().compareTo(p.getPhaseName());
        }

        return lexicographicallyDifferent;
    }
}

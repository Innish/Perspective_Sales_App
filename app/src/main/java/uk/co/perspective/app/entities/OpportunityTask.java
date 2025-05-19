package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class OpportunityTask implements Comparable<OpportunityTask> {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("opportunityID")
    @ColumnInfo(name = "opportunityID")
    private Integer opportunityID;

    @SerializedName("taskID")
    @ColumnInfo(name = "taskID")
    private Integer taskID;

    @SerializedName("localOpportunityID")
    @ColumnInfo(name = "localOpportunityID")
    private Integer localOpportunityID;

    @SerializedName("localTaskID")
    @ColumnInfo(name = "localTaskID")
    private Integer localTaskID;

    public OpportunityTask()
    {

    }

    @Ignore
    public OpportunityTask(int localOpportunityID, int localTaskID)
    {
        this.localOpportunityID = localOpportunityID;
        this.localTaskID = localTaskID;
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

    public Integer getTaskID() {
        return taskID;
    }
    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }

    public Integer getLocalOpportunityID() {
        return localOpportunityID;
    }
    public void setLocalOpportunityID(Integer localOpportunityID) {
        this.localOpportunityID = localOpportunityID;
    }

    public Integer getLocalTaskID() {
        return localTaskID;
    }
    public void setLocalTaskID(Integer localTaskID) {
        this.localTaskID = localTaskID;
    }

    @Override
    public int compareTo(OpportunityTask p) {

        int lexicographicallyDifferent = 0;

        if (getLocalTaskID() != null) {
            lexicographicallyDifferent = getLocalTaskID().compareTo(p.getLocalTaskID());
        }

        return lexicographicallyDifferent;
    }
}

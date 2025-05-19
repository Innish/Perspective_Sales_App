package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class ProjectTask implements Comparable<ProjectTask> {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("projectID")
    @ColumnInfo(name = "projectID")
    private Integer projectID;

    @SerializedName("taskID")
    @ColumnInfo(name = "taskID")
    private Integer taskID;

    @SerializedName("phaseID")
    @ColumnInfo(name = "phaseID")
    private Integer phaseID;

    @SerializedName("localProjectID")
    @ColumnInfo(name = "localProjectID")
    private Integer localProjectID;

    @SerializedName("localTaskID")
    @ColumnInfo(name = "localTaskID")
    private Integer localTaskID;

    @SerializedName("localTaskPhaseID")
    @ColumnInfo(name = "localTaskPhaseID")
    private Integer localTaskPhaseID;

    public ProjectTask()
    {

    }

    @Ignore
    public ProjectTask(int localProjectID, int localTaskID, int localTaskPhaseID)
    {
        this.localProjectID = localProjectID;
        this.localTaskID = localTaskID;
        this.localTaskPhaseID = localTaskPhaseID;
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

    public Integer getTaskID() {
        return taskID;
    }
    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }

    public Integer getPhaseID() {
        return phaseID;
    }
    public void setPhaseID(Integer phaseID) {
        this.phaseID = phaseID;
    }

    public Integer getLocalProjectID() {
        return localProjectID;
    }
    public void setLocalProjectID(Integer localProjectID) {
        this.localProjectID = localProjectID;
    }

    public Integer getLocalTaskID() {
        return localTaskID;
    }
    public void setLocalTaskID(Integer localTaskID) {
        this.localTaskID = localTaskID;
    }

    public Integer getLocalTaskPhaseID() {
        return localTaskPhaseID;
    }
    public void setLocalTaskPhaseID(Integer localTaskPhaseID) {
        this.localTaskPhaseID = localTaskPhaseID;
    }

    @Override
    public int compareTo(ProjectTask p) {

        int lexicographicallyDifferent = 0;

        if (getLocalTaskPhaseID() != null) {
            lexicographicallyDifferent = getLocalTaskPhaseID().compareTo(p.getLocalTaskPhaseID());
        }

        return lexicographicallyDifferent;
    }
}

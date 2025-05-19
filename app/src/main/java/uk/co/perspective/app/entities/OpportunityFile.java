package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class OpportunityFile implements Comparable<OpportunityFile> {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("localOpportunityID")
    @ColumnInfo(name = "localOpportunityID")
    private Integer localOpportunityID;

    @SerializedName("filename")
    @ColumnInfo(name = "filename")
    private String filename;

    @SerializedName("filepath")
    @ColumnInfo(name = "filepath")
    private String filepath;

    @ColumnInfo(name = "isNew")
    private boolean isNew;

    public OpportunityFile()
    {

    }

    @Ignore
    public OpportunityFile(int localOpportunityID, String filename, String filepath)
    {
        this.localOpportunityID = localOpportunityID;
        this.filename = filename;
        this.filepath = filepath;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLocalOpportunityID() {
        return localOpportunityID;
    }
    public void setLocalOpportunityID(Integer localOpportunityID) {
        this.localOpportunityID = localOpportunityID;
    }

    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public OpportunityFile withFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public String getFilepath() {
        return filepath;
    }
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    public OpportunityFile withFilepath(String filepath) {
        this.filepath = filepath;
        return this;
    }

    public boolean getIsNew() {
        return isNew;
    }
    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public int compareTo(OpportunityFile p) {

        int lexicographicallyDifferent = 0;

        if (getFilename() != null) {
            lexicographicallyDifferent = getFilename().compareTo(p.getFilename());
        }

        return lexicographicallyDifferent;
    }
}

package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class ProjectFile implements Comparable<ProjectFile> {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("localProjectID")
    @ColumnInfo(name = "localProjectID")
    private Integer localProjectID;

    @SerializedName("filename")
    @ColumnInfo(name = "filename")
    private String filename;

    @SerializedName("filepath")
    @ColumnInfo(name = "filepath")
    private String filepath;

    @ColumnInfo(name = "isNew")
    private boolean isNew;

    public ProjectFile()
    {

    }

    @Ignore
    public ProjectFile(int localProjectID, String filename, String filepath)
    {
        this.localProjectID = localProjectID;
        this.filename = filename;
        this.filepath = filepath;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLocalProjectID() {
        return localProjectID;
    }
    public void setLocalProjectID(Integer localProjectID) {
        this.localProjectID = localProjectID;
    }

    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public ProjectFile withFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public String getFilepath() {
        return filepath;
    }
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    public ProjectFile withFilepath(String filepath) {
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
    public int compareTo(ProjectFile p) {

        int lexicographicallyDifferent = 0;

        if (getFilename() != null) {
            lexicographicallyDifferent = getFilename().compareTo(p.getFilename());
        }

        return lexicographicallyDifferent;
    }
}

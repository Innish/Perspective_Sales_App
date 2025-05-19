package uk.co.perspective.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.co.perspective.app.entities.ProjectFile;

@Dao
public interface ProjectFileDao {

    @Query("SELECT * FROM projectFile")
    List<ProjectFile> getAllImages();

    @Query("SELECT * FROM projectFile WHERE localProjectID = :localProjectID")
    List<ProjectFile> getAllProjectFiles(int localProjectID);

    @Query("SELECT * FROM projectFile WHERE isNew = 1")
    List<ProjectFile> getNewProjectFiles();

    @Query("SELECT * FROM projectFile WHERE id =:id")
    ProjectFile getProjectFile(int id);

    @Query("DELETE FROM projectFile WHERE id =:id")
    void deleteProjectFile(int id);

    @Query("UPDATE projectFile SET isNew = :isNew WHERE id = :intID")
    void updateIsNew(int intID, boolean isNew);

    @Insert
    void insert(ProjectFile projectFile);

    @Delete
    void delete(ProjectFile projectFile);

    @Update
    void update(ProjectFile projectFile);
}

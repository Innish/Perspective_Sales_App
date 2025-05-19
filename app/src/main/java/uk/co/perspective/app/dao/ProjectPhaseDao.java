package uk.co.perspective.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.co.perspective.app.entities.ProjectPhase;

@Dao
public interface ProjectPhaseDao {

    @Query("SELECT * FROM projectphase")
    List<ProjectPhase> getAll();

    @Query("SELECT * FROM projectphase WHERE localProjectID =:localProjectID")
    List<ProjectPhase> getAllProjectPhases(int localProjectID);

    @Query("SELECT * FROM projectphase WHERE isNew = 1")
    List<ProjectPhase> getNewProjectPhases();

    @Query("SELECT * FROM projectphase WHERE phaseID =:phaseID")
    ProjectPhase getProjectPhaseByPhaseID(int phaseID);

    @Query("SELECT * FROM projectphase WHERE id =:id")
    ProjectPhase getProjectPhase(int id);

    @Query("DELETE FROM projectphase WHERE id =:id")
    void deleteProjectPhase(int id);

    @Query("SELECT count(*) FROM ProjectPhase WHERE phaseID = :phaseID")
    int getProjectPhaseCount(int phaseID);

    @Query("SELECT count(*) FROM ProjectPhase WHERE phaseName = :phaseName AND description = :description AND startDate = :startDate AND endDate = :endDate AND IFNULL(phaseID, 0) = 0")
    int getProjectPhaseCount(String phaseName, String description, String startDate, String endDate);

    @Query("UPDATE projectphase SET phaseName = :phaseName, description = :description, startDate = :startDate, endDate = :endDate, updated = :updated, ischanged = 1 WHERE phaseID = :phaseID")
    void updateProjectPhaseByPhaseID(String phaseName, String description, String startDate, String endDate, String updated, int phaseID);

    @Query("UPDATE projectphase SET isNew = :isNew, phaseID = :phaseID WHERE id = :id")
    void updateProjectPhaseIsNew(int id, int isNew, int phaseID);

    @Query("UPDATE projectphase SET isChanged = :isChanged WHERE id = :id")
    void updateProjectPhaseIsChanged(int id, int isChanged);

    @Insert
    Long insert(ProjectPhase projectPhase);

    @Delete
    void delete(ProjectPhase projectPhase);

    @Update
    void update(ProjectPhase projectPhase);
}

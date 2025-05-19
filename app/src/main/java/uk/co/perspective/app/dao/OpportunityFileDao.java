package uk.co.perspective.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.co.perspective.app.entities.OpportunityFile;
import uk.co.perspective.app.entities.ProjectFile;

@Dao
public interface OpportunityFileDao {

    @Query("SELECT * FROM opportunityFile")
    List<OpportunityFile> getAllImages();

    @Query("SELECT * FROM opportunityFile WHERE localOpportunityID = :localOpportunityID")
    List<OpportunityFile> getAllOpportunityFiles(int localOpportunityID);

    @Query("SELECT * FROM opportunityFile WHERE isNew = 1")
    List<OpportunityFile> getNewOpportunityFiles();

    @Query("SELECT * FROM opportunityFile WHERE id =:id")
    OpportunityFile getOpportunityFile(int id);

    @Query("DELETE FROM opportunityFile WHERE id =:id")
    void deleteOpportunityFile(int id);

    @Query("UPDATE opportunityFile SET isNew = :isNew WHERE id = :intID")
    void updateIsNew(int intID, boolean isNew);

    @Insert
    void insert(OpportunityFile opportunityFile);

    @Delete
    void delete(OpportunityFile opportunityFile);

    @Update
    void update(OpportunityFile opportunityFile);
}

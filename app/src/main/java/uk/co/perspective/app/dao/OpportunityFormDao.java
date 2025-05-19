package uk.co.perspective.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.co.perspective.app.entities.OpportunityForm;

@Dao
public interface OpportunityFormDao {

    @Query("SELECT * FROM opportunityForm")
    List<OpportunityForm> getAllForms();

    @Query("SELECT * FROM opportunityForm WHERE localOpportunityID = :localOpportunityID")
    List<OpportunityForm> getAllOpportunityForms(int localOpportunityID);

    @Query("SELECT * FROM opportunityForm WHERE isNew = 1")
    List<OpportunityForm> getNewOpportunityForms();

    @Query("SELECT * FROM opportunityForm WHERE id =:id")
    OpportunityForm getOpportunityForm(int id);

    @Query("UPDATE opportunityForm SET formData = :formData WHERE id =:id")
    void updateOpportunityForm(String formData, int id);

    @Query("DELETE FROM opportunityForm WHERE id =:id")
    void deleteOpportunityForm(int id);

    @Query("UPDATE opportunityForm SET isNew = :isNew WHERE opportunityID = :opportunityID")
    void updateIsNew(int opportunityID, boolean isNew);

    @Query("UPDATE opportunityForm SET isComplete = :isComplete WHERE id = :intID")
    void updateIsComplete(int intID, boolean isComplete);

    @Insert
    long insert(OpportunityForm opportunityFile);

    @Delete
    void delete(OpportunityForm opportunityFile);

    @Update
    void update(OpportunityForm opportunityFile);
}

package uk.co.perspective.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.co.perspective.app.entities.Lead;
import uk.co.perspective.app.entities.LeadsSummary;

@Dao
public interface LeadDao {

    @Query("SELECT * FROM lead")
    List<Lead> getAll();

    @Query("SELECT * FROM lead WHERE subject like '%' || :searchText || '%' OR notes like '%' || :searchText || '%' OR customerName like '%' || :searchText || '%'")
    List<Lead> searchLeads(String searchText);

    @Query("SELECT * FROM lead WHERE isNew = 1")
    List<Lead> getNewLeads();

    @Query("SELECT * FROM lead WHERE isChanged = 1")
    List<Lead> getChangedLeads();

    @Query("SELECT * FROM lead WHERE status like '%' || :status || '%'")
    List<Lead> getLeadsByStatus(String status);

    @Query("SELECT * FROM lead WHERE datetime(updated) BETWEEN :startDate AND :endDate")
    List<Lead> getLeadsInRange(String startDate, String endDate);

    @Query("SELECT * FROM lead WHERE id =:id")
    Lead getLead(int id);

    @Query("SELECT * FROM lead WHERE leadID =:leadID")
    Lead getLeadByLeadID(int leadID);

    @Query("UPDATE lead SET isArchived = 1 WHERE id =:id")
    void archiveLead(int id);

    @Query("SELECT count(*) FROM lead WHERE leadID = :leadID")
    int getLeadCount(int leadID);

    @Query("SELECT count(*) FROM lead WHERE subject = :subject AND notes = :notes AND IFNULL(leadID, 0) = 0")
    int getLeadCount(String subject, String notes);

    @Query("DELETE FROM lead WHERE id =:id")
    void deleteLead(int id);

    @Query("DELETE FROM lead WHERE isArchived = 1")
    void deleteAllArchivedLeads();

    @Query("UPDATE lead SET subject = :subject, rating = :rating, status = :status, value = :value, notes = :notes WHERE id = :id")
    void updateLead(String subject, String rating, String status, String value, String notes, int id);

    @Query("UPDATE lead SET subject = :subject, rating = :rating, status = :status, value = :value, notes = :notes, updated = :updated WHERE leadID = :leadID")
    void updateLeadByLeadID(String subject, String rating, String status, String value, String notes, String updated, int leadID);

    @Query("UPDATE lead SET isChanged = :isChanged WHERE id = :id")
    void updateLeadIsChanged(int id, int isChanged);

    @Query("UPDATE lead SET isNew = :isNew, leadID = :leadID WHERE id = :id")
    void updateLeadIsNew(int id, int isNew, int leadID);

    //Reports

    @Query("SELECT Count(*) AS statusCount, status FROM lead WHERE isArchived = 0 GROUP BY status")
    List<LeadsSummary> getLeadSummary();

    @Insert
    long insert(Lead lead);

    @Delete
    void delete(Lead lead);

    @Update
    void update(Lead lead);
}

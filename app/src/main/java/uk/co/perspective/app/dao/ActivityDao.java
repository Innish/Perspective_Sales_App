package uk.co.perspective.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.co.perspective.app.entities.Activity;

@Dao
public interface ActivityDao {

    @Query("SELECT * FROM activity")
    List<Activity> getAll();

    @Query("SELECT * FROM activity WHERE localCustomerID =:localCustomerID")
    List<Activity> getCustomerActivity(int localCustomerID);

    @Query("SELECT * FROM activity WHERE customerID =:customerID")
    List<Activity> getCustomerActivityByCustomerID(int customerID);

    @Query("SELECT * FROM activity WHERE isNew = 1")
    List<Activity> getNewActivities();

    @Query("SELECT * FROM activity WHERE isChanged = 1")
    List<Activity> getChangedActivities();

    @Query("SELECT * FROM activity WHERE subject like '%' + :searchText + '%' OR notes like '%' + :searchText + '%'")
    List<Activity> searchActivity(String searchText);

    @Query("SELECT * FROM activity WHERE journalEntryID =:journalEntryID")
    Activity getActivityByJournalEntryID(int journalEntryID);

    @Query("SELECT * FROM activity WHERE id =:id")
    Activity getActivity(int id);

    @Query("DELETE FROM activity WHERE id =:id")
    void deleteActivity(int id);

    @Query("SELECT count(*) FROM activity WHERE journalEntryID = :journalEntryID")
    int getActivityCount(int journalEntryID);

    @Query("SELECT count(*) FROM activity WHERE subject = :subject AND notes = :notes AND IFNULL(journalEntryID, 0) = 0")
    int getActivityCount(String subject, String notes);

    @Query("UPDATE activity SET journalEntryType = :journalEntryType, startDate = :startDate, endDate = :endDate, subject = :subject, notes = :notes, ischanged = 1, updated = :updated WHERE id = :id")
    void updateActivity(String journalEntryType, String startDate, String endDate, String subject, String notes, String updated, int id);

    @Query("UPDATE activity SET journalEntryType = :journalEntryType, startDate = :startDate, endDate = :endDate, subject = :subject, notes = :notes, ischanged = 0, isNew = 0, updated = :updated WHERE journalEntryID = :journalEntryID")
    void updateActivityByJournalEntryID(int journalEntryID, String journalEntryType, String startDate, String endDate, String subject, String notes, String updated);

    @Query("UPDATE activity SET isChanged = :isChanged WHERE id = :id")
    void updateActivityIsChanged(int id, int isChanged);

    @Query("UPDATE activity SET isNew = :isNew, journalEntryID = :journalEntryID, customerID = :customerID WHERE id = :id")
    void updateActivityIsNew(int id, int isNew, int journalEntryID, int customerID);

    @Insert
    long insert(Activity activity);

    @Delete
    void delete(Activity activity);

    @Update
    void update(Activity activity);
}

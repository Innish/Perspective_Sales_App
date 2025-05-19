package uk.co.perspective.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.co.perspective.app.entities.Task;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM task")
    List<Task> getAll();

    @Query("SELECT * FROM task WHERE subject like '%' || :searchText || '%' OR notes like '%' || :searchText || '%'")
    List<Task> searchTasks(String searchText);

    @Query("SELECT * FROM task order by id desc limit 10")
    List<Task> getRecentTasks();

    @Query("SELECT * FROM task WHERE complete = 0")
    List<Task> getPendingTasks();

    @Query("SELECT * FROM task WHERE complete = 1")
    List<Task> getCompleteTasks();

    @Query("SELECT * FROM task WHERE isNew = 1")
    List<Task> getNewTasks();

    @Query("SELECT * FROM task WHERE isChanged = 1")
    List<Task> getChangedTasks();

    @Query("SELECT * FROM task WHERE datetime(dueDate) BETWEEN :startDate AND :endDate")
    List<Task> getTasksInRange(String startDate, String endDate);

    @Query("SELECT * FROM task WHERE id =:id")
    Task getTask(int id);

    @Query("SELECT * FROM task WHERE taskID =:taskID")
    Task getTaskByTaskID(int taskID);

    @Query("SELECT count(*) FROM task WHERE taskID = :taskID")
    int getTaskCount(int taskID);

    @Query("SELECT count(*) FROM task WHERE subject = :subject AND notes = :notes AND dueDate = :dueDate AND IFNULL(taskID, 0) = 0")
    int getTaskCount(String subject, String notes, String dueDate);

    @Query("DELETE FROM task WHERE id =:id")
    void deleteTask(int id);

    @Query("UPDATE task SET subject = :subject, notes = :notes WHERE id = :id")
    void updateTask(String subject, String notes, int id);

    @Query("UPDATE task SET complete = :complete WHERE id = :id")
    void updateTaskComplete(int id, int complete);

    @Query("UPDATE task SET isNew = :isNew, TaskID = :TaskID WHERE id = :id")
    void updateTaskIsNew(int id, int isNew, int TaskID);

    @Query("UPDATE task SET isChanged = :isChanged WHERE id = :id")
    void updateTaskIsChanged(int id, int isChanged);

    @Query("UPDATE task SET subject = :subject, customerID = :customerID, customerName = :customerName, dueDate = :dueDate, notes =:notes, updated = :updated WHERE taskID = :taskID")
    void updateTaskByTaskID(int taskID, String subject, int customerID, String customerName, String dueDate, String notes, String updated);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Task task);

    @Delete
    void delete(Task task);

    @Update
    void update(Task task);
}

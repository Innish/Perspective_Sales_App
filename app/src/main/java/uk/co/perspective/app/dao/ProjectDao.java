package uk.co.perspective.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.co.perspective.app.entities.Project;
import uk.co.perspective.app.entities.ProjectTask;
import uk.co.perspective.app.joins.JoinProjectTask;

@Dao
public interface ProjectDao {

    @Query("SELECT * FROM project")
    List<Project> getAll();

    @Query("SELECT * FROM project WHERE projectName like '%' || :searchText || '%' or customerName like '%' || :searchText || '%'")
    List<Project> searchProjects(String searchText);

    @Query("SELECT * FROM project WHERE isChanged = 1")
    List<Project> getChangedProjects();

    @Query("SELECT * FROM project WHERE isNew = 1")
    List<Project> getNewProjects();

    @Query("SELECT * FROM project WHERE id =:id")
    Project getProject(int id);

    @Query("SELECT * FROM project WHERE projectID =:projectID")
    Project getProjectByProjectID(int projectID);

    @Query("SELECT * FROM project WHERE status like '%' || :status || '%'")
    List<Project> getProjectsByStatus(String status);

    @Query("SELECT * FROM project WHERE datetime(startDate) BETWEEN :startDate AND :endDate")
    List<Project> getProjectsInRange(String startDate, String endDate);

    @Query("SELECT * FROM ProjectTask WHERE localProjectID = :id")
    List<ProjectTask> getAllProjectTasks(int id);

    @Query("SELECT * FROM ProjectTask LEFT JOIN Task ON ProjectTask.localTaskID = Task.id WHERE localProjectID = :id AND isNew = 1")
    List<ProjectTask> getAllNewProjectTasks(int id);

    @Query("SELECT * FROM ProjectTask WHERE localTaskID = :id")
    ProjectTask getProjectTaskByLocalTaskID(int id);

    @Query("SELECT task.id, task.subject, task.notes, task.dueDate, task.complete, projectphase.ID AS phaseLocalID, projectphase.phaseName, projectphase.description AS phaseDescription, 0 AS header, ProjectTask.localProjectID FROM ProjectTask LEFT JOIN projectphase ON ProjectTask.localTaskPhaseID = projectphase.id LEFT JOIN task ON ProjectTask.localTaskID = task.id  WHERE ProjectTask.localProjectID = :id")
    List<JoinProjectTask> getProjectTasks(int id);


    @Query("DELETE FROM ProjectTask WHERE localTaskID =:localTaskID AND localProjectID =:localProjectID")
    void deleteProjectTask(int localTaskID, int localProjectID);

    @Query("UPDATE project SET isArchived = 1 WHERE id =:id")
    void archiveProject(int id);

    @Query("UPDATE ProjectTask SET taskID = :taskID, phaseID = :phaseID, projectID = :projectID  WHERE id =:id")
    void updateProjectTask(int id, int taskID, int phaseID, int projectID);

    @Query("SELECT count(*) FROM project WHERE projectID = :projectID")
    int getProjectCount(int projectID);

    @Query("SELECT count(*) FROM ProjectTask WHERE projectID = :projectID AND taskID = :taskID")
    int getProjectTaskCount(int projectID, int taskID);

    @Query("SELECT count(*) FROM project WHERE projectName = :projectName AND startDate = :startDate AND IFNULL(projectID, 0) = 0")
    int getProjectCount(String projectName, String startDate);

    @Query("DELETE FROM project WHERE id =:id")
    void deleteProject(int id);

    @Query("UPDATE project SET projectName = :projectName, status = :status, reference = :reference, details = :details, startDate = :startDate, endDate = :endDate, notes = :notes, updated = :updated, ischanged = 1 WHERE id = :id")
    void updateProject(String projectName, String status, String reference, String details, String startDate, String endDate, String notes, String updated, int id);

    @Query("UPDATE project SET projectName = :projectName, status = :status, reference = :reference, details = :details, startDate = :startDate, endDate = :endDate, notes = :notes, updated = :updated WHERE projectID = :projectID")
    void updateProjectByProjectID(String projectName, String status, String reference, String details, String startDate, String endDate, String notes, String updated, int projectID);

    @Query("UPDATE project SET isNew = :isNew, projectID = :projectID WHERE id = :id")
    void updateProjectIsNew(int id, int isNew, int projectID);

    @Query("UPDATE project SET isChanged = :isChanged WHERE id = :id")
    void updateProjectIsChanged(int id, int isChanged);

    @Insert
    long insert(Project project);

    @Insert
    void insert(ProjectTask projectTask);

    @Delete
    void delete(Project project);

    @Update
    void update(Project project);
}

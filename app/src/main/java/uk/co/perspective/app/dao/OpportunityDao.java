package uk.co.perspective.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.co.perspective.app.entities.Opportunity;
import uk.co.perspective.app.entities.OpportunityContact;
import uk.co.perspective.app.entities.OpportunityQuote;
import uk.co.perspective.app.entities.OpportunitySummary;
import uk.co.perspective.app.joins.JoinOpportunityContact;
import uk.co.perspective.app.joins.JoinOpportunityQuote;
import uk.co.perspective.app.joins.JoinOpportunityTask;
import uk.co.perspective.app.joins.JoinProjectTask;

@Dao
public interface OpportunityDao {

    @Query("SELECT * FROM opportunity")
    List<Opportunity> getAll();

    @Query("SELECT * FROM opportunity WHERE subject like '%' || :searchText || '%' OR notes like '%' || :searchText || '%' OR customerName like '%' || :searchText || '%'")
    List<Opportunity> searchOpportunities(String searchText);

    @Query("SELECT * FROM opportunity WHERE isChanged = 1")
    List<Opportunity> getChangedOpportunities();

    @Query("SELECT * FROM opportunity WHERE isNew = 1")
    List<Opportunity> getNewOpportunities();

    @Query("SELECT * FROM opportunity WHERE id =:id")
    Opportunity getOpportunity(int id);

    @Query("SELECT * FROM opportunity WHERE status like '%' || :status || '%'")
    List<Opportunity> getOpportunitiesByStatus(String status);

    @Query("SELECT * FROM opportunity WHERE datetime(updated) BETWEEN :startDate AND :endDate")
    List<Opportunity> getOpportunitiesInRange(String startDate, String endDate);

    @Query("SELECT * FROM OpportunityQuote WHERE localOpportunityID = :id")
    List<OpportunityQuote> getOpportunityQuotesByLocalOpportunityID(int id);

    @Query("SELECT * FROM OpportunityContact WHERE localOpportunityID = :id")
    List<OpportunityContact> getOpportunityContactsByLocalOpportunityID(int id);

    @Query("SELECT * FROM opportunity WHERE opportunityID =:opportunityID")
    Opportunity getOpportunityByOpportunityID(int opportunityID);

    @Query("UPDATE opportunity SET isArchived = 1 WHERE id =:id")
    void archiveOpportunity(int id);

    @Query("DELETE FROM opportunity WHERE id =:id")
    void deleteOpportunity(int id);

    @Query("UPDATE opportunity SET subject = :subject, rating = :rating, status = :status, value = :value, probability = :probability, details = :details, targetDate = :targetDate, notes = :notes WHERE id = :id")
    void updateOpportunity(String subject, String rating, String status, String value, int probability, String details, String targetDate, String notes, int id);

    @Query("SELECT task.id, task.subject, task.notes, task.dueDate, task.complete, localOpportunityID, 0 AS header FROM OpportunityTask LEFT JOIN Opportunity ON OpportunityTask.opportunityID = Opportunity.opportunityID LEFT JOIN task ON OpportunityTask.localTaskID = task.id  WHERE OpportunityTask.localOpportunityID = :id")
    List<JoinOpportunityTask> getOpportunityTasks(int id);

    @Query("DELETE FROM OpportunityTask WHERE localTaskID =:localTaskID AND localOpportunityID =:localOpportunityID")
    void deleteOpportunityTask(int localTaskID, int localOpportunityID);

    @Query("SELECT contact.id, contact.contactName, customer.customerName, contact.jobTitle, opportunity.subject FROM OpportunityContact LEFT JOIN contact ON OpportunityContact.localContactID = contact.id LEFT JOIN opportunity ON OpportunityContact.localOpportunityID = opportunity.id LEFT JOIN customer ON contact.localCustomerID = customer.id WHERE opportunity.id = :id")
    List<JoinOpportunityContact> getOpportunityContacts(int id);

    @Query("SELECT contact.id, contact.contactName, customer.customerName, contact.jobTitle, opportunity.subject FROM OpportunityContact LEFT JOIN contact ON OpportunityContact.localContactID = contact.id LEFT JOIN opportunity ON OpportunityContact.localOpportunityID = opportunity.id LEFT JOIN customer ON contact.localCustomerID = customer.id WHERE opportunity.opportunityID = :opportunityID")
    List<JoinOpportunityContact> getOpportunityContactsByOpportunityID(int opportunityID);

    @Query("SELECT quote.id, quote.subject, quote.customerName, quote.status, quote.quoteType FROM OpportunityQuote LEFT JOIN quote ON OpportunityQuote.localQuoteID = quote.id LEFT JOIN opportunity ON OpportunityQuote.localOpportunityID = opportunity.id WHERE opportunity.id = :id")
    List<JoinOpportunityQuote> getOpportunityQuotes(int id);

    @Query("SELECT quote.id, quote.subject, quote.customerName, quote.status, quote.quoteType FROM OpportunityQuote LEFT JOIN quote ON OpportunityQuote.localQuoteID = quote.id LEFT JOIN opportunity ON OpportunityQuote.localOpportunityID = opportunity.id WHERE opportunity.opportunityID = :opportunityID AND quote.quoteID != 0")
    List<JoinOpportunityQuote> getOpportunityQuotesByOpportunityID(int opportunityID);

    @Query("UPDATE OpportunityContact SET localContactID = :localContactID WHERE opportunityID = :opportunityID AND contactID = :contactID")
    void updateOpportunityContactLocalID(int opportunityID, int contactID, int localContactID);

    @Query("UPDATE OpportunityQuote SET localQuoteID = :localQuoteID WHERE opportunityID = :opportunityID AND quoteID = :quoteID")
    void updateOpportunityQuoteLocalID(int opportunityID, int quoteID, int localQuoteID);

    @Query("UPDATE OpportunityQuote SET quoteID = :quoteID, opportunityID = :opportunityID WHERE id = :id")
    void updateOpportunityQuote(int id, int quoteID, int opportunityID);

    @Query("UPDATE OpportunityContact SET contactID = :contactID, opportunityID = :opportunityID WHERE id = :id")
    void updateOpportunityContact(int id, int contactID, int opportunityID);

    @Query("SELECT count(*) FROM opportunity WHERE opportunityID = :opportunityID")
    int getOpportunityCount(int opportunityID);

    @Query("SELECT count(*) FROM opportunity WHERE subject = :subject AND notes = :notes AND IFNULL(opportunityID, 0) = 0")
    int getOpportunityCount(String subject, String notes);

    @Query("DELETE FROM OpportunityContact WHERE localOpportunityID =:localOpportunityID AND localContactID =:localContactID")
    void deleteOpportunityContact(int localOpportunityID, int localContactID);

    @Query("SELECT count(*) FROM OpportunityContact WHERE OpportunityID = :OpportunityID AND ContactID = :ContactID")
    int getOpportunityContactCount(int OpportunityID, int ContactID);

    @Query("DELETE FROM OpportunityQuote WHERE localOpportunityID =:localOpportunityID AND localQuoteID =:localQuoteID")
    void deleteOpportunityQuote(int localOpportunityID, int localQuoteID);

    @Query("DELETE FROM opportunity WHERE isArchived = 1")
    void deleteAllArchivedOpportunities();

    @Query("SELECT count(*) FROM OpportunityQuote WHERE OpportunityID = :OpportunityID AND QuoteID = :QuoteID")
    int getOpportunityQuoteCount(int OpportunityID, int QuoteID);

    @Query("UPDATE opportunity SET subject = :subject, rating = :rating, status = :status, value = :value, probability = :probability, details = :details, targetDate = :targetDate, notes = :notes, updated = :updated WHERE opportunityID = :opportunityID")
    void updateOpportunityByOpportunityID(String subject, String rating, String status, String value, int probability, String details, String targetDate, String notes, String updated, int opportunityID);

    @Query("UPDATE opportunity SET isNew = :isNew, opportunityID = :opportunityID WHERE id = :id")
    void updateOpportunityIsNew(int id, int isNew, int opportunityID);

    @Query("UPDATE opportunity SET isChanged = :isChanged WHERE id = :id")
    void updateOpportunityIsChanged(int id, int isChanged);

    //Reports

    @Query("SELECT Count(*) AS statusCount, status FROM opportunity WHERE isArchived = 0 GROUP BY status")
    List<OpportunitySummary> getOpportunitySummary();

    @Query("SELECT Count(*) FROM opportunity WHERE isArchived = 0")
    int getOpportunitiesCount();

    @Query("SELECT Count(*) FROM opportunity WHERE isArchived = 0 AND status = 'Closed Won'")
    int getWonOpportunitiesCount();

    @Query("SELECT SUM(value) FROM opportunity WHERE isArchived = 0")
    float getOpportunitiesValue();

    @Query("SELECT SUM(value) FROM opportunity WHERE isArchived = 0 AND status = 'Closed Won'")
    float getWonOpportunitiesValue();

    //Other

    @Insert
    long insert(Opportunity opportunity);

    @Insert
    void insert(OpportunityContact opportunityContact);

    @Insert
    void insert(OpportunityQuote opportunityQuote);

    @Delete
    void delete(Opportunity opportunity);

    @Update
    void update(Opportunity opportunity);
}

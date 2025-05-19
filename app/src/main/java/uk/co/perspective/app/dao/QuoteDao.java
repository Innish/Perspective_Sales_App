package uk.co.perspective.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.co.perspective.app.entities.Quote;

@Dao
public interface QuoteDao {

    @Query("SELECT * FROM quote")
    List<Quote> getAll();

    @Query("SELECT * FROM quote WHERE customerName like '%' || :searchText || '%'  OR subject like '%' || :searchText || '%' OR reference like '%' || :searchText || '%'")
    List<Quote> searchQuotes(String searchText);

    @Query("SELECT * FROM quote WHERE isChanged = 1")
    List<Quote> getChangedQuotes();

    @Query("SELECT * FROM quote WHERE isNew = 1")
    List<Quote> getNewQuotes();

    @Query("SELECT * FROM quote WHERE status like '%' || :status || '%'")
    List<Quote> getQuotesByStatus(String status);

    @Query("SELECT * FROM quote WHERE datetime(updated) BETWEEN :startDate AND :endDate")
    List<Quote> getQuotesInRange(String startDate, String endDate);

    @Query("SELECT * FROM quote WHERE quoteID =:quoteID")
    Quote getQuoteByQuoteID(int quoteID);

    @Query("SELECT * FROM quote WHERE id =:id")
    Quote getQuote(int id);

    @Query("UPDATE quote SET isArchived = 1 WHERE id =:id")
    void archiveQuote(int id);

    @Query("DELETE FROM quote WHERE id =:id")
    void deleteQuote(int id);

    @Query("DELETE FROM quote WHERE isArchived = 1")
    void deleteAllArchivedQuotes();

    @Query("UPDATE quote SET subject = :subject, reference = :reference, status = :status, quoteType = :quoteType, targetDate = :targetDate, notes = :notes WHERE id = :id")
    void updateQuote(String subject, String reference, String status, String quoteType, String targetDate, String notes, int id);

    @Query("SELECT count(*) FROM quote WHERE quoteID = :quoteID")
    int getQuoteCount(int quoteID);

    @Query("SELECT count(*) FROM quote WHERE subject = :subject AND notes = :notes AND IFNULL(quoteID, 0) = 0")
    int getQuoteCount(String subject, String notes);

    @Query("UPDATE quote SET subject = :subject, reference = :reference, status = :status, targetDate = :targetDate, notes = :notes, updated = :updated WHERE quoteID = :quoteID")
    void updateQuoteByQuoteID(String subject, String reference, String status, String targetDate, String notes, String updated, int quoteID);

    @Query("UPDATE quote SET isNew = :isNew, quoteID = :quoteID WHERE id = :id")
    void updateQuoteIsNew(int id, int isNew, int quoteID);

    @Query("UPDATE quote SET isNew = :isNew, quoteID = :quoteID, reference = :reference WHERE id = :id")
    void updateQuoteIsNew(int id, int isNew, int quoteID, String reference);

    @Query("UPDATE quote SET isChanged = :isChanged WHERE id = :id")
    void updateQuoteIsChanged(int id, int isChanged);

    @Insert
    long insert(Quote quote);

    @Delete
    void delete(Quote quote);

    @Update
    void update(Quote quote);
}

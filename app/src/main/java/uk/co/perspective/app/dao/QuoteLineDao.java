package uk.co.perspective.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.co.perspective.app.entities.QuoteLine;

@Dao
public interface QuoteLineDao {

    @Query("SELECT * FROM quoteline")
    List<QuoteLine> getAll();

    @Query("SELECT * FROM quoteline WHERE quoteID =:quoteID")
    List<QuoteLine> getQuoteLines(int quoteID);

    @Query("SELECT * FROM quoteline WHERE localQuoteID =:localQuoteID")
    List<QuoteLine> getQuoteLinesFromLocalID(int localQuoteID);

    @Query("SELECT * FROM quoteline WHERE isNew = 1 AND quoteID > 0")
    List<QuoteLine> getNewQuoteLines();

    @Query("SELECT * FROM quoteline WHERE isChanged = 1")
    List<QuoteLine> getChangedQuoteLines();

    @Query("SELECT * FROM quoteline WHERE id =:id")
    QuoteLine getQuoteLine(int id);

    @Query("SELECT * FROM quoteline WHERE quoteLineID =:quoteLineID")
    QuoteLine getQuoteLineByQuoteLineID(int quoteLineID);

    @Query("DELETE FROM quoteline WHERE id =:id")
    void deleteQuoteLine(int id);

    @Query("SELECT count(*) FROM quoteline WHERE quoteLineID = :quoteLineID")
    int getQuoteLineCount(int quoteLineID);

    @Query("SELECT count(*) FROM quoteline WHERE partNumber = :partNumber AND description = :description AND quoteID = :quoteID AND IFNULL(quoteLineID, 0) = 0")
    int getQuoteLineCount(String partNumber, String description, int quoteID);

    @Query("UPDATE quoteline SET partNumber = :partNumber, description = :description, quantity = :quantity, value = :value, discount = :discount, taxRate = :taxRate, notes = :notes WHERE id = :id")
    void updateQuoteLine(String partNumber, String description, float quantity, float value, float discount, float taxRate, String notes, int id);

    @Query("UPDATE quoteline SET partNumber = :partNumber, description = :description, quantity = :quantity, value = :value, costPrice = :costPrice, discount = :discount, taxRate = :taxRate, notes = :notes, updated = :updated WHERE quoteLineID = :quoteLineID")
    void updateQuoteLineByQuoteLineID(String partNumber, String description, float quantity, float value, float costPrice, float discount, float taxRate, String notes, String updated, int quoteLineID);

    @Query("UPDATE quoteline SET isNew = :isNew, quoteID = :quoteID, quoteLineID = :quoteLineID WHERE id = :id")
    void updateQuoteLineIsNew(int id, int isNew, int quoteID, int quoteLineID);

    @Query("UPDATE quoteline SET isNew = :isNew, quoteLineID = :quoteLineID WHERE id = :id")
    void updateQuoteLineIsNew(int id, int isNew, int quoteLineID);

    @Query("UPDATE quoteline SET isChanged = :isChanged WHERE id = :id")
    void updateQuoteLineIsChanged(int id, int isChanged);

    @Insert
    long insert(QuoteLine quoteline);

    @Delete
    void delete(QuoteLine quoteline);

    @Update
    void update(QuoteLine quoteline);
}

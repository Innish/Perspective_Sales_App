package uk.co.perspective.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.co.perspective.app.entities.Currency;

@Dao
public interface CurrencyDao {

    @Query("SELECT * FROM currency")
    List<Currency> getAll();

    @Query("SELECT * FROM currency WHERE currencyName like '%' || :searchText || '%'")
    List<Currency> searchCurrency(String searchText);

    @Query("SELECT * FROM currency WHERE id =:id")
    Currency getCurrency(int id);

    @Query("SELECT count(*) FROM currency WHERE currencyID = :currencyID")
    int getCurrencyCount(int currencyID);

    @Query("SELECT count(*) FROM currency WHERE currencyName = :currencyName")
    int getCurrencyCount(String currencyName);

    @Query("UPDATE currency SET currencyName = :currencyName, ISOSymbol = :ISOSymbol, shortSymbol = :shortSymbol, rate = :rate WHERE currencyID = :currencyID")
    void updateCurrencyByCurrencyID(int currencyID, String currencyName, String ISOSymbol, String shortSymbol, float rate );

    @Query("DELETE FROM currency WHERE id =:id")
    void deleteCurrency(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Currency currency);

    @Delete
    void delete(Currency currency);

    @Update
    void update(Currency currency);
}

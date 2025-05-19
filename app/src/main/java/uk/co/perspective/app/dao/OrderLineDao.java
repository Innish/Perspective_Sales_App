package uk.co.perspective.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.co.perspective.app.entities.OrderLine;

@Dao
public interface OrderLineDao {

    @Query("SELECT * FROM orderLine")
    List<OrderLine> getAll();

    @Query("SELECT * FROM orderLine WHERE orderID =:orderID")
    List<OrderLine> getOrderLines(int orderID);

    @Query("SELECT * FROM orderLine WHERE localOrderID =:localOrderID")
    List<OrderLine> getOrderLinesFromLocalID(int localOrderID);

    @Query("SELECT * FROM orderLine WHERE isNew = 1 AND orderID > 0")
    List<OrderLine> getNewOrderLines();

    @Query("SELECT * FROM orderLine WHERE isChanged = 1")
    List<OrderLine> getChangedOrderLines();

    @Query("SELECT * FROM orderLine WHERE id =:id")
    OrderLine getQOrderLine(int id);

    @Query("SELECT * FROM orderLine WHERE orderLineID =:orderLineID")
    OrderLine getOrderLineByOrderLineID(int orderLineID);

    @Query("DELETE FROM orderLine WHERE id =:id")
    void deleteOrderLine(int id);

    @Query("SELECT count(*) FROM orderLine WHERE orderLineID = :orderLineID")
    int getOrderLineCount(int orderLineID);

    @Query("SELECT count(*) FROM orderLine WHERE partNumber = :partNumber AND description = :description AND orderID = :orderID AND IFNULL(orderLineID, 0) = 0")
    int getOrderLineCount(String partNumber, String description, int orderID);

    @Query("UPDATE orderLine SET partNumber = :partNumber, description = :description, quantity = :quantity, value = :value, discount = :discount, taxRate = :taxRate, notes = :notes WHERE id = :id")
    void updateOrderLine(String partNumber, String description, float quantity, float value, float discount, float taxRate, String notes, int id);

    @Query("UPDATE orderLine SET partNumber = :partNumber, description = :description, quantity = :quantity, value = :value, costPrice = :costPrice, discount = :discount, taxRate = :taxRate, notes = :notes, updated = :updated WHERE orderLineID = :orderLineID")
    void updateOrderLineByOrderLineID(String partNumber, String description, float quantity, float value, float costPrice, float discount, float taxRate, String notes, String updated, int orderLineID);

    @Query("UPDATE orderLine SET isNew = :isNew, orderID = :orderID, orderLineID = :orderLineID WHERE id = :id")
    void updateOrderLineIsNew(int id, int isNew, int orderID, int orderLineID);

    @Query("UPDATE orderLine SET isNew = :isNew, orderLineID = :orderLineID WHERE id = :id")
    void updateOrderLineIsNew(int id, int isNew, int orderLineID);

    @Query("UPDATE orderLine SET isChanged = :isChanged WHERE id = :id")
    void updateOrderLineIsChanged(int id, int isChanged);

    @Insert
    long insert(OrderLine orderLine);

    @Delete
    void delete(OrderLine orderLine);

    @Update
    void update(OrderLine orderLine);
}

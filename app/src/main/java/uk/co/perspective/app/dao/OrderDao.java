package uk.co.perspective.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.co.perspective.app.entities.Order;

@Dao
public interface OrderDao {

    @Query("SELECT * FROM `order`")
    List<Order> getAll();

    @Query("SELECT * FROM `order` WHERE customerName like '%' || :searchText || '%'  OR subject like '%' || :searchText || '%' OR reference like '%' || :searchText || '%'")
    List<Order> searchOrders(String searchText);

    @Query("SELECT * FROM `order` WHERE isChanged = 1")
    List<Order> getChangedOrders();

    @Query("SELECT * FROM `order` WHERE isNew = 1")
    List<Order> getNewOrders();

    @Query("SELECT * FROM `order` WHERE status like '%' || :status || '%'")
    List<Order> getOrdersByStatus(String status);

    @Query("SELECT * FROM `order` WHERE datetime(updated) BETWEEN :startDate AND :endDate")
    List<Order> getOrdersInRange(String startDate, String endDate);

    @Query("SELECT * FROM `order` WHERE orderID = :orderID")
    Order getOrderByOrderID(int orderID);

    @Query("SELECT * FROM `order` WHERE id =:id")
    Order getOrder(int id);

    @Query("UPDATE `order` SET isArchived = 1 WHERE id =:id")
    void archiveOrder(int id);

    @Query("DELETE FROM `order` WHERE id =:id")
    void deleteOrder(int id);

    @Query("DELETE FROM `order` WHERE isArchived = 1")
    void deleteAllArchivedOrders();

    @Query("UPDATE `order` SET subject = :subject, reference = :reference, status = :status, closingDate = :closingDate, notes = :notes WHERE id = :id")
    void updateOrder(String subject, String reference, String status, String closingDate, String notes, int id);

    @Query("SELECT count(*) FROM `order` WHERE orderID = :orderID")
    int getOrderCount(int orderID);

    @Query("SELECT count(*) FROM `order` WHERE subject = :subject AND notes = :notes AND IFNULL(orderID, 0) = 0")
    int getOrderCount(String subject, String notes);

    @Query("UPDATE `order` SET subject = :subject, reference = :reference, status = :status, closingDate = :closingDate, notes = :notes, updated = :updated WHERE orderID = :orderID")
    void updateOrderByOrderID(String subject, String reference, String status, String closingDate, String notes, String updated, int orderID);

    @Query("UPDATE `order` SET isNew = :isNew, orderID = :orderID WHERE id = :id")
    void updateOrderIsNew(int id, int isNew, int orderID);

    @Query("UPDATE `order` SET isNew = :isNew, orderID = :orderID, reference = :reference WHERE id = :id")
    void updateOrderIsNew(int id, int isNew, int orderID, String reference);

    @Query("UPDATE `order` SET isChanged = :isChanged WHERE id = :id")
    void updateOrderIsChanged(int id, int isChanged);

    @Insert
    long insert(Order order);

    @Delete
    void delete(Order order);

    @Update
    void update(Order order);
}

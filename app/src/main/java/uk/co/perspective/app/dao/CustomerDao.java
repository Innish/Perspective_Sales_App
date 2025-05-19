package uk.co.perspective.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.co.perspective.app.entities.Customer;

@Dao
public interface CustomerDao {

    @Query("SELECT * FROM customer")
    List<Customer> getAll();

    @Query("SELECT * FROM customer WHERE customerName like '%' || :searchText || '%'")
    List<Customer> searchCustomers(String searchText);

    @Query("SELECT * FROM customer WHERE isNew = 1")
    List<Customer> getNewCustomers();

    @Query("SELECT * FROM customer WHERE isChanged = 1")
    List<Customer> getChangedCustomers();

    @Query("SELECT * FROM customer WHERE id =:id")
    Customer getCustomer(int id);

    @Query("SELECT * FROM customer WHERE customerID =:customerID")
    Customer getCustomerByCustomerID(int customerID);

    @Query("UPDATE customer SET isArchived = 1 WHERE id =:id")
    void archiveCustomer(int id);

    @Query("SELECT count(*) FROM customer WHERE customerID = :customerID")
    int getCustomerCount(int customerID);

    @Query("SELECT count(*) FROM customer WHERE customerName = :customerName AND IFNULL(customerID, 0) = 0")
    int getCustomerCount(String customerName);

    @Query("DELETE FROM customer WHERE id =:id")
    void deleteCustomer(int id);

    @Query("UPDATE customer SET customerName = :customerName, customerReference = :customerReference, customerType = :customerType, customerStatus = :customerStatus, generalTelephone = :telephone, mobile = :mobile, generalEmail = :email, notes = :notes WHERE id = :id")
    void updateCustomer(String customerName, String customerReference, String customerType, String customerStatus, String telephone, String mobile, String email, String notes, int id);

    @Query("UPDATE customer SET contactName = :contactName WHERE id = :id")
    void updateCustomerContactName(String contactName, int id);

    @Query("UPDATE customer SET isNew = :isNew, customerID = :CustomerID WHERE id = :id")
    void updateCustomerIsNew(int id, int isNew, int CustomerID);

    @Query("UPDATE customer SET isChanged = :isChanged WHERE id = :id")
    void updateCustomerIsChanged(int id, int isChanged);

    @Query("UPDATE customer SET customerReference = :customerReference, customerName = :customerName, customerStatus = :customerStatus, parentCustomerName = :parentCustomerName, contactName = :contactName, generalTelephone = :telephone, generalEmail = :email, notes =:notes, updated = :updated WHERE customerID = :customerID")
    void updateCustomerByCustomerID(int customerID, String customerReference, String customerName, String customerStatus, String parentCustomerName, String contactName, String telephone, String email, String notes, String updated);

    @Insert
    long insert(Customer customer);

    @Delete
    void delete(Customer customer);

    @Update
    void update(Customer customer);
}

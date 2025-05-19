package uk.co.perspective.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.co.perspective.app.entities.Contact;

@Dao
public interface ContactDao {

    @Query("SELECT * FROM contact")
    List<Contact> getAll();

    @Query("SELECT * FROM contact WHERE localCustomerID =:id")
    List<Contact> getCustomerContacts(int id);

    @Query("SELECT * FROM contact WHERE id =:id")
    Contact getContact(int id);

    @Query("SELECT * FROM contact WHERE isNew = 1")
    List<Contact> getNewContacts();

    @Query("SELECT * FROM contact WHERE isChanged = 1")
    List<Contact> getChangedContacts();

    @Query("SELECT * FROM contact WHERE contactName like '%' || :searchText || '%'")
    List<Contact> searchContacts(String searchText);

    @Query("SELECT * FROM contact WHERE id =:id AND contactName like '%' || :searchText || '%'")
    List<Contact> searchCustomerContacts(int id, String searchText);

    @Query("SELECT * FROM contact WHERE contactID =:contactID")
    Contact getContactByContactID(int contactID);

    @Query("UPDATE contact SET isArchived = 1 WHERE id =:id")
    void archiveContact(int id);

    @Query("SELECT count(*) FROM contact WHERE customerID = :customerID AND contactName = :contactName")
    int getContactCount(int customerID, String contactName);

    @Query("SELECT count(*) FROM contact WHERE customerName = :customerName AND contactName = :contactName")
    int getContactCount(String customerName, String contactName);

    @Query("DELETE FROM contact WHERE id =:id")
    void deleteContact(int id);

    @Query("UPDATE contact SET contactName = :contactName, salutation = :salutation, jobTitle = :jobTitle, telephone = :telephone, mobile = :mobile, email = :email, notes = :notes, updated = :updated, ischanged = 1 WHERE id = :id")
    void updateContact(String contactName, String salutation, String jobTitle, String telephone, String mobile, String email, String notes, String updated, int id);

    @Query("UPDATE contact SET isNew = :isNew, contactID = :contactID WHERE id = :id")
    void updateContactIsNew(int id, int isNew, int contactID);

    @Query("UPDATE contact SET isChanged = :isChanged WHERE id = :id")
    void updateContactIsChanged(int id, int isChanged);

    @Query("UPDATE contact SET contactName = :contactName, salutation = :salutation, jobTitle = :jobTitle, telephone = :telephone, mobile = :mobile, email = :email, notes = :notes, updated = :updated WHERE contactID = :contactID")
    void updateContactByContactID(int contactID, String contactName, String salutation, String jobTitle, String telephone, String mobile, String email, String notes, String updated);

    @Insert
    long insert(Contact contact);

    @Delete
    void delete(Contact contact);

    @Update
    void update(Contact contact);
}

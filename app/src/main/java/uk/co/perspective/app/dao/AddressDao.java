package uk.co.perspective.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.co.perspective.app.entities.Address;

@Dao
public interface AddressDao {

    @Query("SELECT * FROM address")
    List<Address> getAll();

    @Query("SELECT * FROM address WHERE localCustomerID =:localCustomerID")
    List<Address> getCustomerAddresses(int localCustomerID);

    @Query("SELECT * FROM address WHERE customerID =:customerID")
    List<Address> getCustomerAddressesByCustomerID(int customerID);

    @Query("SELECT * FROM address WHERE isNew = 1")
    List<Address> getNewAddresses();

    @Query("SELECT * FROM address WHERE id =:id")
    Address getAddress(int id);

    @Query("SELECT * FROM address WHERE isChanged = 1")
    List<Address> getChangedAddresses();

    @Query("SELECT * FROM address WHERE AddressID =:AddressID")
    Address getAddressByAddressID(int AddressID);

    @Query("SELECT count(*) FROM address WHERE customerID = :customerID AND addressID = :addressID")
    int getAddressCount(int customerID, int addressID);

    @Query("UPDATE address SET isArchived = 1 WHERE id =:id")
    void archiveAddress(int id);

    @Query("DELETE FROM address WHERE id =:id")
    void deleteAddress(int id);

    @Query("UPDATE address SET addressType = :addressType, address = :address, updated = :updated, ischanged = 1 WHERE id = :id")
    void updateAddress(String addressType, String address, String updated, int id);

    @Query("UPDATE address SET isChanged = :isChanged WHERE id = :id")
    void updateAddressIsChanged(int id, int isChanged);

    @Query("UPDATE address SET isNew = :isNew, addressID = :addressID WHERE id = :id")
    void updateAddressIsNew(int id, int isNew, int addressID);

    @Query("UPDATE address SET addressType = :addressType, address = :address, updated = :updated WHERE addressID = :addressID")
    void updateAddressByAddressID(int addressID, String addressType, String address, String updated);

    @Insert
    long insert(Address address);

    @Delete
    void delete(Address address);

    @Update
    void update(Address address);
}

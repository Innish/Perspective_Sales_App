package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Address {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("addressID")
    @ColumnInfo(name = "addressID")
    private Integer addressID;

    @SerializedName("localCustomerID")
    @ColumnInfo(name = "localCustomerID")
    private Integer localCustomerID;

    @SerializedName("customerID")
    @ColumnInfo(name = "customerID")
    private Integer customerID;

    @SerializedName("addressType")
    @ColumnInfo(name = "addressType")
    private String addressType;

    @SerializedName("address")
    @ColumnInfo(name = "address")
    private String address;

    @SerializedName("updated")
    @ColumnInfo(name = "updated")
    private String updated;

    @SerializedName("createdByDisplayName")
    @ColumnInfo(name = "createdByDisplayName")
    private String createdByDisplayName;

    @SerializedName("isArchived")
    @ColumnInfo(name = "isArchived")
    private boolean isArchived;

    @ColumnInfo(name = "isChanged")
    private boolean isChanged;

    @ColumnInfo(name = "isNew")
    private boolean isNew;

    public Address()
    {

    }

    @Ignore
    public Address(int addressID, int customerID, String addressType, String address)
    {
        this.addressID = addressID;
        this.customerID = customerID;
        this.addressType = addressType;
        this.address = address;
    }

    @Ignore
    public Address(int addressID, String addressType)
    {
        this.addressID = addressID;
        this.addressType = addressType;
    }

    //Getters / Setters

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAddressID() {
        return addressID;
    }
    public void setAddressID(Integer addressID) {
        this.addressID = addressID;
    }
    public Address withAddressID(Integer addressID) {
        this.addressID = addressID;
        return this;
    }

    //LocalCustomerID

    public Integer getLocalCustomerID() {
        return localCustomerID;
    }
    public void setLocalCustomerID(Integer localCustomerID) {
        this.localCustomerID = localCustomerID;
    }
    public Address withLocalCustomerID(Integer localCustomerID) {
        this.localCustomerID = localCustomerID;
        return this;
    }

    //CustomerID

    public Integer getCustomerID() {
        return customerID;
    }
    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }
    public Address withCustomerID(Integer customerID) {
        this.customerID = customerID;
        return this;
    }

    //Address Type

    public String getAddressType() {
        return addressType;
    }
    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }
    public Address withAddressType(String addressType) {
        this.addressType = addressType;
        return this;
    }

    //Address

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public Address withAddress(String address) {
        this.address = address;
        return this;
    }

    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }
    public Address withUpdated(String updated) {
        this.updated = updated;
        return this;
    }

    public String getCreatedByDisplayName() {
        return createdByDisplayName;
    }
    public void setCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
    }
    public Address withCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
        return this;
    }

    public boolean getIsArchived() { return isArchived; }
    public void setIsArchived(boolean isArchived) { this.isArchived = isArchived; }
    public Address withIsArchived(boolean isArchived) {
        this.isArchived = isArchived;
        return this;
    }

    public boolean getIsChanged() {
        return isChanged;
    }
    public void setIsChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    public boolean isNew() {
        return isNew;
    }
    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }



}

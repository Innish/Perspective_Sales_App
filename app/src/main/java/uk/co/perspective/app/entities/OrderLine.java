package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class OrderLine {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("orderLineID")
    @ColumnInfo(name = "orderLineID")
    private Integer orderLineID;

    @SerializedName("orderID")
    @ColumnInfo(name = "orderID")
    private Integer orderID;

    @SerializedName("localOrderID")
    @ColumnInfo(name = "localOrderID")
    private Integer localOrderID;

    @SerializedName("partNumber")
    @ColumnInfo(name = "partNumber")
    private String partNumber;

    @SerializedName("description")
    @ColumnInfo(name = "description")
    private String description;

    @SerializedName("quantity")
    @ColumnInfo(name = "quantity")
    private Float quantity;

    @SerializedName("value")
    @ColumnInfo(name = "value")
    private Float value;

    @SerializedName("costPrice")
    @ColumnInfo(name = "costPrice")
    private Float costPrice;

    @SerializedName("discount")
    @ColumnInfo(name = "discount")
    private Float discount;

    @SerializedName("taxRate")
    @ColumnInfo(name = "taxRate")
    private Float taxRate;

    @SerializedName("notes")
    @ColumnInfo(name = "notes")
    private String notes;

    @SerializedName("updated")
    @ColumnInfo(name = "updated")
    private String updated;

    @SerializedName("createdByDisplayName")
    @ColumnInfo(name = "createdByDisplayName")
    private String createdByDisplayName;

    @ColumnInfo(name = "isChanged")
    private boolean isChanged;

    @ColumnInfo(name = "isNew")
    private boolean isNew;

    public OrderLine()
    {

    }

    @Ignore
    public OrderLine(int orderLineID, int orderID, String partNumber, String description, Float quantity, Float value, String notes, boolean isNew)
    {
        this.orderLineID = orderLineID;
        this.orderID = orderID;
        this.partNumber = partNumber;
        this.description = description;
        this.quantity = quantity;
        this.value = value;
        this.notes = notes;
        this.isNew = isNew;
    }

    @Ignore
    public OrderLine(int orderLineID, int orderID, String partNumber, String description, Float quantity, Float value, Float discount, Float taxRate, String notes, boolean isNew)
    {
        this.orderLineID = orderLineID;
        this.orderID = orderID;
        this.partNumber = partNumber;
        this.description = description;
        this.quantity = quantity;
        this.value = value;
        this.discount = discount;
        this.taxRate = taxRate;
        this.notes = notes;
        this.isNew = isNew;
    }

    @Ignore
    public OrderLine(int orderLineID, String description)
    {
        this.orderLineID = orderLineID;
        this.description = description;
    }


    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderLineID() {
        return orderLineID;
    }
    public void setOrderLineID(Integer orderLineID) {
        this.orderLineID = orderLineID;
    }
    public OrderLine withOrderLineID(Integer orderLineID) {
        this.orderLineID = orderLineID;
        return this;
    }

    public Integer getOrderID() {
        return orderID;
    }
    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }
    public OrderLine withOrderID(Integer orderID) {
        this.orderID = orderID;
        return this;
    }

    public Integer getLocalOrderID() {
        return localOrderID;
    }
    public void setLocalOrderID(Integer localOrderID) {
        this.localOrderID = localOrderID;
    }
    public OrderLine withLocalOrderID(Integer localOrderID) {
        this.localOrderID = localOrderID;
        return this;
    }

    public String getPartNumber() {
        return partNumber;
    }
    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }
    public OrderLine withPartNumber(String partNumber) {
        this.partNumber = partNumber;
        return this;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public OrderLine withDescription(String description) {
        this.description = description;
        return this;
    }

    public Float getQuantity() {
        return quantity;
    }
    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }
    public OrderLine withQuantity(Float quantity) {
        this.quantity = quantity;
        return this;
    }

    public Float getValue() {
        return value;
    }
    public void setValue(Float value) {
        this.value = value;
    }
    public OrderLine withValue(Float value) {
        this.value = value;
        return this;
    }

    public Float getCostPrice() {
        return costPrice;
    }
    public void setCostPrice(Float costPrice) {
        this.costPrice = costPrice;
    }
    public OrderLine withCostPrice(Float costPrice) {
        this.costPrice = costPrice;
        return this;
    }

    public Float getDiscount() {
        return discount;
    }
    public void setDiscount(Float discount) {
        this.discount = discount;
    }
    public OrderLine withDiscount(Float discount) {
        this.discount = discount;
        return this;
    }

    public Float getTaxRate() {
        return taxRate;
    }
    public void setTaxRate(Float taxRate) {
        this.taxRate = taxRate;
    }
    public OrderLine withTaxRate(Float taxRate) {
        this.taxRate = taxRate;
        return this;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public OrderLine withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }
    public OrderLine withUpdated(String updated) {
        this.updated = updated;
        return this;
    }

    public String getCreatedByDisplayName() {
        return createdByDisplayName;
    }
    public void setCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
    }
    public OrderLine withCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
        return this;
    }

    public boolean getIsChanged() {
        return isChanged;
    }
    public void setIsChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    public boolean getIsNew() {
        return isNew;
    }
    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }
}

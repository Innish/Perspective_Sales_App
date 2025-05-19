package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class QuoteLine {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("quoteLineID")
    @ColumnInfo(name = "quoteLineID")
    private Integer quoteLineID;

    @SerializedName("quoteID")
    @ColumnInfo(name = "quoteID")
    private Integer quoteID;

    @SerializedName("localQuoteID")
    @ColumnInfo(name = "localQuoteID")
    private Integer localQuoteID;

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

    public QuoteLine()
    {

    }

    @Ignore
    public QuoteLine(int quoteLineID, int quoteID, String partNumber, String description, Float quantity, Float value, String notes, boolean isNew)
    {
        this.quoteLineID = quoteLineID;
        this.quoteID = quoteID;
        this.partNumber = partNumber;
        this.description = description;
        this.quantity = quantity;
        this.value = value;
        this.notes = notes;
        this.isNew = isNew;
    }

    @Ignore
    public QuoteLine(int quoteLineID, int quoteID, String partNumber, String description, Float quantity, Float value, Float discount, Float taxRate, String notes, boolean isNew)
    {
        this.quoteLineID = quoteLineID;
        this.quoteID = quoteID;
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
    public QuoteLine(int quoteLineID, String description)
    {
        this.quoteLineID = quoteLineID;
        this.description = description;
    }


    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuoteLineID() {
        return quoteLineID;
    }
    public void setQuoteLineID(Integer quoteLineID) {
        this.quoteLineID = quoteLineID;
    }
    public QuoteLine withQuoteLineID(Integer quoteLineID) {
        this.quoteLineID = quoteLineID;
        return this;
    }

    public Integer getQuoteID() {
        return quoteID;
    }
    public void setQuoteID(Integer quoteID) {
        this.quoteID = quoteID;
    }
    public QuoteLine withQuoteID(Integer quoteID) {
        this.quoteID = quoteID;
        return this;
    }

    public Integer getLocalQuoteID() {
        return localQuoteID;
    }
    public void setLocalQuoteID(Integer localQuoteID) {
        this.localQuoteID = localQuoteID;
    }
    public QuoteLine withLocalQuoteID(Integer localQuoteID) {
        this.localQuoteID = localQuoteID;
        return this;
    }

    public String getPartNumber() {
        return partNumber;
    }
    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }
    public QuoteLine withPartNumber(String partNumber) {
        this.partNumber = partNumber;
        return this;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public QuoteLine withDescription(String description) {
        this.description = description;
        return this;
    }

    public Float getQuantity() {
        return quantity;
    }
    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }
    public QuoteLine withQuantity(Float quantity) {
        this.quantity = quantity;
        return this;
    }

    public Float getValue() {
        return value;
    }
    public void setValue(Float value) {
        this.value = value;
    }
    public QuoteLine withValue(Float value) {
        this.value = value;
        return this;
    }

    public Float getCostPrice() {
        return costPrice;
    }
    public void setCostPrice(Float costPrice) {
        this.costPrice = costPrice;
    }
    public QuoteLine withCostPrice(Float costPrice) {
        this.costPrice = costPrice;
        return this;
    }

    public Float getDiscount() {
        return discount;
    }
    public void setDiscount(Float discount) {
        this.discount = discount;
    }
    public QuoteLine withDiscount(Float discount) {
        this.discount = discount;
        return this;
    }

    public Float getTaxRate() {
        return taxRate;
    }
    public void setTaxRate(Float taxRate) {
        this.taxRate = taxRate;
    }
    public QuoteLine withTaxRate(Float taxRate) {
        this.taxRate = taxRate;
        return this;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public QuoteLine withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }
    public QuoteLine withUpdated(String updated) {
        this.updated = updated;
        return this;
    }

    public String getCreatedByDisplayName() {
        return createdByDisplayName;
    }
    public void setCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
    }
    public QuoteLine withCreatedByDisplayName(String createdByDisplayName) {
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

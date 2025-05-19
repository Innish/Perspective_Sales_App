package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Product implements Comparable<Product> {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("productID")
    @ColumnInfo(name = "productID")
    private Integer productID;

    @SerializedName("partNumber")
    @ColumnInfo(name = "partNumber")
    private String partNumber;

    @SerializedName("description")
    @ColumnInfo(name = "description")
    private String description;

    @SerializedName("costPrice")
    @ColumnInfo(name = "costPrice")
    private Float costPrice;

    @SerializedName("salePrice")
    @ColumnInfo(name = "salePrice")
    private Float salePrice;

    @SerializedName("taxRate")
    @ColumnInfo(name = "taxRate")
    private Float taxRate;

    public Product()
    {

    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductID() {
        return productID;
    }
    public void setProductID(Integer productID) {
        this.productID = productID;
    }
    public Product withProductID(Integer productID) {
        this.productID = productID;
        return this;
    }

    public String getPartNumber() {
        return partNumber;
    }
    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }
    public Product withPartNumber(String partNumber) {
        this.partNumber = partNumber;
        return this;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Product withDescription(String description) {
        this.description = description;
        return this;
    }

    public Float getCostPrice() {
        return costPrice;
    }
    public void setCostPrice(Float costPrice) {
        this.costPrice = costPrice;
    }
    public Product withCostPrice(Float costPrice) {
        this.costPrice = costPrice;
        return this;
    }

    public Float getSalePrice() {
        return salePrice;
    }
    public void setSalePrice(Float salePrice) {
        this.salePrice = salePrice;
    }
    public Product withSalesPrice(Float salePrice) {
        this.salePrice = salePrice;
        return this;
    }

    public Float getTaxRate() {
        return taxRate;
    }
    public void setTaxRate(Float taxRate) {
        this.taxRate = taxRate;
    }
    public Product withTaxRate(Float taxRate) {
        this.taxRate = taxRate;
        return this;
    }

    @Override
    public int compareTo(Product o) {

        int lexicographicallyDifferent = 0;

        if (getDescription() != null) {
            lexicographicallyDifferent = getDescription().compareTo(o.getDescription());
        }

        return lexicographicallyDifferent;
    }
}

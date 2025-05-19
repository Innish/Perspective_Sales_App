package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Currency implements Comparable<Currency> {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("currencyID")
    @ColumnInfo(name = "currencyID")
    private Integer currencyID;

    @SerializedName("currencyName")
    @ColumnInfo(name = "currencyName")
    private String currencyName;

    @SerializedName("ISOSymbol")
    @ColumnInfo(name = "ISOSymbol")
    private String ISOSymbol;

    @SerializedName("shortSymbol")
    @ColumnInfo(name = "shortSymbol")
    private String shortSymbol;

    @SerializedName("rate")
    @ColumnInfo(name = "rate")
    private float rate;

    public Currency()
    {

    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCurrencyID() {
        return currencyID;
    }
    public void setCurrencyID(Integer currencyID) {
        this.currencyID = currencyID;
    }
    public Currency withCurrencyID(Integer currencyID) {
        this.currencyID = currencyID;
        return this;
    }

    public String getCurrencyName() {
        return currencyName;
    }
    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }
    public Currency withCurrencyName(String currencyName) {
        this.currencyName = currencyName;
        return this;
    }

    public String getISOSymbol() {
        return ISOSymbol;
    }
    public void setISOSymbol(String ISOSymbol) {
        this.ISOSymbol = ISOSymbol;
    }
    public Currency withISOSymbol(String ISOSymbol) {
        this.ISOSymbol = ISOSymbol;
        return this;
    }

    public String getShortSymbol() {
        return shortSymbol;
    }
    public void setShortSymbol(String shortSymbol) {
        this.shortSymbol = shortSymbol;
    }
    public Currency withShortSymbol(String shortSymbol) {
        this.shortSymbol = shortSymbol;
        return this;
    }

    public float getRate() {
        return rate;
    }
    public void setRate(float rate) {
        this.rate = rate;
    }
    public Currency withRate(float rate) {
        this.rate = rate;
        return this;
    }

    @Override
    public int compareTo(Currency o) {

        int lexicographicallyDifferent = 0;

        if (getCurrencyName() != null) {
            lexicographicallyDifferent = getCurrencyName().compareTo(o.getCurrencyName());
        }

        return lexicographicallyDifferent;
    }
}

package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class OpportunityQuote {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @SerializedName("opportunityID")
    @ColumnInfo(name = "opportunityID")
    private Integer opportunityID;

    @SerializedName("quoteID")
    @ColumnInfo(name = "quoteID")
    private Integer quoteID;

    @SerializedName("localOpportunityID")
    @ColumnInfo(name = "localOpportunityID")
    private Integer localOpportunityID;

    @SerializedName("localQuoteID")
    @ColumnInfo(name = "localQuoteID")
    private Integer localQuoteID;

    public OpportunityQuote()
    {

    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOpportunityID() {
        return opportunityID;
    }
    public void setOpportunityID(Integer opportunityID) {
        this.opportunityID = opportunityID;
    }
    public OpportunityQuote withOpportunityID(int opportunityID) {
        this.opportunityID = opportunityID;
        return this;
    }

    public Integer getQuoteID() {
        return quoteID;
    }
    public void setQuoteID(Integer quoteID) {
        this.quoteID = quoteID;
    }
    public OpportunityQuote withQuoteID(Integer quoteID) {
        this.quoteID = quoteID;
        return this;
    }

    public Integer getLocalOpportunityID() {
        return localOpportunityID;
    }
    public void setLocalOpportunityID(Integer localOpportunityID) {
        this.localOpportunityID = localOpportunityID;
    }
    public OpportunityQuote withLocalOpportunityID(Integer localOpportunityID) {
        this.localOpportunityID = localOpportunityID;
        return this;
    }

    public Integer getLocalQuoteID() {
        return localQuoteID;
    }
    public void setLocalQuoteID(Integer localQuoteID) {
        this.localQuoteID = localQuoteID;
    }
    public OpportunityQuote withLocalQuoteID(Integer localQuoteID) {
        this.localQuoteID = localQuoteID;
        return this;
    }
}

package uk.co.perspective.app.joins;

import androidx.room.Ignore;

public class JoinOpportunityQuote {

    public int id;
    private String subject;
    private String customerName;
    private String status;
    private String quoteType;

    public JoinOpportunityQuote()
    {

    }

    @Ignore
    public JoinOpportunityQuote(int id, String subject)
    {
        this.id = id;
        this.subject = subject;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getQuoteType() {
        return quoteType;
    }
    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}

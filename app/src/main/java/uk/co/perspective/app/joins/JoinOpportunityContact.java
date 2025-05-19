package uk.co.perspective.app.joins;


import androidx.room.Ignore;

public class JoinOpportunityContact {

    public int id;
    private String contactName;
    private String customerName;
    private String jobTitle;
    private String subject;

    public JoinOpportunityContact()
    {

    }

    @Ignore
    public JoinOpportunityContact(int id, String contactName)
    {
        this.id = id;
        this.contactName = contactName;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getContactName() {
        return contactName;
    }
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getJobTitle() {
        return jobTitle;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
}

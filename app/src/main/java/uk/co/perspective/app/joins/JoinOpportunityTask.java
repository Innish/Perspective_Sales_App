package uk.co.perspective.app.joins;

import androidx.room.Ignore;

public class JoinOpportunityTask implements Comparable<JoinOpportunityTask> {

    public int id;
    public int localOpportunityID;
    private boolean header;
    private String subject;
    private String notes;
    private String dueDate;
    private boolean complete;

    public JoinOpportunityTask()
    {

    }

    @Ignore
    public JoinOpportunityTask(int id, String subject)
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

    public int getLocalOpportunityID() {
        return localOpportunityID;
    }
    public void setLocalOpportunityID(int localOpportunityID) {
        this.localOpportunityID = localOpportunityID;
    }

    public boolean getHeader() {
        return header;
    }
    public void setHeader(boolean header) {
        this.header = header;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDueDate() {
        return dueDate;
    }
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public boolean getComplete() {
        return complete;
    }
    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    @Override
    public int compareTo(JoinOpportunityTask o) {

        int lexicographicallyDifferent = 0;

        if (getLocalOpportunityID() != 0) {
            lexicographicallyDifferent = (Integer.toString(getLocalOpportunityID()).compareTo(Integer.toString(o.getLocalOpportunityID())));
        }

        return lexicographicallyDifferent;
    }
}

package uk.co.perspective.app.joins;

import androidx.room.Ignore;

public class JoinProjectTask implements Comparable<JoinProjectTask> {

    public int id;
    public int localProjectID;
    public int phaseLocalID;
    private String phaseName;
    private String phaseDescription;
    private boolean header;
    private String subject;
    private String notes;
    private String dueDate;
    private boolean complete;

    public JoinProjectTask()
    {

    }

    @Ignore
    public JoinProjectTask(int id, String subject)
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

    public int getLocalProjectID() {
        return localProjectID;
    }
    public void setLocalProjectID(int localProjectID) {
        this.localProjectID = localProjectID;
    }

    public int getPhaseLocalID() {
        return phaseLocalID;
    }
    public void setPhaseLocalID(int phaseLocalID) {
        this.phaseLocalID = phaseLocalID;
    }

    public String getPhaseName() {
        return phaseName;
    }
    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public String getPhaseDescription() {
        return phaseDescription;
    }
    public void setPhaseDescription(String phaseDescription) {
        this.phaseDescription = phaseDescription;
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
    public int compareTo(JoinProjectTask o) {

        int lexicographicallyDifferent = 0;

        if (getPhaseLocalID() != 0) {
            lexicographicallyDifferent = (Integer.toString(getPhaseLocalID()).compareTo(Integer.toString(o.getPhaseLocalID())));
        }

        return lexicographicallyDifferent;
    }
}

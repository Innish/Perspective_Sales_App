package uk.co.perspective.app.entities;

import androidx.room.Entity;

@Entity
public class OpportunitySummary {

    private Integer statusCount;
    private String status;

    public OpportunitySummary()
    {

    }

    public Integer getStatusCount() {
        return statusCount;
    }
    public void setStatusCount(Integer statusCount) {
        this.statusCount = statusCount;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}

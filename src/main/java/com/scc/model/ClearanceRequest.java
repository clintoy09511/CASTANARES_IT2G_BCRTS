package com.scc.model;

import java.time.LocalDateTime;

public class ClearanceRequest {
    private int clearanceId;
    private int residentId;
    private LocalDateTime requestDate;
    private String purpose;
    private String status;
    private Integer processedBy;

    public ClearanceRequest() {
    }

    public ClearanceRequest(int clearanceId, int residentId, LocalDateTime requestDate,
                            String purpose, String status, Integer processedBy) {
        this.clearanceId = clearanceId;
        this.residentId = residentId;
        this.requestDate = requestDate;
        this.purpose = purpose;
        this.status = status;
        this.processedBy = processedBy;
    }

    public int getClearanceId() { return clearanceId; }
    public void setClearanceId(int clearanceId) { this.clearanceId = clearanceId; }

    public int getResidentId() { return residentId; }
    public void setResidentId(int residentId) { this.residentId = residentId; }

    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getProcessedBy() { return processedBy; }
    public void setProcessedBy(Integer processedBy) { this.processedBy = processedBy; }
}
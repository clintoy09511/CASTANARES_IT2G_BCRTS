package com.scc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private int paymentId;
    private int clearanceId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String paymentStatus;
    private Integer recordedBy;

    public Payment() {
    }

    public Payment(int paymentId, int clearanceId, BigDecimal amount, LocalDateTime paymentDate,
                   String paymentStatus, Integer recordedBy) {
        this.paymentId = paymentId;
        this.clearanceId = clearanceId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentStatus = paymentStatus;
        this.recordedBy = recordedBy;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getClearanceId() { return clearanceId; }
    public void setClearanceId(int clearanceId) { this.clearanceId = clearanceId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Integer getRecordedBy() { return recordedBy; }
    public void setRecordedBy(Integer recordedBy) { this.recordedBy = recordedBy; }
}
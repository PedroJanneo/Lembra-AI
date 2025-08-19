package com.lembrai.dto;

public class CreateCheckoutRequest {
    private Long amountInCents;
    private String description;
    private Long lembreteid;


    public Long getAmountInCents() {
        return amountInCents;
    }

    public void setAmountInCents(Long amountInCents) {
        this.amountInCents = amountInCents;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getLembreteid() {
        return lembreteid;
    }

    public void setLembreteid(Long lembreteid) {
        this.lembreteid = lembreteid;
    }
}

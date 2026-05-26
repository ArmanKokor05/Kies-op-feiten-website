package com.sop.backend.dto;

public class CreateElectionsDTO {
    private int amountAdded;

    public void setAmount(int amountAdded) {
        this.amountAdded = amountAdded;
    }

    public int getAmount() {
        return amountAdded;
    }
}

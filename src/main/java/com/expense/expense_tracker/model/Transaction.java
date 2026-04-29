package com.expense.expense_tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "transactions")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {

    @Id
    private String id;
    private String userId;  // NEW: Associate transaction with user
    private BigDecimal amount;
    private String type;
    private String category;
    private String merchant;

    private LocalDateTime date;

    // Default constructor
    public Transaction() {}

    // Constructor with parameters
    public Transaction(BigDecimal amount, String type, String category, String merchant, LocalDateTime date) {
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.merchant = merchant;
        this.date = date;
    }

    // Constructor with userId
    public Transaction(String userId, BigDecimal amount, String type, String category, String merchant, LocalDateTime date) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.merchant = merchant;
        this.date = date;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                ", category='" + category + '\'' +
                ", merchant='" + merchant + '\'' +
                ", date=" + date +
                '}';
    }
}

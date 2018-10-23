package com.neu.pojo;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;


@Entity
public class TransactionDetails implements Persistable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column
    private UUID transactionDetailsId;

    private String merchant;

    private String amount;

    private String date;

    private String category;

    private String username;

    private String description;

    @OneToMany(mappedBy = "transactionDetails", cascade = CascadeType.ALL)
    private Set<TransactionAttachments> transactionAttachments;

    @ManyToOne
    @JoinColumn(name="userId")
    private UserDetails userDetails;

    public UUID getTransactionDetailsId() {
        return transactionDetailsId;
    }

    public void setTransactionDetailsId(UUID transactionDetailsId) {
        this.transactionDetailsId = transactionDetailsId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserDetails getUserDetails() {
       return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
        }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Set<TransactionAttachments> getTransactionAttachments() {
        return transactionAttachments;
    }

    public void setTransactionAttachments(Set<TransactionAttachments> transactionAttachments) {
        this.transactionAttachments = transactionAttachments;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Serializable getId() {
        return transactionDetailsId;
    }




    @Override
    public boolean isNew() {
        return true;
    }
}

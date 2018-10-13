package com.neu.pojo;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;


@Entity
@Table(name = "UserInfo")
@EntityListeners(AuditingEntityListener.class)
public class UserDetails implements Persistable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @OneToMany(mappedBy = "userDetails", cascade = CascadeType.ALL)
    private List<TransactionDetails> transactionDetailsList;


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<TransactionDetails> getTransactionDetailsList() {
        return transactionDetailsList;
    }

    public void setTransactionDetailsList(List<TransactionDetails> transactionDetailsList) {
        this.transactionDetailsList = transactionDetailsList;
    }

    @Override
    public Serializable getId() {
        return userId;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}

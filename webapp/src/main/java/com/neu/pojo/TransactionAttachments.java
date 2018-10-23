package com.neu.pojo;


import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;


@Entity
public class TransactionAttachments  implements Persistable {

    public TransactionAttachments ()
    {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int transactionAttachmentsId;

    @Column
    private String fileName;

    @ManyToOne
    @JoinColumn(name="transactionDetailsId")
    private TransactionDetails transactionDetails;



    public TransactionAttachments(String fileName)
    {
        this.fileName = fileName;
    }

    public int getTransactionAttachmentsId() {
        return transactionAttachmentsId;
    }

    public void setTransactionAttachmentsId(int transactionAttachmentsId) {
        this.transactionAttachmentsId = transactionAttachmentsId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public TransactionDetails getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(TransactionDetails transactionDetails) {
        this.transactionDetails = transactionDetails;
    }

    @Override
    public Serializable getId() {
        return transactionAttachmentsId;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}

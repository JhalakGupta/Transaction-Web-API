package com.neu.repository;

import com.neu.pojo.TransactionAttachments;
import com.neu.pojo.TransactionDetails;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionAttachmentsRepository extends CrudRepository<TransactionAttachments,Integer> {

    List<TransactionAttachments> findByTransactionDetails(TransactionDetails task);
    //TransactionAttachments findTaskAttachmentsByTaskAttachmentId(int taskAttachmentId);




    TransactionAttachments findTransactionAttachmentsByTransactionAttachmentsId(int transactionAttachmentsId);
}

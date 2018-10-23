package com.neu.repository;

import com.neu.pojo.Transaction;
import com.neu.pojo.TransactionDetails;
import com.neu.pojo.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionDetails, String> {

    //TransactionDetails findByTaskId(UUID id);
    TransactionDetails findTransactionDetailsByTransactionDetailsId(UUID transactionDetailsId);
    //List<TransactionDetails> findTasksByUser(UserDetails userDetails);
}




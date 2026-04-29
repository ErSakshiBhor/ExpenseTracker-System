package com.expense.expense_tracker.repository;

import com.expense.expense_tracker.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    // Find all transactions for a specific user
    List<Transaction> findByUserId(String userId);
    
    // Find all expense transactions for a user
    List<Transaction> findByUserIdAndType(String userId, String type);
}

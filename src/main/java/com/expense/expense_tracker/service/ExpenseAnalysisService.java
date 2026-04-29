package com.expense.expense_tracker.service;

import com.expense.expense_tracker.model.Transaction;
import com.expense.expense_tracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseAnalysisService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public ExpenseAnalysisService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Map<String, Double> getTotalExpensesByCategory() {
        List<Transaction> allTransactions = transactionRepository.findAll();

        return allTransactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(tx -> tx.getAmount() != null ? tx.getAmount().doubleValue() : 0.0)));
    }

    // NEW METHOD: Get expenses by category for a specific user
    public Map<String, Double> getTotalExpensesByCategoryForUser(String userId) {
        List<Transaction> userTransactions = transactionRepository.findByUserId(userId);

        return userTransactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(tx -> tx.getAmount() != null ? tx.getAmount().doubleValue() : 0.0)));
    }
}

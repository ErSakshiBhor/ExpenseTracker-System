package com.expense.expense_tracker.service;

import com.expense.expense_tracker.model.Transaction;
import com.expense.expense_tracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MonthlyReportService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public MonthlyReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Map<String, Double> getMonthlyReport() {
        List<Transaction> allTransactions = transactionRepository.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        return allTransactions.stream()
                .filter(tx -> tx.getDate() != null && tx.getType() != null && tx.getType().equalsIgnoreCase("expense"))
                .collect(Collectors.groupingBy(
                        tx -> tx.getDate().format(formatter),
                        Collectors.summingDouble(tx -> tx.getAmount() != null ? tx.getAmount().doubleValue() : 0.0)
                ));
    }

    // NEW METHOD: Get monthly report for a specific user
    public Map<String, Double> getMonthlyReportForUser(String userId) {
        List<Transaction> userTransactions = transactionRepository.findByUserId(userId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        return userTransactions.stream()
                .filter(tx -> tx.getDate() != null && tx.getType() != null && tx.getType().equalsIgnoreCase("expense"))
                .collect(Collectors.groupingBy(
                        tx -> tx.getDate().format(formatter),
                        Collectors.summingDouble(tx -> tx.getAmount() != null ? tx.getAmount().doubleValue() : 0.0)
                ));
    }
}

package com.expense.expense_tracker.service;

import com.expense.expense_tracker.model.Transaction;
import com.expense.expense_tracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TopMerchantsService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Map<String, Double> getTopMerchants() {
        List<Transaction> transactions = transactionRepository.findAll();

        return transactions.stream()
                .filter(t -> t.getType() != null && t.getType().equalsIgnoreCase("expense"))
                .filter(t -> t.getMerchant() != null && !t.getMerchant().trim().isEmpty())
                .collect(Collectors.groupingBy(
                        t -> t.getMerchant().toLowerCase(),
                        Collectors.summingDouble(t -> t.getAmount() != null ? t.getAmount().doubleValue() : 0.0)
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    // NEW METHOD: Get top merchants for a specific user
    public Map<String, Double> getTopMerchantsForUser(String userId) {
        List<Transaction> userTransactions = transactionRepository.findByUserId(userId);

        return userTransactions.stream()
                .filter(t -> t.getType() != null && t.getType().equalsIgnoreCase("expense"))
                .filter(t -> t.getMerchant() != null && !t.getMerchant().trim().isEmpty())
                .collect(Collectors.groupingBy(
                        t -> t.getMerchant().toLowerCase(),
                        Collectors.summingDouble(t -> t.getAmount() != null ? t.getAmount().doubleValue() : 0.0)
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}

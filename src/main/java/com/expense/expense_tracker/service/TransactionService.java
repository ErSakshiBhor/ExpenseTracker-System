package com.expense.expense_tracker.service;

import com.expense.expense_tracker.model.Transaction;
import com.expense.expense_tracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new HashMap<>();

    static {
        CATEGORY_KEYWORDS.put("food", Arrays.asList("swiggy", "zomato"));
        CATEGORY_KEYWORDS.put("shopping", Arrays.asList("amazon", "flipkart"));
        CATEGORY_KEYWORDS.put("travel", Arrays.asList("uber", "ola"));
        CATEGORY_KEYWORDS.put("groceries", Arrays.asList("bigbazaar", "dmart"));
        CATEGORY_KEYWORDS.put("entertainment", Arrays.asList("netflix", "spotify"));
    }

    public Transaction saveTransaction(Transaction transaction) {
        // Automatically assign category if not provided by the user manually
        if (transaction.getCategory() == null || transaction.getCategory().trim().isEmpty()) {
            String detectedCategory = "other"; // Default fallback
            
            if (transaction.getMerchant() != null) {
                String merchant = transaction.getMerchant().toLowerCase();
                
                categorySearch:
                for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
                    for (String keyword : entry.getValue()) {
                        if (merchant.contains(keyword)) {
                            detectedCategory = entry.getKey();
                            break categorySearch;
                        }
                    }
                }
            }
            transaction.setCategory(detectedCategory);
        }
        
        return transactionRepository.save(transaction);
    }
}

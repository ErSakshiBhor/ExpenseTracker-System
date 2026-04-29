package com.expense.expense_tracker.service;

import com.expense.expense_tracker.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SmsParsingService {

    @Autowired
    private TransactionService transactionService;

    public Transaction parseAndSaveSms(String sms) {
        if (sms == null || sms.trim().isEmpty()) {
            throw new IllegalArgumentException("SMS content cannot be empty");
        }

        // Detect if it is an expense transaction
        if (!sms.toLowerCase().contains("debited")) {
            throw new IllegalArgumentException("Not an expense SMS (does not contain 'debited')");
        }

        // Extract amount using regex: Rs.(\d+)
        Pattern amountPattern = Pattern.compile("Rs\\.(\\d+)");
        Matcher amountMatcher = amountPattern.matcher(sms);
        BigDecimal amount = BigDecimal.ZERO;
        if (amountMatcher.find()) {
            amount = new BigDecimal(amountMatcher.group(1));
        } else {
            throw new IllegalArgumentException("Could not extract amount from SMS");
        }

        // Extract merchant name
        Pattern merchantPattern = Pattern.compile("at\\s+([A-Za-z0-9]+)", Pattern.CASE_INSENSITIVE);
        Matcher merchantMatcher = merchantPattern.matcher(sms);
        String merchant = "Unknown";
        if (merchantMatcher.find()) {
            merchant = merchantMatcher.group(1).toLowerCase();
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setMerchant(merchant);
        // Leave category empty so TransactionService can auto-detect it
        transaction.setType("expense");
        transaction.setDate(LocalDateTime.now());

        return transactionService.saveTransaction(transaction);
    }

    // NEW METHOD: Parse SMS and save with userId
    public Transaction parseAndSaveSms(String sms, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        
        Transaction transaction = parseAndSaveSms(sms);
        transaction.setUserId(userId);
        // Update the transaction with userId
        return transactionService.saveTransaction(transaction);
    }
}

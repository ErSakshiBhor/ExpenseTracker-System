package com.expense.expense_tracker.controller;

import com.expense.expense_tracker.model.Transaction;
import com.expense.expense_tracker.repository.TransactionRepository;
import com.expense.expense_tracker.service.ExpenseAnalysisService;
import com.expense.expense_tracker.service.InsightService;
import com.expense.expense_tracker.service.BudgetAlertService;
import com.expense.expense_tracker.service.MonthlyReportService;
import com.expense.expense_tracker.service.SmsParsingService;
import com.expense.expense_tracker.service.TransactionService;
import com.expense.expense_tracker.service.TopMerchantsService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Transaction controller - uses global CORS from CorsConfig
 * No @CrossOrigin needed here as CorsConfig handles it globally
 */
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ExpenseAnalysisService expenseAnalysisService;

    @Autowired
    private InsightService insightService;

    @Autowired
    private MonthlyReportService monthlyReportService;

    @Autowired
    private BudgetAlertService budgetAlertService;

    @Autowired
    private SmsParsingService smsParsingService;

    @Autowired
    private TopMerchantsService topMerchantsService;

    @PostMapping
    public Transaction addTransaction(@RequestBody Transaction transaction) {
        // Ensure userId is set
        if (transaction.getUserId() == null || transaction.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        return transactionService.saveTransaction(transaction);
    }

    @GetMapping
    public List<Transaction> getAllTransactions(@RequestParam(required = false) String userId) {
        if (userId != null && !userId.trim().isEmpty()) {
            // Return only transactions for this user
            return transactionRepository.findByUserId(userId);
        }
        // If no userId provided, return all (for backward compatibility)
        return transactionRepository.findAll();
    }

    @GetMapping("/analysis")
    public Map<String, Double> getExpenseAnalysis(@RequestParam(required = false) String userId) {
        if (userId != null && !userId.trim().isEmpty()) {
            return expenseAnalysisService.getTotalExpensesByCategoryForUser(userId);
        }
        return expenseAnalysisService.getTotalExpensesByCategory();
    }

    @GetMapping("/monthly-report")
    public Map<String, Double> getMonthlyReport(@RequestParam(required = false) String userId) {
        if (userId != null && !userId.trim().isEmpty()) {
            return monthlyReportService.getMonthlyReportForUser(userId);
        }
        return monthlyReportService.getMonthlyReport();
    }

    @PostMapping("/parse-sms")
    public Transaction parseSms(@RequestBody Map<String, String> payload) {
        String sms = payload.get("sms");
        String userId = payload.get("userId");
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        return smsParsingService.parseAndSaveSms(sms, userId);
    }

    @PostMapping("/budget")
    public com.expense.expense_tracker.model.Budget setBudget(@RequestBody com.expense.expense_tracker.model.Budget budget) {
        return budgetAlertService.saveBudget(budget);
    }

    @GetMapping("/budget-alerts")
    public List<String> getBudgetAlerts() {
        return budgetAlertService.getBudgetAlerts();
    }

    @GetMapping("/top-merchants")
    public Map<String, Double> getTopMerchants(@RequestParam(required = false) String userId) {
        if (userId != null && !userId.trim().isEmpty()) {
            return topMerchantsService.getTopMerchantsForUser(userId);
        }
        return topMerchantsService.getTopMerchants();
    }
}

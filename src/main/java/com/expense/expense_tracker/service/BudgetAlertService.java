package com.expense.expense_tracker.service;

import com.expense.expense_tracker.model.Budget;
import com.expense.expense_tracker.model.Transaction;
import com.expense.expense_tracker.repository.BudgetRepository;
import com.expense.expense_tracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetAlertService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public Budget saveBudget(Budget budget) {
        if (budget.getCategory() != null) {
            String cat = budget.getCategory().toLowerCase();
            budget.setCategory(cat);
            
            // Check if budget for this category already exists, if so update it
            Optional<Budget> existing = budgetRepository.findByCategory(cat);
            if (existing.isPresent()) {
                Budget toUpdate = existing.get();
                toUpdate.setLimitAmount(budget.getLimitAmount());
                return budgetRepository.save(toUpdate);
            }
        }
        return budgetRepository.save(budget);
    }

    public List<String> getBudgetAlerts() {
        List<String> alerts = new ArrayList<>();
        List<Budget> allBudgets = budgetRepository.findAll();
        
        if (allBudgets.isEmpty()) return alerts;

        List<Transaction> transactions = transactionRepository.findAll();
        YearMonth currentMonth = YearMonth.now();

        // Calculate this month's spending by category for expenses
        Map<String, Double> currentSpending = transactions.stream()
                .filter(t -> t.getType() != null && t.getType().equalsIgnoreCase("expense"))
                .filter(t -> t.getDate() != null && YearMonth.from(t.getDate()).equals(currentMonth))
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().toLowerCase(),
                        Collectors.summingDouble(t -> t.getAmount() != null ? t.getAmount().doubleValue() : 0.0)
                ));

        for (Budget budget : allBudgets) {
            String category = budget.getCategory();
            double limit = budget.getLimitAmount() != null ? budget.getLimitAmount().doubleValue() : 0.0;
            double spent = currentSpending.getOrDefault(category, 0.0);

            if (spent > limit) {
                alerts.add("You have exceeded your " + category + " budget.");
            } else if (spent >= (limit * 0.8)) {
                alerts.add("You are close to exceeding your " + category + " budget.");
            }
        }

        return alerts;
    }
}

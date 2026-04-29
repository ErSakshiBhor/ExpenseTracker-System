package com.expense.expense_tracker.service;

import com.expense.expense_tracker.model.Transaction;
import com.expense.expense_tracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InsightService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public InsightService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<String> getInsights() {
        List<Transaction> allTransactions = transactionRepository.findAll();
        List<String> insights = new ArrayList<>();

        if (allTransactions.isEmpty()) {
            insights.add("No spending data available to generate insights.");
            return insights;
        }

        // Filter only expenses
        List<Transaction> expenses = allTransactions.stream()
                .filter(t -> t.getType() != null && t.getType().equalsIgnoreCase("expense"))
                .collect(Collectors.toList());

        if (expenses.isEmpty()) {
            insights.add("No existing expense records found.");
            return insights;
        }

        // Group by category for all-time
        Map<String, Double> allTimeByCategory = expenses.stream()
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(tx -> tx.getAmount() != null ? tx.getAmount().doubleValue() : 0.0)
                ));

        // Find highest all-time spending
        String highestCategory = null;
        double maxSpending = -1;
        for (Map.Entry<String, Double> entry : allTimeByCategory.entrySet()) {
            if (entry.getValue() > maxSpending) {
                maxSpending = entry.getValue();
                highestCategory = entry.getKey();
            }
        }

        if (highestCategory != null) {
            insights.add("You are spending a large portion on " + highestCategory + ".");
            if (highestCategory.equalsIgnoreCase("food")) {
                insights.add("Consider reducing spending on food delivery services.");
            } else {
                insights.add("Consider reducing expenses in the " + highestCategory + " category.");
            }
        }

        // Month over Month Analysis
        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth = currentMonth.minusMonths(1);

        Map<String, Double> currentMonthSpending = getCategorySpendingForMonth(expenses, currentMonth);
        Map<String, Double> previousMonthSpending = getCategorySpendingForMonth(expenses, previousMonth);

        for (Map.Entry<String, Double> entry : currentMonthSpending.entrySet()) {
            String category = entry.getKey();
            double currentSpent = entry.getValue();
            double prevSpent = previousMonthSpending.getOrDefault(category, 0.0);

            if (prevSpent > 0 && currentSpent > prevSpent) {
                double increasePercent = ((currentSpent - prevSpent) / prevSpent) * 100;
                if (increasePercent >= 5) { // Report if increase is notable
                    insights.add(String.format("Your %s expenses increased by %.0f%% compared to last month.", category, increasePercent));
                }
            } else if (prevSpent == 0 && currentSpent > 0) {
                 insights.add(String.format("You started spending on %s this month ($%.2f).", category, currentSpent));
            }
        }

        return insights;
    }

    private Map<String, Double> getCategorySpendingForMonth(List<Transaction> expenses, YearMonth yearMonth) {
        return expenses.stream()
                .filter(t -> t.getDate() != null && YearMonth.from(t.getDate()).equals(yearMonth))
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(tx -> tx.getAmount() != null ? tx.getAmount().doubleValue() : 0.0)
                ));
    }
}

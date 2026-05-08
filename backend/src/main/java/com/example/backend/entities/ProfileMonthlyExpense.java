package com.example.backend.entities;

import com.example.backend.types.ExpenseCategory;
import jakarta.persistence.*;

import java.time.Month;
import java.time.Year;

@Entity
@Table(name = "monthly_expense")
public class ProfileMonthlyExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(name = "expense_category", nullable = false)
    private ExpenseCategory expenseCategory;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "month", nullable = false)
    private Month month;

    @Column(name = "amount", nullable = false)
    private Integer amount = 0;

    @Column(name = "expenses_count", nullable = false)
    private Short expensesCount = 0;

    public Long getId() {
        return id;
    }

    public ExpenseCategory getExpenseCategory() {
        return expenseCategory;
    }

    public Integer getYear() {
        return year;
    }

    public Month getMonth() {
        return month;
    }

    public Profile getProfile() {return profile;}

    public Integer getAmount() {
        return amount;
    }

    public Short getExpensesCount() {
        return expensesCount;
    }

    public void addAmount(Integer amount) {
        this.amount += amount;
        expensesCount++;
    }

    public ProfileMonthlyExpense() {}

    public ProfileMonthlyExpense(Profile profile, ExpenseCategory category, Year year, Month month) {
        expenseCategory = category;
        this.month = month;
        this.profile = profile;
        this.year = year.getValue();
    }

}
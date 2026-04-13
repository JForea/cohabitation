package com.example.backend.entities;

import com.example.backend.entities.keys.MonthlyExpenseKey;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "monthly_expense")
public class MonthlyExpense {
    @EmbeddedId
    private MonthlyExpenseKey monthlyExpenseKey;

    @Column(name = "sum", nullable = false)
    private Integer sum;

    public Integer getSum() {
        return sum;
    }

    public MonthlyExpenseKey getMonthlyExpenseKey() {
        return monthlyExpenseKey;
    }

    public void setMonthlyExpenseKey(MonthlyExpenseKey monthlyExpenseKey) {
        this.monthlyExpenseKey = monthlyExpenseKey;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> objectEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != objectEffectiveClass) {
            return false;
        }
        MonthlyExpense that = (MonthlyExpense) o;
        return getMonthlyExpenseKey() != null && Objects.equals(getMonthlyExpenseKey(), that.getMonthlyExpenseKey());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(monthlyExpenseKey);
    }
}
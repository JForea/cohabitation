package com.example.backend.entities;

import com.example.backend.entities.keys.ProfileMonthlyExpenseKey;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "monthly_expense")
public class ProfileMonthlyExpense {
    @EmbeddedId
    private ProfileMonthlyExpenseKey profileMonthlyExpenseKey;

    @Column(name = "amount", nullable = false)
    private Integer amount = 0;

    public Integer getAmount() {
        return amount;
    }

    public void addAmount(Integer amount) {
        this.amount += amount;
    }

    public ProfileMonthlyExpenseKey getProfileMonthlyExpenseKey() {
        return profileMonthlyExpenseKey;
    }

    public void setProfileMonthlyExpenseKey(ProfileMonthlyExpenseKey profileMonthlyExpenseKey) {
        this.profileMonthlyExpenseKey = profileMonthlyExpenseKey;
    }

    public ProfileMonthlyExpense() {}

    public ProfileMonthlyExpense(ProfileMonthlyExpenseKey key) {
        profileMonthlyExpenseKey = key;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> objectEffectiveClass = o instanceof HibernateProxy proxy ?
                proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ?
                proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != objectEffectiveClass) {
            return false;
        }
        ProfileMonthlyExpense that = (ProfileMonthlyExpense) o;
        return getProfileMonthlyExpenseKey() != null &&
                Objects.equals(getProfileMonthlyExpenseKey(), that.getProfileMonthlyExpenseKey());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(profileMonthlyExpenseKey);
    }
}
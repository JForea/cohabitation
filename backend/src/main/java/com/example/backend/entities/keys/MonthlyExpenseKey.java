package com.example.backend.entities.keys;

import com.example.backend.entities.Apartment;
import jakarta.persistence.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.Month;
import java.util.Objects;

@Embeddable
public class MonthlyExpenseKey {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    @Column(name = "month")
    private Month month;

    public Month getMonth() {
        return month;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public MonthlyExpenseKey() {}

    public MonthlyExpenseKey(Apartment apartment, Month month) {
        this.apartment = apartment;
        this.month = month;
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
        MonthlyExpenseKey that = (MonthlyExpenseKey) o;
        return getApartment() != null && Objects.equals(getApartment(), that.getApartment())
                && getMonth() != null && Objects.equals(getMonth(), that.getMonth());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(apartment, month);
    }
}
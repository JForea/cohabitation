package com.example.backend.entities;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "apartment")
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 31)
    private String name;

    @Column(name = "address", length = 63)
    private String address;

    @Column(name = "invite_code", unique = true, length = 8)
    private String inviteCode;

    @Column(name = "budget", nullable = false)
    private Integer budget = 50000;

    @OneToMany(mappedBy = "monthlyExpenseKey.apartment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<MonthlyExpense> monthlyExpenses = new LinkedHashSet<>();

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Rule> rules = new LinkedHashSet<>();

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Expense> expenses = new LinkedHashSet<>();

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Buying> buyings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "apartment", orphanRemoval = true)
    private Set<Profile> profiles = new LinkedHashSet<>();

    @OneToMany(mappedBy = "apartment", orphanRemoval = true)
    private Set<Event> events = new LinkedHashSet<>();

    @OneToMany(mappedBy = "apartment", orphanRemoval = true)
    private Set<Task> tasks = new LinkedHashSet<>();

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public Set<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(Set<Profile> profiles) {
        this.profiles = profiles;
    }

    public Set<Buying> getBuyings() {
        return buyings;
    }

    public void setBuyings(Set<Buying> buyings) {
        this.buyings = buyings;
    }

    public Set<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(Set<Expense> expenses) {
        this.expenses = expenses;
    }

    public Set<Rule> getRules() {
        return rules;
    }

    public void setRules(Set<Rule> rules) {
        this.rules = rules;
    }

    public Set<MonthlyExpense> getMonthlyExpenses() {
        return monthlyExpenses;
    }

    public void setMonthlyExpenses(Set<MonthlyExpense> monthlyExpenses) {
        this.monthlyExpenses = monthlyExpenses;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

}
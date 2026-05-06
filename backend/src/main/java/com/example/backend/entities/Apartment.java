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

    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @Column(name = "address", length = 64)
    private String address;

    @Column(name = "invite_code", unique = true, length = 8)
    private String inviteCode;

    @Column(name = "budget", nullable = false)
    private Integer budget = 50000;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Rule> rules = new LinkedHashSet<>();

    @OneToMany(mappedBy = "apartment", orphanRemoval = true)
    private Set<Profile> profiles = new LinkedHashSet<>();

    @OneToMany(mappedBy = "apartment", orphanRemoval = true)
    private Set<Event> events = new LinkedHashSet<>();

    public Apartment() {}

    public Apartment(String name, String address) {
        this.name = name;
        this.address = address;
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

    public Set<Rule> getRules() {
        return rules;
    }

    public void setRules(Set<Rule> rules) {
        this.rules = rules;
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
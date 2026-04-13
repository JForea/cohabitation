package com.example.backend.entities;

import com.example.backend.types.Color;
import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "user_")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "name", nullable = false, length = 63)
    private String name;

    @Column(name = "male", nullable = false)
    private Boolean male = false;

    @Enumerated
    @Column(name = "avatar_color", nullable = false)
    private Color avatarColor;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Set<Profile> profiles = new LinkedHashSet<>();

    public Set<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(Set<Profile> profiles) {
        this.profiles = profiles;
    }

    public Color getAvatarColor() {
        return avatarColor;
    }

    public void setAvatarColor(Color avatarColor) {
        this.avatarColor = avatarColor;
    }

    public Boolean getMale() {
        return male;
    }

    public void setMale(Boolean male) {
        this.male = male;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Integer getId() {
        return id;
    }

}
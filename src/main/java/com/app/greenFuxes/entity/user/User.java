package com.app.greenFuxes.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(unique = true)
    private String userName;

    @JsonIgnore
    private String password;

    @Column(unique = true)
    private String email;

    private String firstName;
    private String lastName;
    private Integer age;
    private Gender gender;
    private String profileImageUrl;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinDate;
    private String role;
    private String[] authorities;
    private Boolean active;
    private Boolean notLocked;

    public User(String userName, String password, String role, Boolean active, Boolean notLocked) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.active = active;
        this.notLocked = notLocked;
    }
}

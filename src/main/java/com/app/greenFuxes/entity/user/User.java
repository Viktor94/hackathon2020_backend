package com.app.greenFuxes.entity.user;

import com.app.greenFuxes.entity.reservedDate.ReservedDate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  @Column(unique = true, length = 250)
  private String userName;

  @JsonIgnore
  private String password;

  @Column(unique = true, length = 250)
  private String email;

  private String firstName;
  private String lastName;
  private String role;
  private String[] authorities;
  private Boolean active;
  private Boolean notLocked;
  private String profileImageUrl;

  @ManyToOne
  private ReservedDate reservedDate;

  public User(String userName, String password, String role, String[] authorities, Boolean active, Boolean notLocked) {
    this.userName = userName;
    this.password = password;
    this.role = role;
    this.authorities = authorities;
    this.active = active;
    this.notLocked = notLocked;
  }
}

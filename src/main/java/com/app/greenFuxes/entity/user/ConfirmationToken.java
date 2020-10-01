package com.app.greenFuxes.entity.user;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class ConfirmationToken {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "token_id", length = 250)
  private Long tokenid;

  @Column(name = "confirmation_token", length = 250)
  private String confirmationToken;

  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @Temporal(TemporalType.TIMESTAMP)
  private Date expirationTime;

  @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
  @JoinColumn(nullable = false, name = "user_id")
  private User user;

  public ConfirmationToken(User user) {
    this.user = user;
    createdDate = new Date();
    confirmationToken = UUID.randomUUID().toString();
    this.expirationTime = new Date(new Date().getTime() + 86400000); // 24 hours
  }
}

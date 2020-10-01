package com.app.greenFuxes.security;

import static java.util.Arrays.stream;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component
public class JwtUtility {
  private static final String SECRET = System.getenv("JWT_SECRET");

  public String generateJwtToken(UserPrincipal userPrincipal) {
    String[] claims = getClaimsFromUser(userPrincipal);
    return JWT.create()
        .withIssuer(SecurityConstant.GET_ARRAYS_LLC)
        .withAudience(SecurityConstant.GET_ARRAYS_ADMINISTRATION)
        .withIssuedAt(new Date())
        .withSubject(userPrincipal.getUsername())
        .withArrayClaim(SecurityConstant.AUTHORITIES, claims)
        .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstant.EXPIRATION_TIME))
        .sign(Algorithm.HMAC512(SECRET.getBytes()));
  }

  public List<GrantedAuthority> getAuthorities(String token) {
    String[] claims = getClaimsFromToken(token);
    return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
  }

  public Authentication getAuthentication(
      String username, List<GrantedAuthority> authorities, HttpServletRequest request) {
    UsernamePasswordAuthenticationToken userPasswordAuthToken =
        new UsernamePasswordAuthenticationToken(username, null, authorities);
    userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    return userPasswordAuthToken;
  }

  public boolean isTokenValid(String username, String token) {
    JWTVerifier verifier = getJWTVerifier();
    return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier, token);
  }

  public String getSubject(String token) {
    JWTVerifier verifier = getJWTVerifier();
    return verifier.verify(token).getSubject();
  }

  private boolean isTokenExpired(JWTVerifier verifier, String token) {
    Date expiration = verifier.verify(token).getExpiresAt();
    return expiration.before(new Date());
  }

  private String[] getClaimsFromToken(String token) {
    JWTVerifier verifier = getJWTVerifier();
    return verifier.verify(token).getClaim(SecurityConstant.AUTHORITIES).asArray(String.class);
  }

  private JWTVerifier getJWTVerifier() {
    JWTVerifier verifier;
    try {
      Algorithm algorithm = Algorithm.HMAC512(SECRET);
      verifier = JWT.require(algorithm).withIssuer(SecurityConstant.GET_ARRAYS_LLC).build();
    } catch (JWTVerificationException e) {
      throw new JWTVerificationException(SecurityConstant.TOKEN_CANNOT_BE_VERIFIED);
    }
    return verifier;
  }

  private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
    List<String> authorities = new ArrayList<>();
    for (GrantedAuthority grantedAuthority : userPrincipal.getAuthorities()) {
      authorities.add(grantedAuthority.getAuthority());
    }
    return authorities.toArray(new String[0]);
  }
}

package com.app.greenFuxes.filter;

import com.app.greenFuxes.security.JwtUtility;
import com.app.greenFuxes.security.SecurityConstant;
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  @Autowired private JwtUtility jwtUtility;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (request.getMethod().equalsIgnoreCase(SecurityConstant.OPTIONS_HTTP_METHOD)) {
      response.setStatus(HttpStatus.OK.value());
    } else {
      String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
      if (authorizationHeader == null
          || !authorizationHeader.startsWith(SecurityConstant.TOKEN_PREFIX)) {
        filterChain.doFilter(request, response);
        return;
      }
      String token = authorizationHeader.substring(SecurityConstant.TOKEN_PREFIX.length());
      String username = jwtUtility.getSubject(token);
      if (jwtUtility.isTokenValid(username, token)
          && SecurityContextHolder.getContext().getAuthentication() == null) {
        List<GrantedAuthority> authorities = jwtUtility.getAuthorities(token);
        Authentication authentication =
            jwtUtility.getAuthentication(username, authorities, request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } else {
        SecurityContextHolder.clearContext();
      }
    }
    filterChain.doFilter(request, response);
  }
}

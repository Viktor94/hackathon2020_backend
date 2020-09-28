package com.app.greenFuxes.security;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class SecurityConstant {
    public static final long EXPIRATION_TIME = 432_000_000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified.";
    public static final String GET_ARRAYS_LLC = "Get Arrays, LLC";
    public static final String GET_ARRAYS_ADMINISTRATION = "User Management Portal";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to aces this page.";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to aces this page.";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String APP_BASE_URL = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
}

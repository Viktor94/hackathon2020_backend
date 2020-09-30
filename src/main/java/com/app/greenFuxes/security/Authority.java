package com.app.greenFuxes.security;

public class Authority {
  public static final String[] GUEST_AUTHORITIES = {"user"};
  public static final String[] VIP_AUTHORITIES = {"user"};
  public static final String[] USER_AUTHORITIES = {"user"};
  public static final String[] ADMIN_AUTHORITIES = {"user", "admin"};
  public static final String[] SUPER_ADMIN_AUTHORITIES = {"user", "admin", "superAdmin"};
}

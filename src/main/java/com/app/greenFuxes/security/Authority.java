package com.app.greenFuxes.security;

public class Authority {
  public static final String[] GUEST_AUTHORITIES = {"read"};
  public static final String[] VIP_AUTHORITIES = {"read"};
  public static final String[] USER_AUTHORITIES = {"read"};
  public static final String[] ADMIN_AUTHORITIES = {"read", "create", "update"};
  public static final String[] SUPER_ADMIN_AUTHORITIES = {"read", "create", "update", "delete"};
}

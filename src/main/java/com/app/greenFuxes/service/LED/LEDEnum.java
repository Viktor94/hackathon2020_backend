package com.app.greenFuxes.service.LED;

public enum LEDEnum {
  RED("configRed.txt"),
  GREEN("configGreen.txt"),
  ORANGE("configOrange.txt");

  private String value;

  private LEDEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}

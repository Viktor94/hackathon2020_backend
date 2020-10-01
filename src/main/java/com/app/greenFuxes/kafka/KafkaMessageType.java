package com.app.greenFuxes.kafka;

public enum KafkaMessageType {
  NOTIFICATION_OF_ENTRY("Notification");

  private String type;

  KafkaMessageType(String type) {
    this.type = type;
  }

  public String getValue() {
    return this.type;
  }
}

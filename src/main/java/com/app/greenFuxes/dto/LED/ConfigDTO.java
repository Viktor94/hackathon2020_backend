package com.app.greenFuxes.dto.LED;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.kafka.common.protocol.types.Field.Str;

@Getter
@Setter
@NoArgsConstructor
public class ConfigDTO {

  private String configText;

  public ConfigDTO(String configText) {
    this.configText = configText;
  }
}

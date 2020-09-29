package com.app.greenFuxes.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KafkaMessage {

    private String type;
    private String message;

}
package com.app.greenFuxes.dto.canteen;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CanteenSettingDTO {
    private Integer maxCanteenCapacity;
    private Integer lunchtimeInMinute;
}

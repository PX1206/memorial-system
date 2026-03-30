package com.memorial.system.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("墓碑提醒开关")
public class TombReminderToggleParam {

    @NotNull(message = "id不能为空")
    @ApiModelProperty(required = true)
    private Long id;

    @NotNull(message = "enabled不能为空")
    @ApiModelProperty(required = true)
    private Boolean enabled;
}

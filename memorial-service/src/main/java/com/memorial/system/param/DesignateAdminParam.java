package com.memorial.system.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("指定管理员参数")
public class DesignateAdminParam {
    @NotNull(message = "家族ID不能为空")
    @ApiModelProperty("家族ID（type=家族的根）")
    private Long familyId;

    @NotNull(message = "用户ID不能为空")
    @ApiModelProperty("要指定为管理员的用户ID")
    private Long userId;
}

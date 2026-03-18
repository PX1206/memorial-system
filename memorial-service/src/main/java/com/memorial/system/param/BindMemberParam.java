package com.memorial.system.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@ApiModel(value = "扫码绑定成员参数")
public class BindMemberParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "绑定码不能为空")
    @ApiModelProperty(value = "成员绑定码（二维码中包含）", required = true)
    private String bindCode;
}

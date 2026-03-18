package com.memorial.system.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel(value = "邀请码加入家族参数")
public class JoinFamilyByCodeParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "邀请码不能为空")
    @ApiModelProperty(value = "家族邀请码", required = true)
    private String inviteCode;

    @ApiModelProperty("与族长关系")
    private String relation;

    @ApiModelProperty("家族角色：族长/管理员/成员（默认成员）")
    private String role;
}


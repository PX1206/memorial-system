package com.memorial.system.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel(value = "家族成员参数")
public class FamilyMemberParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("成员ID（修改时必传）")
    private Long id;

    @NotNull(message = "家族ID不能为空")
    @ApiModelProperty(value = "家族ID", required = true)
    private Long familyId;

    @NotBlank(message = "姓名不能为空")
    @ApiModelProperty(value = "成员姓名", required = true)
    private String name;

    @ApiModelProperty("与族长关系")
    private String relation;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("家族角色：族长/管理员/成员")
    private String role;
}

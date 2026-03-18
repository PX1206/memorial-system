package com.memorial.system.param;

import com.memorial.common.pagination.BasePageOrderParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "家族成员分页参数")
public class FamilyMemberPageParam extends BasePageOrderParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("家族ID（为空表示全部家族）")
    private Long familyId;

    @ApiModelProperty("姓名关键字")
    private String keyword;

    @ApiModelProperty(value = "当前用户ID（角色2时用于过滤）", hidden = true)
    private Long currentUserId;
    @ApiModelProperty(value = "是否超级管理员（角色1可看全部）", hidden = true)
    private Boolean isAdmin;
}

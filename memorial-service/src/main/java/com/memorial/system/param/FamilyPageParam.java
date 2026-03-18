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
@ApiModel(value = "家族分页参数")
public class FamilyPageParam extends BasePageOrderParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("家族名称关键字")
    private String keyword;

    @ApiModelProperty("家族ID（选中树节点时过滤该节点及子孙）")
    private Long familyId;

    @ApiModelProperty(value = "当前用户ID（角色2时用于过滤）", hidden = true)
    private Long currentUserId;
    @ApiModelProperty(value = "是否超级管理员（角色1可看全部）", hidden = true)
    private Boolean isAdmin;
}

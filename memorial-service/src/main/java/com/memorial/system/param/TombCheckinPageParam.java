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
@ApiModel(value = "打卡记录分页参数")
public class TombCheckinPageParam extends BasePageOrderParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("关键字（墓碑名/访客名）")
    private String keyword;

    @ApiModelProperty("打卡类型")
    private String type;

    @ApiModelProperty("墓碑ID")
    private Long tombId;

    @ApiModelProperty(value = "当前用户ID（角色2时用于过滤）", hidden = true)
    private Long currentUserId;
    @ApiModelProperty(value = "是否超级管理员（角色1可看全部）", hidden = true)
    private Boolean isAdmin;
}

package com.memorial.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "仪表盘统计信息")
public class DashboardVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("墓碑总数")
    private Long tombCount;

    @ApiModelProperty("注册用户数")
    private Long userCount;

    @ApiModelProperty("家族数量")
    private Long familyCount;

    @ApiModelProperty("留言总数")
    private Long messageCount;

    @ApiModelProperty("最近打卡记录")
    private List<TombCheckinVO> recentCheckins;

    @ApiModelProperty("最新留言")
    private List<TombMessageVO> recentMessages;
}

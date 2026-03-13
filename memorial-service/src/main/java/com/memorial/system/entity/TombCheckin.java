package com.memorial.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.memorial.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("tomb_checkin")
@ApiModel(value = "TombCheckin对象")
public class TombCheckin extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("墓碑ID")
    private Long tombId;

    @ApiModelProperty("访客姓名")
    private String visitorName;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("打卡类型")
    private String type;

    @ApiModelProperty("IP地址")
    private String ip;

    @ApiModelProperty("位置")
    private String location;

    private Date createTime;
    private Boolean delFlag;
}

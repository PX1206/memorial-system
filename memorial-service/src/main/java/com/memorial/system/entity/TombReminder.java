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
@TableName("tomb_reminder")
@ApiModel(value = "TombReminder")
public class TombReminder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("墓碑ID")
    private Long tombId;

    @ApiModelProperty("与墓主关系")
    private String relationship;

    @ApiModelProperty("是否开启")
    private Boolean enabled;

    @ApiModelProperty("短信渠道")
    private Boolean channelSms;

    @ApiModelProperty("微信渠道（预留）")
    private Boolean channelWechat;

    @ApiModelProperty("事件类型 JSON")
    private String eventTypes;

    @ApiModelProperty("自定义日期 JSON")
    private String customDates;

    @ApiModelProperty("自定义日期备注 JSON，与 custom_dates 一一对应，最多3条")
    private String customDateRemarks;

    @ApiModelProperty("提前天数 JSON")
    private String advanceOffsets;

    @ApiModelProperty("按农历推算的事件 JSON：DEATH_ANNIVERSARY、BIRTHDAY 子集（已废弃，以墓碑 lunar_flag 为准）")
    private String lunarEventTypes;

    private Date createTime;
    private Long createBy;
    private Date updateTime;
    private Long updateBy;
}

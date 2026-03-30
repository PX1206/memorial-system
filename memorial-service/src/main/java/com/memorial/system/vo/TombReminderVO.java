package com.memorial.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel("墓碑提醒配置")
public class TombReminderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long tombId;

    @ApiModelProperty("与墓主关系")
    private String relationship;

    @ApiModelProperty("是否开启")
    private Boolean enabled;

    @ApiModelProperty("短信")
    private Boolean channelSms;

    @ApiModelProperty("微信（预留）")
    private Boolean channelWechat;

    private List<String> eventTypes;
    private List<String> customDates;
    @ApiModelProperty("自定义日期备注，与 customDates 一一对应")
    private List<String> customDateRemarks;
    private List<Integer> advanceOffsets;
}

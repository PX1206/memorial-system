package com.memorial.system.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("墓碑提醒保存参数")
public class TombReminderParam {

    private Long id;

    @NotNull(message = "墓碑ID不能为空")
    @ApiModelProperty(value = "墓碑ID", required = true)
    private Long tombId;

    @NotBlank(message = "请选择与墓主的关系")
    @ApiModelProperty(value = "与墓主关系", required = true)
    private String relationship;

    @ApiModelProperty("是否开启，默认 true")
    private Boolean enabled;

    @ApiModelProperty("短信，默认 true")
    private Boolean channelSms;

    @ApiModelProperty("微信（预留）")
    private Boolean channelWechat;

    @NotNull(message = "请选择提醒日期类型")
    @ApiModelProperty(value = "事件类型编码列表", required = true)
    private List<String> eventTypes;

    @ApiModelProperty("自定义日期，最多3个，MM-dd 或 yyyy-MM-dd")
    private List<String> customDates;

    @ApiModelProperty("自定义日期备注，与 customDates 下标一一对应，可选，如纪念日名称")
    private List<String> customDateRemarks;

    @ApiModelProperty("固定提前项：0 当天、1、3，可为空（与 customAdvanceDays 至少其一）")
    private List<Integer> advanceOffsets;

    @ApiModelProperty("额外自定义提前天数（与 advanceOffsets 合并去重）")
    private List<Integer> customAdvanceDays;
}

package com.memorial.system.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel(value = "墓碑参数")
public class TombParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("墓碑ID（修改时必传）")
    private Long id;

    @NotBlank(message = "逝者姓名不能为空")
    @Size(max = 64, message = "逝者姓名不能超过64字")
    @ApiModelProperty(value = "逝者姓名", required = true)
    private String name;

    @Size(max = 500, message = "照片URL不能超过500字符")
    @ApiModelProperty("照片URL")
    private String photo;

    @ApiModelProperty("出生日期")
    private String birthday;

    @ApiModelProperty("逝世日期")
    private String deathday;

    @Size(max = 1000, message = "个人简介HTML内容不能超过1000字符")
    @ApiModelProperty("个人简介")
    private String biography;

    @ApiModelProperty("生平事迹")
    private String story;

    @Size(max = 32, message = "墓志铭不能超过32字")
    @ApiModelProperty("墓志铭")
    private String epitaph;

    @ApiModelProperty("是否开放访客互动（留言/献花等）：true开放，false仅家族成员可互动")
    private Boolean visitorActionOpen;

    @ApiModelProperty("所属家族ID")
    private Long familyId;

    @Size(max = 200, message = "所处位置不能超过200字")
    @ApiModelProperty("所处位置（非必填）")
    private String address;
}

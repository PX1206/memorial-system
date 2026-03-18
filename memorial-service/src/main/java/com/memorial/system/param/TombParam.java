package com.memorial.system.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel(value = "墓碑参数")
public class TombParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("墓碑ID（修改时必传）")
    private Long id;

    @NotBlank(message = "逝者姓名不能为空")
    @ApiModelProperty(value = "逝者姓名", required = true)
    private String name;

    @ApiModelProperty("照片URL")
    private String photo;

    @ApiModelProperty("出生日期")
    private String birthday;

    @ApiModelProperty("逝世日期")
    private String deathday;

    @ApiModelProperty("个人简介")
    private String biography;

    @ApiModelProperty("生平事迹")
    private String story;

    @ApiModelProperty("墓志铭")
    private String epitaph;

    @ApiModelProperty("是否开放访客互动（留言/献花等）：true开放，false仅家族成员可互动")
    private Boolean visitorActionOpen;

    @ApiModelProperty("所属家族ID")
    private Long familyId;
}

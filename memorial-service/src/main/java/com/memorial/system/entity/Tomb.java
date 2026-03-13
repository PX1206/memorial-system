package com.memorial.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@ApiModel(value = "Tomb对象")
public class Tomb extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("逝者姓名")
    private String name;

    @ApiModelProperty("照片URL")
    private String photo;

    @ApiModelProperty("出生日期")
    private Date birthday;

    @ApiModelProperty("逝世日期")
    private Date deathday;

    @ApiModelProperty("个人简介")
    private String biography;

    @ApiModelProperty("生平事迹")
    private String story;

    @ApiModelProperty("所属家族ID")
    private Long familyId;

    @ApiModelProperty("二维码URL")
    private String qrCode;

    @ApiModelProperty("访问量")
    private Integer visitCount;

    @ApiModelProperty("留言数")
    private Integer messageCount;

    @ApiModelProperty("创建用户ID")
    private Long userId;

    @ApiModelProperty("状态：0禁用 1正常")
    private Integer status;

    private Date createTime;
    private Long createBy;
    private Date updateTime;
    private Long updateBy;
    private Boolean delFlag;
}

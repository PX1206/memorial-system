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
@ApiModel(value = "Family对象")
public class Family extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("上级家族ID，0表示顶级")
    private Long pid;

    @ApiModelProperty("家族名称")
    private String name;

    @ApiModelProperty("家族简介")
    private String description;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("所在地区")
    private String address;

    @ApiModelProperty("创建人用户ID")
    private Long founderId;

    @ApiModelProperty("族长用户ID（家庭/支族时 designated）")
    private Long chiefId;

    @ApiModelProperty("根家族ID（type=家族时等于id）")
    private Long rootId;

    @ApiModelProperty("创建人姓名")
    private String founderName;

    @ApiModelProperty("成员数")
    private Integer memberCount;

    @ApiModelProperty("墓碑数")
    private Integer tombCount;

    @ApiModelProperty("家族邀请码")
    private String inviteCode;

    @ApiModelProperty("状态：0禁用 1正常")
    private Integer status;

    private Date createTime;
    private Long createBy;
    private Date updateTime;
    private Long updateBy;
    private Boolean delFlag;
}

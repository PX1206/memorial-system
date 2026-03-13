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
@TableName("family_member")
@ApiModel(value = "FamilyMember对象")
public class FamilyMember extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("家族ID")
    private Long familyId;

    @ApiModelProperty("成员姓名")
    private String name;

    @ApiModelProperty("关联用户ID")
    private Long userId;

    @ApiModelProperty("与族长关系")
    private String relation;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("家族角色：族长/管理员/成员")
    private String role;

    @ApiModelProperty("头像")
    private String avatar;

    private Date createTime;
    private Long createBy;
    private Date updateTime;
    private Long updateBy;
    private Boolean delFlag;
}

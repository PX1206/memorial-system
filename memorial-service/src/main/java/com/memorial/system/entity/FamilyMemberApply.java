package com.memorial.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("family_member_apply")
@ApiModel(value = "家族成员申请")
public class FamilyMemberApply {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("家族ID")
    private Long familyId;

    @ApiModelProperty("申请用户ID")
    private Long userId;

    @ApiModelProperty("与族长关系")
    private String relation;

    @ApiModelProperty("申请理由")
    private String reason;

    @ApiModelProperty("状态：pending/approved/rejected")
    private String status;

    @ApiModelProperty("拒绝理由")
    private String rejectReason;

    private Date createTime;
    private Date updateTime;
    private Long updateBy;
}

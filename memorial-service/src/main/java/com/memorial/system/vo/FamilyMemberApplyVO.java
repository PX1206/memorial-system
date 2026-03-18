package com.memorial.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel("家族成员申请VO")
public class FamilyMemberApplyVO {

    private Long id;
    private Long familyId;
    @ApiModelProperty("家族名称")
    private String familyName;
    private Long userId;
    @ApiModelProperty("申请人昵称/账号")
    private String userName;
    private String relation;
    private String reason;
    private String status;
    private String rejectReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}

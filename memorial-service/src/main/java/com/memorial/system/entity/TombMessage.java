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
@TableName("tomb_message")
@ApiModel(value = "TombMessage对象")
public class TombMessage extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("墓碑ID")
    private Long tombId;

    @ApiModelProperty("留言人姓名")
    private String visitorName;

    @ApiModelProperty("留言用户ID")
    private Long userId;

    @ApiModelProperty("留言人头像")
    private String avatar;

    @ApiModelProperty("留言内容")
    private String content;

    @ApiModelProperty("配图 URL 列表 JSON 数组，最多 3 张")
    private String imageUrls;

    @ApiModelProperty("审核状态：pending/approved/rejected")
    private String status;

    @ApiModelProperty("拒绝理由")
    private String rejectReason;

    private Date createTime;
    private Long createBy;
    private Date updateTime;
    private Long updateBy;
    private Boolean delFlag;
}

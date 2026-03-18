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
@TableName("tomb_story")
@ApiModel(value = "TombStory对象")
public class TombStory extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("墓碑ID")
    private Long tombId;

    @ApiModelProperty("事迹标题")
    private String title;

    @ApiModelProperty("事迹内容（富文本HTML）")
    private String content;

    @ApiModelProperty("排序（越小越靠前）")
    private Integer sort;

    private Date createTime;
    private Long createBy;
    private Date updateTime;
    private Long updateBy;
    private Boolean delFlag;
}


package com.memorial.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value = "留言信息")
public class TombMessageVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long tombId;
    private String tombName;
    private String visitorName;
    private String avatar;
    private String content;

    @ApiModelProperty("配图 URL 列表 JSON 字符串，前端解析为数组展示")
    private String imageUrls;

    private String status;
    private String rejectReason;

    @ApiModelProperty("当前用户对该留言所属墓碑的操作权限：admin/chief/member 可操作，null 不可操作")
    private String myRole;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}

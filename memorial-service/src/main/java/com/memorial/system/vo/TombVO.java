package com.memorial.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "墓碑信息")
public class TombVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String photo;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deathday;

    private String biography;
    private String story;
    @ApiModelProperty("墓志铭")
    private String epitaph;
    @ApiModelProperty("是否开放访客互动（留言/献花等）")
    private Boolean visitorActionOpen;
    private Long familyId;
    private String familyName;
    @ApiModelProperty("所处位置")
    private String address;
    private String qrCode;
    @ApiModelProperty("二维码识别标识（用于扫码访问链接）")
    private String qrCodeKey;
    private Integer visitCount;
    private Integer messageCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("当前用户对该墓碑的权限：admin/chief/member，用于前端显隐操作按钮")
    private String myRole;

    @ApiModelProperty("生平事迹列表")
    private List<TombStoryVO> stories;

    @ApiModelProperty("当前登录用户是否已是该墓碑所属家族的成员（同根家族树内），用于纪念页隐藏「申请成为家族成员」提示")
    private Boolean isFamilyMember;
}

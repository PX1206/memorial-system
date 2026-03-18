package com.memorial.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "家族信息")
public class FamilyVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long pid;
    private String type;
    private String parentName;
    private String name;
    private String description;
    private String phone;
    private String address;
    private String founderName;
    private Long chiefId;
    private Integer memberCount;
    private Integer tombCount;

    /** 家族邀请码 */
    private String inviteCode;

    /** 当前用户在该家族的角色：族长/管理员/成员，用于前端控制操作按钮 */
    private String myRole;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private List<FamilyVO> children;
}

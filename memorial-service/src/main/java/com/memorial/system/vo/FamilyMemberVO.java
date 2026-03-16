package com.memorial.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value = "家族成员信息")
public class FamilyMemberVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long familyId;
    private String familyName;
    private String name;
    private String relation;
    private String phone;
    private String role;
    private String avatar;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}

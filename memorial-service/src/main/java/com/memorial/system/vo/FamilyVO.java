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
    private String parentName;
    private String name;
    private String description;
    private String phone;
    private String address;
    private String founderName;
    private Integer memberCount;
    private Integer tombCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private List<FamilyVO> children;
}

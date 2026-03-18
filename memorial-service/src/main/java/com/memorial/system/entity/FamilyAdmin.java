package com.memorial.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 家族管理员：超级管理员（创建人）、管理员（由超级管理员指定）
 * family_id 存储根家族ID（type=家族），管理员拥有对该根家族及所有下级分支的完整权限（递归包含所有家庭/支族）
 */
@Data
@Accessors(chain = true)
@TableName("family_admin")
@ApiModel(value = "FamilyAdmin对象")
public class FamilyAdmin {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("家族ID（type=家族的根）")
    private Long familyId;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("角色：super_admin 超级管理员/admin 管理员")
    private String role;

    private Date createTime;
    private Long createBy;
}

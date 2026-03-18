package com.memorial.system.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel(value = "家族参数")
public class FamilyParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("家族ID（修改时必传）")
    private Long id;

    @ApiModelProperty("上级家族ID，0表示顶级（创建家庭/支族时必填）")
    private Long pid;

    @ApiModelProperty("类型：家族/家庭/支族，创建时必选")
    private String type;

    @ApiModelProperty("族长用户ID（家庭/支族时可指定）")
    private Long chiefId;

    @NotBlank(message = "名称不能为空")
    @ApiModelProperty(value = "家族名称", required = true)
    private String name;

    @ApiModelProperty("家族简介")
    private String description;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("所在地区")
    private String address;
}

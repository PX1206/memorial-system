package com.memorial.system.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel(value = "申请加入家族参数")
public class ApplyJoinFamilyParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "家族ID不能为空")
    @ApiModelProperty(value = "家族ID", required = true)
    private Long familyId;

    @ApiModelProperty("与族长关系")
    private String relation;

    @ApiModelProperty("申请理由")
    private String reason;
}

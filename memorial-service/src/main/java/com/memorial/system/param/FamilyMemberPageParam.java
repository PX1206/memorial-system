package com.memorial.system.param;

import com.memorial.common.pagination.BasePageOrderParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "家族成员分页参数")
public class FamilyMemberPageParam extends BasePageOrderParam {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "家族ID不能为空")
    @ApiModelProperty(value = "家族ID", required = true)
    private Long familyId;

    @ApiModelProperty("姓名关键字")
    private String keyword;
}

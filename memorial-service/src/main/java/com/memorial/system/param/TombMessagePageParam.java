package com.memorial.system.param;

import com.memorial.common.pagination.BasePageOrderParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "留言分页参数")
public class TombMessagePageParam extends BasePageOrderParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("关键字（留言内容/墓碑名）")
    private String keyword;

    @ApiModelProperty("审核状态")
    private String status;

    @ApiModelProperty("墓碑ID")
    private Long tombId;
}

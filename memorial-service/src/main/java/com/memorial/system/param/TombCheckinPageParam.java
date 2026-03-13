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
@ApiModel(value = "打卡记录分页参数")
public class TombCheckinPageParam extends BasePageOrderParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("关键字（墓碑名/访客名）")
    private String keyword;

    @ApiModelProperty("打卡类型")
    private String type;

    @ApiModelProperty("墓碑ID")
    private Long tombId;
}

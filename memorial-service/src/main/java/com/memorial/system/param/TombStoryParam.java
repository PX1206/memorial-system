package com.memorial.system.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel(value = "墓碑事迹参数")
public class TombStoryParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("事迹ID（修改时必传）")
    private Long id;

    @NotNull(message = "墓碑ID不能为空")
    @ApiModelProperty(value = "墓碑ID", required = true)
    private Long tombId;

    @NotBlank(message = "事迹标题不能为空")
    @ApiModelProperty(value = "事迹标题", required = true)
    private String title;

    @NotBlank(message = "事迹内容不能为空")
    @ApiModelProperty(value = "事迹内容（富文本HTML）", required = true)
    private String content;

    @ApiModelProperty("排序（越小越靠前）")
    private Integer sort;
}


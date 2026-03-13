package com.memorial.common.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.memorial.common.constant.CommonConstant;
import com.memorial.common.tool.StringUtil;
import org.slf4j.MDC;

import java.util.UUID;

public class TraceIdPatternConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        String traceId = MDC.get(CommonConstant.REQUEST_ID);
        if (StringUtil.isEmpty(traceId)) {
            traceId = UUID.randomUUID().toString().replaceAll("-", "");
        }
        return traceId;
    }
}

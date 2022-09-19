package com.hepo.c2c.social.govern.mall.config;

import com.hepo.c2c.social.govern.vo.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author linhaibo
 */
@Slf4j
@RestControllerAdvice
public class WebExceptionAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResultObject<String> handleRuntimeException(RuntimeException e) {
        log.error(e.toString(), e);
        return ResultObject.error("服务器异常");
    }


}

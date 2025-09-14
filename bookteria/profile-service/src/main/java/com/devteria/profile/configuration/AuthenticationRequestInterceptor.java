package com.devteria.profile.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class AuthenticationRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        var authorization = servletRequestAttributes.getRequest().getHeader("Authorization");
        log.info("value log header : " + authorization);

        if (StringUtils.hasText(authorization)) {
            template.header("Authorization", authorization);
        }
    }
}

package com.github.blog.configuration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.github.blog.annotation.SessionAttr;

@Service
public class SessionAttrMethodArgumentResolver implements HandlerMethodArgumentResolver {
	final Logger logger = LoggerFactory.getLogger(SessionAttrMethodArgumentResolver.class);

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        if(methodParameter.hasParameterAnnotation(SessionAttr.class))return true;
        else if (methodParameter.getMethodAnnotation(SessionAttr.class) != null)return true;
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        String annoVal = null;

        if(methodParameter.getParameterAnnotation(SessionAttr.class)!=null){
            logger.debug("param anno val::::"+methodParameter.getParameterAnnotation(SessionAttr.class).value());
            annoVal = methodParameter.getParameterAnnotation(SessionAttr.class).value();
        }else if(methodParameter.getMethodAnnotation(SessionAttr.class)!=null){
            logger.debug("method anno val::::"+methodParameter.getMethodAnnotation(SessionAttr.class).value());
            annoVal = methodParameter.getMethodAnnotation(SessionAttr.class)!=null?
                    StringUtils.defaultString(methodParameter.getMethodAnnotation(SessionAttr.class).value()):StringUtils.EMPTY;
        }
        if(annoVal != null)        	
            return nativeWebRequest.getAttribute("annoVal", RequestAttributes.SCOPE_SESSION);
        return null;
    }
   
}

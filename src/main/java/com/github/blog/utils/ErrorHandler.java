package com.github.blog.utils;

import com.alibaba.fastjson.JSONException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.NestedServletException;

@ControllerAdvice
public class ErrorHandler {
	private final Logger log = LoggerFactory.getLogger(ErrorHandler.class);
	
    @ExceptionHandler(value = JSONException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response<Void> errorJsonResponse(JSONException e)
    {
    	log.error("Server error", e);
        return new Response<Void>(ResultCode.INTERVAL_ERROR,"Request body has bad json format");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Response<Void> errorJsonResponse(MissingServletRequestParameterException e)
    {
    	log.error("Server error", e);
        return new Response<Void>(ResultCode.INTERVAL_ERROR,e.getMessage());
    }

    @ExceptionHandler(ConversionFailedException.class)
    @ResponseBody
    public Response<Void> errorJsonResponse(ConversionFailedException e)
    {
    	log.error("Server error", e);
        return new Response<Void>(ResultCode.INTERVAL_ERROR,"Invalid parameter format");
    }

    @ExceptionHandler(TypeMismatchException.class)
    @ResponseBody
    public Response<Void> errorJsonResponse(TypeMismatchException e)
    {
    	log.error("Server error", e);
        return new Response<Void>(ResultCode.INTERVAL_ERROR,"Invalid parameter format");
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response<Void> errorParamResponse(IllegalArgumentException e)
    {
    	log.error("Server error", e);
        return new Response<Void>(ResultCode.INTERVAL_ERROR,e.getMessage());
    }

    @ExceptionHandler(value = NestedServletException.class)
    @ResponseBody
    public Response<Void> errorRequestResponse(NestedServletException e)
    {
        log.error("Server error", e);
        return new Response<Void>(ResultCode.INTERVAL_ERROR,e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response<Void> errorParamResponse(Exception e)
    {
    	log.error("Server error", e);
        return new Response<Void>(ResultCode.INTERVAL_ERROR,"Bad request, please check the input parameters");
    }

    @ExceptionHandler(value = Throwable.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response<Void> errorResponse(Throwable e)
    {
    	log.error("Server error", e);
        return new Response<Void>(ResultCode.INTERVAL_ERROR,"Interval error");
    }
}

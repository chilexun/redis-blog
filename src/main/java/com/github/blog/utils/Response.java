package com.github.blog.utils;

public class Response<T> {
    private int code;
    private String message;
    private T data;
    
    public Response() {
    	this(0, null, null);
    }

    public Response(int code, String message){
        this(code, message, null);
    }

    public Response(T data){
       this(ResultCode.SUCCESS, null, data);
    }

    public Response(int code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

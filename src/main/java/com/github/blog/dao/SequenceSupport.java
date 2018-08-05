package com.github.blog.dao;

public interface SequenceSupport {

    Long nextValue(String sequenceName);
}

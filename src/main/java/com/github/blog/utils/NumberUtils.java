package com.github.blog.utils;

public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils{

	public static int zeroOnNull(Number number) {
		if(number == null)
			return 0;
		return number.intValue();
	}
}

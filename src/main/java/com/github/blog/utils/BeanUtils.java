package com.github.blog.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

public class BeanUtils extends org.apache.commons.beanutils.BeanUtils{

	public static Map<String, Object> beanToMap(Object javaBean) {
        return beanToMap(javaBean, new String[0]);
    }

    public static Map<String, Object> beanToMap(Object javaBean, String... ignoreProp) {
        Map<String, Object> result = new HashMap<String, Object>();
        Set<String> ignoreProps = Sets.newHashSet(ignoreProp);
        Method[] methods = javaBean.getClass().getDeclaredMethods();

        for (Method method : methods) {
            try {
                if (method.getName().startsWith("get")) {
                    String field = method.getName();//getName  getPassword
                    field = field.substring(field.indexOf("get") + 3);//Name  Password
                    field = field.toLowerCase().charAt(0) + field.substring(1);//name password
                    if(!ignoreProps.contains(field)) {
                        Object value = method.invoke(javaBean, (Object[]) null);
                        if (value != null)
                            result.put(field, value);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error occurs where parse bean to map:"+ javaBean.getClass().getName());
            }
        }
        return result;
    }

    public static <T> T mapToBean(Map<Object, Object> propMap, Class<T> clazz){
	    try{
            T result = clazz.newInstance();
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                Class<?>[] cc = method.getParameterTypes();
                if (cc.length != 1)
                    continue;
                if (!methodName.startsWith("set"))
                    continue;
                Object fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
                if (propMap.containsKey(fieldName) && propMap.get(fieldName) != null)
                    method.invoke(result, new Object[]{propMap.get(fieldName)});
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error occurs where parse bean to map:"+ clazz.getName());
        }
    }

}

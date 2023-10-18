package com.hcs.android.common.util;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class JsonUtils {
    public static String toJsonString(Object o){
        //Gson gson = new Gson();
        return JSON.toJSONString(o);
    }

    public static <T> T toObject(String orgData,Class<T> clazz){
        Gson gson = new Gson();
        return gson.fromJson(orgData,clazz);
    }

    private static Type getClassType(Class raw,Class sub){
        return new ParameterizedTypeImpl(raw,new Class[]{sub});
    }
    private static Type getClassType(Class raw,Type sub){
        return new ParameterizedTypeImpl(raw,new Type[]{sub});
    }
    private static Type getClassType(Class[] clazzs){
        if(clazzs == null || clazzs.length < 2){
            return null;
        }
        int len = clazzs.length;
        Type type = getClassType(clazzs[len - 2],clazzs[len - 1]);
        for(int i = len - 3; i >= 0; i--){
            type = getClassType(clazzs[i],type);
        }
        return type;
    }
    public static <T> T toObject(String orgData, Class ... clazzs) {
        Gson gson = new Gson();
        Type type = getClassType(clazzs);
        if(type == null){
            type = new TypeToken<T>(){}.getType();
        }
        return gson.fromJson(orgData, type);
    }
}

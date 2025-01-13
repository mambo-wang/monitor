package com.virtual.cloud.om.sdk.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 * 序列化工具类
 * Created by w17423 on 2018/7/9.
 */
@Slf4j
public class SerializeUtils {


	private static final ObjectMapper OBJECT_MAPPER;

	static {
		OBJECT_MAPPER = new ObjectMapper();
		OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		OBJECT_MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		//OBJECT_MAPPER.enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
	}

	public static String toJson(Object obj) {
		try {
			return OBJECT_MAPPER.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			log.error("write to json error", e);
		}
		return "";
	}

	public static ObjectMapper getObjectMapper() {
		return OBJECT_MAPPER;
	}

	public static <T> T json2Object(String jsonString, Class<T> c) {
		if (jsonString == null || "".equals(jsonString.trim())) {
			return null;
		} else {
			try {
				return c.cast(OBJECT_MAPPER.readValue(jsonString, c));
			} catch (Throwable t) {
				log.error("json error:", t);
			}
		}
		return null;
	}

	public static <T> List<T> json2Array(String jsonString, Class<T> c) {
		if (jsonString == null || "".equals(jsonString.trim())) {
			return null;
		} else {
			try {
				JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructArrayType(c);
				return Arrays.asList(getObjectMapper().readValue(jsonString, javaType));
			} catch (Throwable t) {
				log.error("json error:", t);
			}
		}
		return null;
	}
}

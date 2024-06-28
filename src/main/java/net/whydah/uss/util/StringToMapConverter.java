package net.whydah.uss.util;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StringToMapConverter implements AttributeConverter<Map<String, String>, String> {
   
  @Override
  public String convertToDatabaseColumn(Map<String, String> map) {
    if(map == null) return null;
    try {
		return EntityUtils.object_mapToJsonString(map);
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	}
  }

  @Override
  public Map<String, String> convertToEntityAttribute(String value) {
    if(value == null) return null;
    try {
    	MapType type = TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, String.class);
		return EntityUtils.mapFromJson(value, type);
	} catch (Exception e) {
		e.printStackTrace();
		return new HashMap<String, String>();
	}
  }
}
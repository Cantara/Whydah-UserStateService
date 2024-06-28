package net.whydah.uss.util;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StringToGenericListConverter<T> implements AttributeConverter<List<T>, String> {

	
	
	  @Override
	  public String convertToDatabaseColumn(List<T> list) {
		  if(list == null) return null;
		  try {
			  return EntityUtils.object_mapToJsonString(list);
		  } catch (Exception e) {
			  e.printStackTrace();
			  return null;
		  }
	  }

	  @Override
	  public List<T> convertToEntityAttribute(String value) {
		  if(value == null) return null;
		    try {
				return EntityUtils.mapFromJson(value, new TypeReference<List<T>>() {});
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
	  }
	  
}
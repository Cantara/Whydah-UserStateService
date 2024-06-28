package net.whydah.uss.util;

import lombok.Data;

import java.nio.charset.StandardCharsets;


@Data
public class Response {

	
	private int responseCode;
	private byte[] data = null;
	
	public String getContent() {
		if(data!=null) {
			return new String(data, StandardCharsets.UTF_8);
		}
		return null;
	}
}
package net.whydah.uss.model;

import lombok.Data;

@Data
public class UASUserIdentity {

	private String personRef;
	private String uid;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private String cellPhone;
	private String uri;
}

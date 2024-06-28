package net.whydah.uss.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/*
		{
		"rows":"170",
		"currentPage":2,
		"pageSize":250,
		"totalItems":420,
		"result":
		[
		{
		 "personRef":"PersonRef0",
		 "uid":"d3bea892-5811-4eb0-8058-06d2f2fd3864",
		 "username":"Test-User-abcaa1d86",
		 "firstName":"Mt Test",
		 "lastName":"Testesen",
		 "email":"6f3575840@getwhydah.com",
		 "cellPhone":"4791122754",
		 "uri":"http://ip-172-31-55-84.ec2.internal:9995/uib/useradmin/users/d3bea892-5811-4eb0-8058-06d2f2fd3864\/"
		}
		]
		}
*/
@Data
@NoArgsConstructor
public class UASUserQueryResult {

	String rows;
	int currentPage;
	int pageSize;
	int totalItems;
	List<UASUserIdentity> result = new ArrayList<>();

}

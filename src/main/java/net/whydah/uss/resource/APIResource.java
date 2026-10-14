package net.whydah.uss.resource;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import net.whydah.sso.user.types.UserIdentity;
import net.whydah.uss.entity.LoginUserStatusEntity;
import net.whydah.uss.service.APIService;
import net.whydah.uss.settings.AppSettings;
import net.whydah.uss.util.EntityUtils;
import no.cantara.config.ApplicationProperties;

@Path("/api")
public class APIResource {


	private static final Logger log = LoggerFactory.getLogger(APIResource.class);

	private APIService service;
	
	public APIResource(ApplicationProperties p, APIService _service) {
		this.service = _service;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response toHealth(@Context HttpServletRequest request) throws IOException {
		return Response.seeOther(URI.create("/health")).build();
	}
	
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{accesstoken}/update")
	public Response updateLogonTimeFromSTS(@Context HttpServletRequest request, 
			@PathParam("accesstoken") String accesstoken,
			@RequestBody List<UserIdentity> users) throws Exception {
		log.info("updateLogonTimeFromSTS/{}/update called", accesstoken);
		if(accesstoken!=null && accesstoken.equalsIgnoreCase(AppSettings.ACCESS_TOKEN)) {
			service.updateUserLogonTimeFromSTS(users);
			return Response.ok("OK").build();
		} else {
			return Response.status(Status.FORBIDDEN).build();
		}
		
	}
	
	/**
	 * Login status for one user, for UserAdminWebApp's GDPR access (innsyn) report: when this service first saw the
	 * user (import or first login after it went live) and the last login STS reported.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{accesstoken}/user/{uid}")
	public Response getLoginStatus(@Context HttpServletRequest request,
			@PathParam("accesstoken") String accesstoken,
			@PathParam("uid") String uid) throws Exception {
		if (accesstoken == null || !accesstoken.equalsIgnoreCase(AppSettings.ACCESS_TOKEN)) {
			return Response.status(Status.FORBIDDEN).build();
		}
		log.info("getLoginStatus called for uid {}", uid);
		Optional<LoginUserStatusEntity> status = service.getRepositoryLoginUserStatus().findById(uid);
		if (status.isEmpty()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		Map<String, String> body = new LinkedHashMap<>();
		body.put("uid", uid);
		body.put("creationTime", status.get().getCreationTime() != null ? status.get().getCreationTime().toString() : null);
		body.put("lastLoginTime", status.get().getLastLoginTime() != null ? status.get().getLastLoginTime().toString() : null);
		return Response.ok(EntityUtils.object_mapToJsonString(body)).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{accesstoken}/delete/{uid}")
	public Response deleteUserFromUAS(@Context HttpServletRequest request, 
			@PathParam("accesstoken") String accesstoken,
			@PathParam("uid") String uid) throws Exception {
		if(accesstoken!=null && accesstoken.equalsIgnoreCase(AppSettings.ACCESS_TOKEN)) {
			log.info("deleteUserFromUAS/{}/delete/{} called", accesstoken, uid);
			service.deleteUserLogonTimeFromUAS(uid);
			return Response.ok("OK").build();
		} else {
			return Response.status(Status.FORBIDDEN).build();
		}
		
	}
	

}

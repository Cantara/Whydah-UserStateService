package net.whydah.uss.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.whydah.uss.entity.AppStateEntity;
import net.whydah.uss.service.APIService;
import net.whydah.uss.settings.AppSettings;
import net.whydah.uss.util.EntityUtils;
import net.whydah.uss.util.FluentHashMap;
import net.whydah.uss.util.FreeMarkerHelper;
import no.cantara.config.ApplicationProperties;
import no.cantara.stingray.security.application.StingraySecurityOverride;

@Path("/")
public class UIResource {

    private static final Logger log = LoggerFactory.getLogger(UIResource.class);
    private String publicResourcePath = "public";
   
    private APIService service;
    
    public UIResource(APIService service) {  
    	this.service = service;
    }

    @GET
    @Path("/{path: (?!api|health).*}")
    @StingraySecurityOverride
    public Response getInformation(@Context ServletContext context, 
    		@PathParam("path") final String path, 
    		@Context HttpServletRequest request) throws IOException {
    	String _path = path; 
    	Map<String, Object> model = new HashMap<>();
    
    	boolean foundAccessToken = request.getParameterMap().entrySet().stream().anyMatch(x -> x.getKey().toLowerCase().equals("accesstoken") && x.getValue()[0].equalsIgnoreCase(AppSettings.ACCESS_TOKEN));
    	
    	if(foundAccessToken) {
    		model.put("accesstoken", AppSettings.ACCESS_TOKEN);
    	}
    	
    	if(path.equals("")) {
    		
    		return toHome(foundAccessToken, model);
			
    	} else {
    		InputStream resource = getClass().getResourceAsStream(String.format("/" + publicResourcePath + "/%s", _path));

    		String mt = context.getMimeType(_path);
    		return Objects.isNull(resource)
    				? Response.status(500).build()
    						: Response.ok().entity(resource)
    						.header("Access-Control-Allow-Origin", "*")
    						.header("Content-Type", mt)

    						.build();
    	}
    }
    

	private Response toHome(boolean accesstokenFound, Map<String, Object> model) {
		
    	if(accesstokenFound) {
           	AppStateEntity en = service.getRepositoryAppState().get();
    		model.put("accesstoken", AppSettings.ACCESS_TOKEN);
        	model.put("app_state", en);
        	model.put("number_of_recent_logins", String.valueOf(service.getNumberOfRecentLogins()) + " logins in " + AppSettings.RECENT_LOGON_PERIOD_IN_DAY + " days" );
        	model.put("templateparams", EntityUtils.object_mapToJsonString(
        			FluentHashMap.map("username", "9999999")
        			.with("firstname", "John")
        			.with("lastname", "Kenedy")
        			));
        	if (AppSettings.MAIL_TEMPLATE != null && AppSettings.MAIL_TEMPLATE.length() > 4) {
        		model.put("templatefilename", AppSettings.MAIL_TEMPLATE);
        	} else {
        		model.put("message", AppSettings.MAIL_DEFAULT_MESSAGE);
        	}
 
    	}
		String body = FreeMarkerHelper.createBody("/home.ftl", model);
		
		return Response.ok().entity(new ByteArrayInputStream(body.getBytes())).build();
	}

	@POST
	@Path("/")
	public Response sendTestingMail(@Context HttpServletRequest request,
			@FormParam("email") String email,
			@FormParam("message") String message,
			@FormParam("templatefilename") String templatefilename,
			@FormParam("templateparams") String templateparams,
			@FormParam("accesstoken") String accesstoken
			) throws IOException {
		
		boolean foundAccessToken = accesstoken!=null && accesstoken.equalsIgnoreCase(AppSettings.ACCESS_TOKEN);
		Map<String, Object> model = new HashMap<>();
	     	
		Map<String, String> params = EntityUtils.mapFromJson(templateparams, Map.class);
		boolean sent = false;
		if(templatefilename!=null) {
			sent = service.getModuleWhydahClient().sendScheduledMail(email,
					params.containsKey("username")?params.get("username"):"99999999",
					params.containsKey("firstname")?params.get("firstname"):"John",
					params.containsKey("lastname")?params.get("lastname"):"Kenedy",
				    0);
		} else {
			String template = "<html><head></head><body><p>${msg}</p></body></html>";
			template = template.replace("${msg}", message)
					.replace("${lastname}", params.containsKey("lastname")?params.get("lastname"):"Kenedy")
					.replace("${firstname}", params.containsKey("firstname")?params.get("firstname"):"John")
					.replace("${username}", params.containsKey("username")?params.get("username"):"99999999");
			
			sent = service.getModuleWhydahClient().sendScheduledMailWithAMessage(email, template, 0);
		}
		
		model.put("response_test_mail_sending", sent? "Send succeeded" : "Send failed");
	
		return toHome(foundAccessToken, model);
	
	}
	
}

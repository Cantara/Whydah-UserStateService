package net.whydah.uss;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.whydah.uss.provider.AppExceptionProvider;
import net.whydah.uss.resource.APIResource;
import net.whydah.uss.resource.UIResource;
import net.whydah.uss.service.APIService;
import net.whydah.uss.service.APIServiceImpl;
import net.whydah.uss.service.FlywayService;
import net.whydah.uss.util.LogFilter;
import no.cantara.config.ApplicationProperties;
import no.cantara.stingray.application.AbstractStingrayApplication;
import no.cantara.stingray.application.StingrayLogging;

public class MainApplication extends AbstractStingrayApplication<MainApplication> {

    private static final Logger log = LoggerFactory.getLogger(MainApplication.class);

    static {
        StingrayLogging.init();
       
    }
    
	public static MainApplication instance;

    public static void main(String[] args) {
        try {
        	
        	MainApplication application = initApp();
            APIService apiService = application.get(APIService.class);
            apiService.initialize();
        
            
        } catch (Throwable t) {
            log.error("During application initialization", t);
        }
    }

	public static MainApplication initApp() {
		ApplicationFactory factory = new ApplicationFactory();
		ApplicationProperties config = factory.conventions(ApplicationProperties.builder())
		        .classpathPropertiesFile("hibernate.properties")
		        .filesystemPropertiesFile("hibernate.properties")
		        .filesystemPropertiesFile("application.properties")
				.build();
		MainApplication application = factory.create(config);
		MainApplication.instance = application;
		application.init().start();
		return application;
	}

    public MainApplication(ApplicationProperties config) {
        super("uss", readMetaInfMavenPomVersion("net.whydah.uss", "UserStateService"), config);
    }

    protected MainApplication(String applicationAlias, String version, ApplicationProperties config) {
        super(applicationAlias, version, config);
    }

    @Override
    protected void doInit() {
    	
        initBuiltinDefaults();
        init(FlywayService.class, () -> new FlywayService(config()));
        init(APIService.class, () -> new APIServiceImpl());
        initAndRegisterJaxRsWsComponent(MultiPartFeature.class, MultiPartFeature::new);
        initAndRegisterJaxRsWsComponent(LogFilter.class, LogFilter::new);
        initAndRegisterJaxRsWsComponent(AppExceptionProvider.class, () -> new AppExceptionProvider(config()));
        initAndRegisterJaxRsWsComponent(APIResource.class, this::createAPIResource);
        initAndRegisterJaxRsWsComponent(UIResource.class, this::createUIResource);
       
        
    }
    
    private APIResource createAPIResource() {
        APIService api = get(APIService.class);
        return new APIResource(config(), api);
    }
    
    private UIResource createUIResource() {
        return new UIResource(get(APIService.class));
    }
    

}

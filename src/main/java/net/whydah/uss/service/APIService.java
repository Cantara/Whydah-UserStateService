package net.whydah.uss.service;

import java.util.List;

import net.whydah.sso.user.types.UserIdentity;
import net.whydah.uss.repository.AppStateRepository;
import net.whydah.uss.repository.LoginUserStatusRepository;
import net.whydah.uss.repository.OldUserRepository;
import net.whydah.uss.service.module.DetectOldUserModule;
import net.whydah.uss.service.module.DetectOldUserModuleImpl;
import net.whydah.uss.service.module.ImportUserModule;
import net.whydah.uss.service.module.ImportUserModuleImpl;
import net.whydah.uss.service.module.NotifyOldUserModule;
import net.whydah.uss.service.module.NotifyOldUserModuleImpl;
import net.whydah.uss.service.module.RemoveOldUserModule;
import net.whydah.uss.service.module.RemoveOldUserModuleImpl;
import net.whydah.uss.service.module.WhydahClientModule;


public abstract class APIService {
	
	boolean testMode = false;
	
	LoginUserStatusRepository loginUserStatusRepository = new LoginUserStatusRepository();
	AppStateRepository appStateRepository = new AppStateRepository();
	OldUserRepository oldUserRepository = new OldUserRepository();
	
	ImportUserModule importUserModule = new ImportUserModuleImpl(this);
	DetectOldUserModule detectOldUserModule = new DetectOldUserModuleImpl(this);
	RemoveOldUserModule removeOldUserModule = new RemoveOldUserModuleImpl(this);
	WhydahClientModule whydahClientModule = new WhydahClientModule(this);
	NotifyOldUserModule notifyOldUserModule = new NotifyOldUserModuleImpl(this);
	
	public LoginUserStatusRepository getRepositoryLoginUserStatus() {
		return loginUserStatusRepository;
	}
	
	public AppStateRepository getRepositoryAppState() {
		return appStateRepository;
	}
	
	public OldUserRepository getRepositoryOldUser() {
		return oldUserRepository;
	}
	
	
	//module
	
	
	public ImportUserModule getModuleImportUser() {
		
		return importUserModule;
	}
	
	public DetectOldUserModule getModuleDetectOldUser() {
		return detectOldUserModule;
	}
	
	public WhydahClientModule getModuleWhydahClient() {
		return whydahClientModule;
	}
	
	public RemoveOldUserModule getModuleRemoveOldUser() {
		return removeOldUserModule; 
	}
	
	public NotifyOldUserModule getNotifyOldUserModule() {
		return notifyOldUserModule;
	}
	
	
	//functions
	
	public abstract void initialize();
	
	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}
	
	public boolean isTestMode() {
		return this.testMode;
	}
	
	public abstract void updateUserLogonTimeFromSTS(List<UserIdentity> useridentity);

	public abstract long getNumberOfRecentLogins();


}

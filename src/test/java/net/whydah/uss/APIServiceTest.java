package net.whydah.uss;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.whydah.uss.entity.LoginUserStatusEntity;
import net.whydah.uss.entity.OldUserEntity;
import net.whydah.uss.repository.AppStateRepository;
import net.whydah.uss.repository.LoginUserStatusRepository;
import net.whydah.uss.service.APIService;
import net.whydah.uss.service.APIServiceImpl;
import net.whydah.uss.service.module.DetectOldUserModule;
import net.whydah.uss.service.module.DetectOldUserModuleImpl;
import net.whydah.uss.service.module.WhydahClientModule;
import net.whydah.uss.util.EntityUtils;
import net.whydah.uss.util.FluentHashMap;

public class APIServiceTest {
	
	private static final Logger log = LoggerFactory.getLogger(APIServiceTest.class);
	
	static APIService service;
	
	@BeforeAll
	public static void beforeClass() {
		
		MainApplication app = MainApplication.initApp();
		
		/* Use the testMode to control instead 
		 * 
		//override for testing purpose
		
		app.init(APIService.class, () -> new APIServiceImpl() {
			public net.whydah.uss.service.module.WhydahClientModule getModuleWhydahClient() {
				return new WhydahClientModule(this) {
					@Override
					public boolean removeUser(String id) {
						//assume we call UAS to remove the user successfully
						return true;
					}
				};
			};
		});
		*/
		service = app.get(APIService.class);
		
		service.setTestMode(true);
		
	}
	
	@AfterAll
	public static void afterClass() {
		service.getRepositoryLoginUserStatus().close();
		service.getRepositoryAppState().close();
		service.getRepositoryOldUser().close();
	}

	@BeforeEach
	public void before() {
		service.getRepositoryLoginUserStatus().deleteAll();
		service.getRepositoryAppState().deleteAll();
		service.getRepositoryOldUser().deleteAll();
	}
	
	@Test
	public void testImportUser_module() {
		String logPrefix = "testImportUser_module() - ";
		assertTrue(service.getRepositoryLoginUserStatus().count()==0);
		assertTrue(service.getRepositoryAppState().get().getStats_total_users_imported() == 0);
		
		service.getModuleImportUser().importUsers();
		
		assertTrue(service.getRepositoryLoginUserStatus().count()>0);
		assertTrue(service.getRepositoryAppState().get().getStats_total_users_imported() > 0);
		log.info(logPrefix + "App state {}", service.getRepositoryAppState().get());
	}

	@Test
	public void testDetectOldUser_module() {
		String logPrefix = "testDetectOldUser_module() - ";
		assertTrue(service.getRepositoryOldUser().count()==0);
		assertTrue(service.getRepositoryAppState().get().getStats_number_of_old_users_detected() == 0);
		
		LoginUserStatusEntity person = new LoginUserStatusEntity();
		person.setId(UUID.randomUUID().toString());
		person.setFirstName("Huy");
		person.setLastName("Do");
		person.setCellPhone("99999999");
		person.setEmail("misterhuydo@gmail.com");
		person.setLastLoginTime(LocalDateTime.now().minusMonths(24));
		person.setPersonRef(UUID.randomUUID().toString());
		person.setUsername("useradmin");
		//CREATE
		person = service.getRepositoryLoginUserStatus().insert(person);
		
		service.getModuleDetectOldUser().detectOldUsers();
		
		assertTrue(service.getRepositoryOldUser().count()>0);
		assertTrue(service.getRepositoryAppState().get().getStats_number_of_old_users_detected() > 0);
		log.info(logPrefix + "App state {}", service.getRepositoryAppState().get());
	}
	
	@Disabled("Sending email is not working in whydah cantara")
	@Test
	public void testNotifyOldUser_module() {
		String logPrefix = "testNotifyOldUser_module() - ";
		assertTrue(service.getRepositoryOldUser().count()==0);
		assertTrue(service.getRepositoryAppState().get().getStats_number_of_mails_sent() == 0);
		

		OldUserEntity person = new OldUserEntity();
		person.setId(UUID.randomUUID().toString());
		person.setFirstName("Huy");
		person.setLastName("Do");
		person.setCellPhone("99999999");
		person.setEmail("misterhuydo@gmail.com");
		person.setLastLoginTime(LocalDateTime.now().minusMonths(23));
		person.setPersonRef(UUID.randomUUID().toString());
		person.setUsername("useradmin");
		person.setNotified(false);
		//CREATE
		person = service.getRepositoryOldUser().insert(person);
		
		service.getNotifyOldUserModule().checkAndSendNotifications();
		
		assertTrue(service.getRepositoryOldUser().count()>0);
		assertTrue(service.getRepositoryAppState().get().getStats_number_of_mails_sent() > 0);
		log.info(logPrefix + "App state {}", service.getRepositoryAppState().get());
	}
	
	@Test
	public void testRemoveOldUser_module() {
		
		assertTrue(service.getRepositoryOldUser().count()==0);
		assertTrue(service.getRepositoryAppState().get().getStats_number_of_old_users_detected() == 0);
		
		OldUserEntity person = new OldUserEntity();
		person.setId(UUID.randomUUID().toString());
		person.setFirstName("Huy");
		person.setLastName("Do");
		person.setCellPhone("99999999");
		person.setEmail("misterhuydo@gmail.com");
		person.setLastLoginTime(LocalDateTime.now().minusMonths(25));
		person.setPersonRef(UUID.randomUUID().toString());
		person.setUsername("useradmin");
		person.setNotified(true);
		//CREATE
		person = service.getRepositoryOldUser().insert(person);
		

		assertTrue(service.getRepositoryOldUser().count()>0);
		
		assertTrue(service.getRepositoryAppState().get().getStats_number_of_old_users_removed() == 0);
		
		service.getModuleRemoveOldUser().checkOldUsersAndRemove();
		
		assertTrue(service.getRepositoryOldUser().count() == 0);
		
		assertTrue(service.getRepositoryAppState().get().getStats_number_of_old_users_removed() > 0);
		
		
	}
	
	@Test
	public void testCRUD_LoginUserStatusEntity() {
		LoginUserStatusEntity person = new LoginUserStatusEntity();
		person.setId(UUID.randomUUID().toString());
		person.setFirstName("Huy");
		person.setLastName("Do");
		person.setCellPhone("99999999");
		person.setEmail("misterhuydo@gmail.com");
		person.setLastLoginTime(LocalDateTime.now());
		person.setPersonRef(UUID.randomUUID().toString());
		person.setUsername("useradmin");
		//CREATE
		person = service.getRepositoryLoginUserStatus().insert(person);
		String id = person.getId();
		assertTrue(person !=null);
		assertTrue(id !=null);
		log.debug("person {}", person);
		
		
		//READ
		person = service.getRepositoryLoginUserStatus().findById(id).orElse(null);
		assertTrue(person !=null);
		List<LoginUserStatusEntity> list = service.getRepositoryLoginUserStatus().findAll();
		assertTrue(list.size() == 1);
		long count = service.getRepositoryLoginUserStatus().count();
		assertTrue(count == 1);
		count = service.getRepositoryLoginUserStatus().countWithQuery("select count(*) from LoginUserStatusEntity t where t.cellPhone =:cellphone", 
				FluentHashMap.map("cellphone", "99999999"));
		assertTrue(count == 1);
		
		List list_by_email = service.getRepositoryLoginUserStatus().findByEmail("misterhuydo@gmail.com");
		assertTrue(list_by_email.size() == 1);
		//UPDATE
		person.setPersonRef("NEW PERSON REF");
		person = service.getRepositoryLoginUserStatus().update(person);
		assertTrue(person.getPersonRef().equals("NEW PERSON REF"));
		//DELETE
		service.getRepositoryLoginUserStatus().delete(person);
		count = service.getRepositoryLoginUserStatus().count();
		assertTrue(count == 0);
		
	}

	@Test
	public void testBulkInsertAndUpdate_LoginUserStatusEntity() {
		List<LoginUserStatusEntity> users = new ArrayList<LoginUserStatusEntity>();
		for(int i = 0; i<1000; i++) {
			LoginUserStatusEntity person = new LoginUserStatusEntity();
			person.setId(UUID.randomUUID().toString());
			person.setFirstName("Huy" + i);
			person.setLastName("Do" + i);
			person.setCellPhone("99999999");
			person.setEmail(i + "misterhuydo@gmail.com");
			person.setLastLoginTime(LocalDateTime.now());
			person.setPersonRef(UUID.randomUUID().toString());
			person.setUsername("useradmin" + i);
			users.add(person);
		}
		users = service.getRepositoryLoginUserStatus().insertAll(users);
		log.debug(EntityUtils.object_mapToJsonString(users));
		
		for(LoginUserStatusEntity u : users) {
			u.setFirstName("updated-" + u.getFirstName());
			u.setLastName("updated-" + u.getLastName());
		}
		users = service.getRepositoryLoginUserStatus().updateAll(users);
		assertTrue(EntityUtils.object_mapToJsonString(users).contains("updated-"));
		log.debug(EntityUtils.object_mapToJsonString(users));
		
		
	}


}
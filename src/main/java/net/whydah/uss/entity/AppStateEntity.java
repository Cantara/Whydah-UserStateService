package net.whydah.uss.entity;

import java.io.Serializable;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="APP_STATE")
public class AppStateEntity implements Serializable  {

	@Id
	//@UuidGenerator
	protected String id;
	
	//trace the last page index from fetching the users from UAS
	//new CommandListUsersWithPagination(...)
    protected int importuser_page_index = 1;
    
    //total users imported in the local DB
    protected int stats_total_users_imported;
    
    //trace number of old users
    protected int stats_number_of_old_users_detected;
    
    //trace number of notification mails sent;
    protected int stats_number_of_mails_sent;
    
    //trace number of deleted users
    protected int stats_number_of_old_users_removed;
    
    //trace number of users comming back
    protected int stats_number_of_old_users_comming_back;
    

}

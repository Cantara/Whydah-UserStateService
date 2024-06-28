package net.whydah.uss.repository;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import net.whydah.uss.entity.LoginUserStatusEntity;
import net.whydah.uss.util.FluentHashMap;
import net.whydah.uss.util.repository.CRUDRepository;
import net.whydah.uss.util.repository.CountQueryHelper;

public class LoginUserStatusRepository extends CRUDRepository<LoginUserStatusEntity, String> {

	@Override
	public Class<LoginUserStatusEntity> getClassType() {
		return LoginUserStatusEntity.class;
	}
	
	public List<LoginUserStatusEntity> findByEmail(String email) {
		return findWithQuery("SELECT a FROM LoginUserStatusEntity a WHERE a.email = :email", 
				FluentHashMap.map("email", email));
	}
	
	public List<LoginUserStatusEntity> detectOldUsers(LocalDateTime olderThanThisDate) {
		return findWithQuery("SELECT a FROM LoginUserStatusEntity a WHERE a.lastLoginTime <= :date AND a.oldLogonDetected = false", 
				FluentHashMap.map("date", olderThanThisDate));
	}
	
	public long getNumberOfRecentLogins(LocalDateTime newerThanThisDate) {
		return countWithQuery("SELECT count(*) FROM LoginUserStatusEntity a WHERE a.lastLoginTime > :date", 
				FluentHashMap.map("date", newerThanThisDate));
	}
	
	
	
	
	
}
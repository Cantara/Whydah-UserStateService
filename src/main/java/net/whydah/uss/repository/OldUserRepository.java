package net.whydah.uss.repository;

import java.time.LocalDateTime;
import java.util.List;

import net.whydah.uss.entity.LoginUserStatusEntity;
import net.whydah.uss.entity.OldUserEntity;
import net.whydah.uss.util.FluentHashMap;
import net.whydah.uss.util.repository.CRUDRepository;

public class OldUserRepository extends CRUDRepository<OldUserEntity, String> {

	@Override
	public Class<OldUserEntity> getClassType() {
		return OldUserEntity.class;
	}
	

	public List<OldUserEntity> findOldUsersForDeleteOperation(LocalDateTime olderThanThisDate) {
		return findWithQuery("SELECT a FROM OldUserEntity a WHERE a.notified IS true AND a.lastLoginTime <= :oldDate", 
				FluentHashMap.map("oldDate", olderThanThisDate));
	}
	
	public List<OldUserEntity> findOldUsersForNotifyingOperation() {
		return findWithQuery("SELECT a FROM OldUserEntity a WHERE a.notified IS false");
	}
	
	
	

}

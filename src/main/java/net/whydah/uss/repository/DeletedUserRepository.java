package net.whydah.uss.repository;

import java.time.LocalDateTime;
import java.util.List;

import net.whydah.uss.entity.DeletedUserEntity;
import net.whydah.uss.entity.LoginUserStatusEntity;
import net.whydah.uss.entity.OldUserEntity;
import net.whydah.uss.util.FluentHashMap;
import net.whydah.uss.util.repository.CRUDRepository;

public class DeletedUserRepository extends CRUDRepository<DeletedUserEntity, String> {

	@Override
	public Class<DeletedUserEntity> getClassType() {
		return DeletedUserEntity.class;
	}
	
	
	public long getNumberOfRecentDeletedUsers(LocalDateTime newerThanThisDate) {
		return countWithQuery("SELECT count(*) FROM DeletedUserEntity a WHERE a.creationTime > :fromDate", 
				FluentHashMap.map("fromDate", newerThanThisDate));
	}

	
	
	

}

package net.whydah.uss.service.module;

import java.util.List;

import net.whydah.uss.entity.LoginUserStatusEntity;
import net.whydah.uss.entity.OldUserEntity;

public interface DetectOldUserModule {

	List<OldUserEntity> detectOldUsers();
	
}

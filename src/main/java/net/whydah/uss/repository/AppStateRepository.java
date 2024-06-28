package net.whydah.uss.repository;

import java.util.List;

import net.whydah.uss.entity.AppStateEntity;
import net.whydah.uss.util.repository.CRUDRepository;

public class AppStateRepository extends CRUDRepository<AppStateEntity, String> {

	@Override
	public Class<AppStateEntity> getClassType() {
		return AppStateEntity.class;
	}
	
	public AppStateEntity get() {
		List<AppStateEntity> list = super.findAll();
		if(list.size()==0) {
			AppStateEntity en = new AppStateEntity();
			en.setImportuser_page_index(1);
			en = super.insert(en);
			return en;
		}
		return (AppStateEntity) list.get(0);
	}
	
	
}
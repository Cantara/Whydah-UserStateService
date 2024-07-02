package net.whydah.uss.repository;

import java.util.List;

import net.whydah.uss.entity.AppStateEntity;
import net.whydah.uss.util.repository.CRUDRepository;

public class AppStateRepository extends CRUDRepository<AppStateEntity, String> {

	//THIS TABLE HAS ONLY ONE ROW WITH THIS ID
	final String id = "470c02cc-2615-4004-8335-1f4565c9feac";
	
	@Override
	public Class<AppStateEntity> getClassType() {
		return AppStateEntity.class;
	}
	
	public AppStateEntity get() {
		AppStateEntity entry = findById(id).orElse(null);
		if(entry==null) {
			AppStateEntity en = new AppStateEntity();
			en.setId(id);
			en.setImportuser_page_index(1);
			en = super.insert(en);
			return en;
		}
		return entry;
	}
	
	
}
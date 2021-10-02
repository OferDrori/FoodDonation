package dts.logic;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import dts.data.IdGeneratorEntity;

@Component
public class GenerateDatabaseIds {

	private IdGeneratorEntityDao idGeneratorEntityDao;

	public GenerateDatabaseIds(IdGeneratorEntityDao idGeneratorEntityDao) {
		this.idGeneratorEntityDao = idGeneratorEntityDao;
	}

	@Transactional
	public Long getNextId() {

		IdGeneratorEntity idGeneratorEntity = new IdGeneratorEntity();
		idGeneratorEntity = this.idGeneratorEntityDao.save(idGeneratorEntity);

		Long newId = idGeneratorEntity.getId();
		this.idGeneratorEntityDao.deleteById(newId);

		return newId;
	}
}

package dts.logic;


import org.springframework.data.repository.CrudRepository;

import dts.data.IdGeneratorEntity;

public interface IdGeneratorEntityDao extends CrudRepository<IdGeneratorEntity, Long>{

}

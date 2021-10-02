package dts.logic;

import org.springframework.data.repository.PagingAndSortingRepository;

import dts.data.UserEntity;

public interface UserDao extends PagingAndSortingRepository<UserEntity, String> {
	
}

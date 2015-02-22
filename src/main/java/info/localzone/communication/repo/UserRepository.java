package info.localzone.communication.repo;

import info.localzone.communication.model.security.User;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User, Long>{
	public User findOneByEmail(String email);
}

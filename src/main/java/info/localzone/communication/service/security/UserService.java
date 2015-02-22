package info.localzone.communication.service.security;

import info.localzone.communication.model.security.User;
import info.localzone.communication.model.security.UserCreateForm;

import java.util.Collection;

import com.google.common.base.Optional;

public interface UserService {

   User getUserById(long id);

   User getUserByEmail(String email);

    Collection<User> getAllUsers();

    User create(UserCreateForm form);

}
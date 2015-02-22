package info.localzone.communication.service.security;

import info.localzone.communication.model.security.CurrentUser;
import info.localzone.communication.model.security.Role;

import org.springframework.stereotype.Service;

@Service
public class CurrentUserServiceImpl implements CurrentUserService {

    @Override
    public boolean canAccessUser(CurrentUser currentUser, Long userId) {
        return currentUser != null
                && (currentUser.getRole() == Role.ADMIN || currentUser.getId().equals(userId));
    }

}
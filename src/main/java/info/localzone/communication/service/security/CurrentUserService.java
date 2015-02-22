package info.localzone.communication.service.security;

import info.localzone.communication.model.security.CurrentUser;

public interface CurrentUserService {
    boolean canAccessUser(CurrentUser currentUser, Long userId);
}

package info.localzone.communication.service.security;

import info.localzone.communication.model.security.CurrentUser;
import info.localzone.communication.model.security.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Autowired
    public CurrentUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public CurrentUser loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.getUserByEmail(email);
        if (user == null)
                throw new UsernameNotFoundException(String.format("User with email=%s was not found", email));
                
        return new CurrentUser(user);
    }
}

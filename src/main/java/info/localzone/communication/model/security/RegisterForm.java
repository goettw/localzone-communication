package info.localzone.communication.model.security;


public class RegisterForm extends UserForm{
	public RegisterForm() {
		setRole(Role.USER);
	}

	@Override
	public void setRole(Role role) {
	
		super.setRole(Role.USER);
	}
}

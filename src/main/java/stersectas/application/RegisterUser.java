package stersectas.application;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import stersectas.validation.PasswordsMatch;
import stersectas.validation.UsernameAvailable;

@PasswordsMatch
public class RegisterUser {

	@Size(min = 3, max = 30)
	@UsernameAvailable
	private String username;

	@Email
	private String email;

	@Size(min = 8, max = 100)
	private String password;

	@Size(min = 8, max = 100)
	private String passwordConfirmation;

	public RegisterUser() {
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getPasswordConfirmation() {
		return passwordConfirmation;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	}
}

package stersectas.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import stersectas.BaseIT;
import stersectas.domain.User;
import stersectas.repositories.UserRepository;
import stersectas.stub.EmailServiceStub;
import stersectas.stub.TimeTravellingClock;

public class UserServiceIT extends BaseIT {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TimeTravellingClock clock;

	@Autowired
	private EmailServiceStub emailServiceStub;

	@Test
	@Transactional
	public void registerUser() {
		RegisterUser registerUser = new RegisterUser();
		registerUser.setUsername("test-user");
		registerUser.setEmail("test@test.com");
		registerUser.setPassword("12345");

		userService.registerNewUser(registerUser);
		SimpleMailMessage email = emailServiceStub.getLastEmail();

		User testUser = userRepository.findByUsername("test-user").get();
		assertEquals("test-user", testUser.getUsername());
		assertNotEquals("12345", testUser.getPassword());
		assertTrue(new BCryptPasswordEncoder().matches("12345", testUser.getPassword()));
		assertFalse(testUser.isEnabled());
		assertNotNull(email);
		assertEquals("test@test.com", email.getTo()[0]);
	}

	@Test
	@Transactional
	public void confirmRegistrationImmediately() {
		userService.registerNewUser(createRegisterUser("test-user"));

		boolean confirmation = userService.confirmEmailVerification("test-token");
		User user = userRepository.findByUsername("test-user").get();

		assertTrue(confirmation);
		assertTrue(user.isEnabled());
	}

	private RegisterUser createRegisterUser(String username) {
		RegisterUser registerUser = new RegisterUser();
		registerUser.setUsername(username);
		registerUser.setEmail("test@test.com");
		registerUser.setPassword("12345");
		return registerUser;
	}

	@Test
	@Transactional
	public void confirmRegistrationAnHourLater() {
		LocalDateTime dateTime = LocalDateTime.parse("2015-09-28T14:42:21");
		clock.travelThroughTimeTo(dateTime);
		userService.registerNewUser(createRegisterUser("test-user"));

		clock.travelThroughTimeTo(dateTime.plusHours(1));
		boolean confirmation = userService.confirmEmailVerification("test-token");
		User user = userRepository.findByUsername("test-user").get();

		assertTrue(confirmation);
		assertTrue(user.isEnabled());
	}

	@Test
	@Transactional
	public void registrationExpired() {
		LocalDateTime dateTime = LocalDateTime.parse("2015-09-28T14:42:21");
		clock.travelThroughTimeTo(dateTime);
		userService.registerNewUser(createRegisterUser("test-user"));

		clock.travelThroughTimeTo(dateTime.plusDays(1).plusSeconds(1));
		boolean confirmation = userService.confirmEmailVerification("test-token");
		User user = userRepository.findByUsername("test-user").get();

		assertFalse(confirmation);
		assertFalse(user.isEnabled());
	}

	@Test
	@Transactional
	public void createInitialUser() {
		userService.initializeUsers();
		User initialUser = userRepository.findByUsername("initial").get();
		assertEquals("initial", initialUser.getUsername());
		assertTrue(initialUser.isEnabled());
	}

	@Test
	@Transactional
	public void alreadyInitialisedUsers() {
		registerAndEnableAUser();
		assertFalse(userRepository.findByUsername("initial").isPresent());

		userService.initializeUsers();

		assertFalse(userRepository.findByUsername("initial").isPresent());
	}

	private void registerAndEnableAUser() {
		RegisterUser registerUser = new RegisterUser();
		registerUser.setUsername("test-user");
		registerUser.setEmail("test@test.com");
		registerUser.setPassword("12345");
		userService.registerNewUser(registerUser);
		User user = userRepository.findByUsername("test-user").get();
		user.enable();
		userRepository.save(user);
	}

	@Test
	@Transactional
	public void initialiseWithDisabledInitialUser() {
		registerInitialUser();
		assertFalse(userRepository.findByUsername("initial").get().isEnabled());

		userService.initializeUsers();

		User initialUser = userRepository.findByUsername("initial").get();
		assertEquals("initial", initialUser.getUsername());
		assertTrue(initialUser.isEnabled());
	}

	private void registerInitialUser() {
		RegisterUser registerUser = new RegisterUser();
		registerUser.setUsername("initial");
		registerUser.setEmail("test@test.com");
		registerUser.setPassword("password");
		userService.registerNewUser(registerUser);
	}

	@Test
	@Transactional
	public void promoteUser() {
		registerAndEnableAUser();
		userService.promoteUserToAdministrator("test-user");

		User user = userRepository.findByUsername("test-user").get();
		assertTrue(user.isAdministrator());
	}

}

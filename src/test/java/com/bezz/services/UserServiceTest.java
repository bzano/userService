package com.bezz.services;

import java.util.List;

import org.dozer.DozerBeanMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.bezz.beans.User;
import com.bezz.exceptions.UserAlreadyExistException;
import com.bezz.exceptions.UserNotFoundException;
import com.bezz.repo.IUserRepository;
import com.bezz.repo.impl.UserRepository;
import com.bezz.services.impl.IpV4Service;
import com.bezz.services.impl.SubNetIpV4Service;
import com.bezz.services.impl.UserService;

@RunWith(SpringRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class UserServiceTest {
	
	@TestConfiguration
	static class UserServiceTestConfiguration {
		@Bean
		public UserService userServiceBean(){
			return new UserService();
		}
		
		@Bean
		public IUserRepository userRepositoryBean(){
			return new UserRepository();
		}
		
		@Bean
		public DozerBeanMapper dozerBeanMapperBean(){
			return new DozerBeanMapper();
		}
		
		@Bean
		public ISubNetIpV4Service subNetIpV4Service(){
			return new SubNetIpV4Service();
		}
		
		@Bean
		public IIpV4Service ipServiceBean(){
			return new IpV4Service();
		}
	}
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IUserRepository userRepository;
	
	@Before
	public void setUp(){
		User user = new User(2, "titi", "toto", "127.0.0.1", 20, "local");
		userRepository.save(user);
	}
	
	@Test
	public void add_should_add_new_user() throws UserAlreadyExistException {
		// GIVEN
		User user = new User(1, "titi", "toto", "127.0.0.1", 20, "local");
		// WHEN
		User newUser = userService.addUser(user);
		// THEN
		Assert.assertEquals(user, newUser);
	}
	
	@Test(expected = UserAlreadyExistException.class)
	public void add_should_throw_already_exist_exception_if_user_exist() throws UserAlreadyExistException {
		// GIVEN
		User user = new User(2, "titi", "toto", "127.0.0.1", 20, "local");
		// WHEN
		userService.addUser(user);
		// THEN Assert Exception
	}
	
	public void update_should_update_an_existing_user() throws UserNotFoundException {
		// GIVEN
		User updatedUser = new User(2, "tata", "fifi", "127.0.0.1", 30, "local");
		//WHEN
		User newUser = userService.updateUser(updatedUser);
		//THEN
		Assert.assertEquals(newUser, updatedUser);
	}
	
	@Test(expected = UserNotFoundException.class)
	public void update_should_throw_user_not_found_exception_if_user_does_not_exist() throws UserNotFoundException {
		// GIVEN
		User updatedUser = new User(44, "tata", "fifi", "127.0.0.1", 30, "local");
		//WHEN
		userService.updateUser(updatedUser);
		//THEN Assert Exception
	}
	
	@Test
	public void getAll_should_return_all_users(){
		// GIVEN
		// WHEN
		List<User> allUsers = userService.getAll();
		// THEN
		Assert.assertEquals(userRepository.findAll(), allUsers);
	}
	
	@Test
	public void getById_should_return_a_specific_user() throws UserNotFoundException {
		// GIVEN
		Integer userId = new Integer(2);
		// WHEN
		User user = userService.getById(userId);
		// THEN
		Assert.assertEquals(userId, user.getId());
	}
	
	@Test(expected = UserNotFoundException.class)
	public void getById_should_throw_user_not_found_exception_if_user_does_not_exist() throws UserNotFoundException {
		// GIVEN
		Integer userId = new Integer(55);
		// WHEN
		userService.getById(userId);
		// THEN Assert Exception
	}
}

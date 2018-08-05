package com.bezz.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bezz.beans.User;
import com.bezz.dtos.requests.UserRequest;
import com.bezz.dtos.responses.UserResponse;
import com.bezz.exceptions.UserAlreadyExistException;
import com.bezz.exceptions.UserNotFoundException;
import com.bezz.services.IUserService;

@RestController
public class UserController {
	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
	private static final String REQUEST_ALL_USERS_LOG = "HTTP Request all users";
	private static final String REQUEST_USER_BY_ID_LOG = "HTTP Request user by id {}";
	private static final String SAVE_NEW_USER_LOG = "HTTP Save new user";
	private static final String UPDATE_USER_LOG = "HTTP Update user {}";
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private DozerBeanMapper mapper;
	
	@RequestMapping(value="/users", method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<UserResponse>> getAllUsers() {
		if(LOG.isDebugEnabled()){
			LOG.debug(REQUEST_ALL_USERS_LOG);
		}
		List<User> users = userService.getAll();
		List<UserResponse> usersResponse = new ArrayList<UserResponse>(users.size());
		mapper.map(users, usersResponse);
		return ResponseEntity.ok(usersResponse);
	}
	
	@RequestMapping(value="/users/{id}", method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<UserResponse> getUserById(@PathVariable("id") Integer id) throws UserNotFoundException {
		if(LOG.isDebugEnabled()){
			LOG.debug(REQUEST_USER_BY_ID_LOG, id);
		}
		User user = userService.getById(id);
		UserResponse userResponse = mapper.map(user, UserResponse.class);
		return ResponseEntity.ok(userResponse);
	}
	
	@RequestMapping(value="/users", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<UserResponse> addUser(@RequestBody @Valid UserRequest userRequest, BindingResult bindingResult) throws BindException, UserAlreadyExistException {
		if(LOG.isDebugEnabled()){
			LOG.debug(SAVE_NEW_USER_LOG);
		}
		if(bindingResult.hasErrors()){
			throw new BindException(bindingResult);
		}
		
		User user = mapper.map(userRequest, User.class);
		User userResult = userService.addUser(user);
		UserResponse userResponse = mapper.map(userResult, UserResponse.class);
		return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
	}
	
	@RequestMapping(value="/users", method=RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<UserResponse> updateUser(@RequestBody @Valid UserRequest userRequest, BindingResult bindingResult) throws BindException, UserNotFoundException {
		if(LOG.isDebugEnabled()){
			LOG.debug(UPDATE_USER_LOG, userRequest.getId());
		}
		if(bindingResult.hasErrors()){
			throw new BindException(bindingResult);
		}
		
		User user = mapper.map(userRequest, User.class);
		User userResult = userService.updateUser(user);
		UserResponse userResponse = mapper.map(userResult, UserResponse.class);
		return ResponseEntity.ok(userResponse);
	}
}

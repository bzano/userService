package com.bezz.services.impl;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.message.MessageFormatMessage;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bezz.beans.User;
import com.bezz.exceptions.IpNotFoundException;
import com.bezz.exceptions.UserAlreadyExistException;
import com.bezz.exceptions.UserNotFoundException;
import com.bezz.repo.IUserRepository;
import com.bezz.services.IIpV4Service;
import com.bezz.services.IUserService;

@Service
public class UserService implements IUserService {
	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
	
	private static final String USER_NOT_FOUND = "User with id {0} not found";
	private static final String USER_ALREADY_EXIST = "User with id {0} already exist";
	@Autowired
	private IUserRepository userRepository;
	@Autowired
	private IIpV4Service ipService;
	@Autowired
	private DozerBeanMapper mapper;
	
	public List<User> getAll() {
		return userRepository.findAll();
	}
	
	public User getById(Integer id) throws UserNotFoundException {
		Optional<User> optionalUser = userRepository.findById(id);
		if(!optionalUser.isPresent()){
			String errorMessage = new MessageFormatMessage(USER_NOT_FOUND, id).getFormattedMessage();
			throw new UserNotFoundException(errorMessage);
		}
		return optionalUser.get();
	}
	
	public User addUser(User user) throws UserAlreadyExistException {
		Integer userId = user.getId();
		Optional<User> optionalUser = userRepository.findById(userId);
		if(optionalUser.isPresent()){
			String errorMessage = new MessageFormatMessage(USER_ALREADY_EXIST, userId).getFormattedMessage();
			throw new UserAlreadyExistException(errorMessage);
		}
		updateUserCountry(user, user.getIpv4());
		return userRepository.save(user);
	}
	
	public User updateUser(User user) throws UserNotFoundException {
		Integer userId = user.getId();
		Optional<User> optionalUser = userRepository.findById(userId);
		if(!optionalUser.isPresent()){
			String errorMessage = new MessageFormatMessage(USER_NOT_FOUND, userId).getFormattedMessage();
			throw new UserNotFoundException(errorMessage);
		}
		User actualUser = optionalUser.get();
		mapper.map(user, actualUser);
		
		updateUserCountry(actualUser, user.getIpv4());
		return actualUser;
	}
	
	private void updateUserCountry(User user, String ipv4){
		try {
			String country = ipService.findIpCountry(ipv4);
			user.setCountry(country);
		} catch (IpNotFoundException e) {
			LOG.warn(e.getMessage());
		}
	}
}

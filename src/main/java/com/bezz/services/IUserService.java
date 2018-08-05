package com.bezz.services;

import java.util.List;

import com.bezz.beans.User;
import com.bezz.exceptions.UserAlreadyExistException;
import com.bezz.exceptions.UserNotFoundException;

public interface IUserService {
	List<User> getAll();
	User getById(Integer id) throws UserNotFoundException;
	User addUser(User user) throws UserAlreadyExistException;
	User updateUser(User user) throws UserNotFoundException;
}

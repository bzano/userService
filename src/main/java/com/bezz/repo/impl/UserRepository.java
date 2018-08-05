package com.bezz.repo.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.bezz.beans.User;
import com.bezz.repo.IUserRepository;

@Repository
public class UserRepository implements IUserRepository {
	public List<User> users = new ArrayList<User>();

	@Override
	public List<User> findAll(){
		return Collections.unmodifiableList(users);
	}

	@Override
	public Optional<User> findById(Integer id){
		return users.stream().filter(user -> id.equals(user.getId())).findFirst();
	}
	
	@Override
	public User save(User user){
		Optional<User> optionalUser = findById(user.getId());
		if(optionalUser.isPresent()){
			
		}else{
			users.add(user);
		}
		return user;
	}
}

package com.bezz.repo;

import java.util.List;
import java.util.Optional;

import com.bezz.beans.User;

public interface IUserRepository {
	public List<User> findAll();
	public Optional<User> findById(Integer id);
	public User save(User user);
}

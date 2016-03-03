package com.test.dao;

import java.io.Serializable;

import com.test.model.User;

public interface UserDao {

	      /**
	      * 保存用户
	      * @param user
	      * @return
	      */
	     Serializable save(User user); 
}

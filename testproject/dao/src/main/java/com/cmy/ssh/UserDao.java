package main.java.com.cmy.ssh;

import java.io.Serializable;

public interface UserDao {

	/**
	 * 保存用户
	 * @param user
	 * @return
	 */
	Serializable save(User user);
}

package main.java.com.cmy.ssh;


import java.io.Serializable;


public interface UserService {
	/**
	 * 测试方法
	 */
	void test();

	/**
	 * 保存用户
	 * 
	 * @param user
	 * @return
	 */
	Serializable save(User user);
}

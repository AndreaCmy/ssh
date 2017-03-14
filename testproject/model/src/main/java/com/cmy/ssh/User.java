package main.java.com.cmy.ssh;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "USER", schema = "test")
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String password;
	
	public User() {
	     }
	
	 // Property accessors
	     @Id
	     @Column(name = "ID", unique = true, nullable = false, length = 16)
	     public String getId() {
	         return this.id;
	     }
	 
	     public void setId(String id) {
	         this.id = id;
	     }
	 
	     @Column(name = "NAME",nullable = false, length = 100)
	     public String getName() {
	         return this.name;
	     }
	 
	     public void setName(String name) {
	         this.name = name;
	     }
	     @Column(name = "password",nullable = false, length = 100)
 		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	 
	
	
}
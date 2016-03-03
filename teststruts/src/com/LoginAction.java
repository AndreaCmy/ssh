package com;


import com.opensymphony.xwork2.ActionSupport;



public class LoginAction extends ActionSupport{
 private String username;
 private String userpass;
 
 public String execute(){
  if("daniel".equals(username)&&"abcde".equals(userpass))
   return SUCCESS;
  else
   return ERROR;
 }
 
 public String getUsername() {
  return username;
 }
 public void setUsername(String username) {
  this.username = username;
 }
 public String getUserpass() {
  return userpass;
 }
 public void setUserpass(String userpass) {
  this.userpass = userpass;
 }
}

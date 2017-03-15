<%@ page language="java" import="java.util.*" pageEncoding="GB2312"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head><title>登录界面</title></head>
  <body>
    <form action="<%=request.getContextPath() %>/saveUser!saveUser.action">
       用户名：<input name="username"><br>
       密 码：<input type="password" name="userpass"><br>
      <input type="submit" value="提 交">
      <input type="reset"  value="取 消">
    </form>
  </body>
</html>
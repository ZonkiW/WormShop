package com.buaa.service;

import java.sql.SQLException;

import com.buaa.dao.LoginDao;

public class LoginService {

	public boolean loginHandle(String username, String password) {
		// TODO Auto-generated method stub
		LoginDao dao = new LoginDao();
		int isLoginSuccess=0;
		try {
			isLoginSuccess = dao.loginHandle(username,password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isLoginSuccess>0?true:false;
	}

	public boolean loginHandleRegister(String username, String password) {
		// TODO Auto-generated method stub
		LoginDao dao = new LoginDao();
		int isRegister=0;
		try {
			isRegister = dao.loginHandleRegister(username,password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isRegister>0?true:false;
	}
	
}

package com.buaa.service;

import java.sql.SQLException;

import com.buaa.dao.UserDao;
import com.buaa.domain.User;

public class UserService {

		UserDao dao=new UserDao();
		public boolean register(User user){
			int row=0;
			try {
				row=dao.register(user);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return row>0?true:false;			
		}
		public boolean active(String activeCode) {
			// TODO Auto-generated method stub
			int row=0;
			try {
				row = dao.active(activeCode);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return row>0?true:false;
		}
		public boolean checkUserName(String userName) {
			// TODO Auto-generated method stub
			UserDao userDao = new UserDao();
			long isExist=0;
			try {
				isExist=userDao.checkUserName(userName);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return isExist>0?true:false;
		}
}

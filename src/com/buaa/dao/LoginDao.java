package com.buaa.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.buaa.utils.DataSourceUtils;

public class LoginDao {

	public int loginHandle(String username, String password) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String SQL="select count(*) from user where username=?&&password=?";
	    long query = (long) runner.query(SQL, new ScalarHandler(),username,password);
	    int count=new Long(query).intValue();
		return count;
	}

	public int loginHandleRegister(String username, String password) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String SQL="select count(*) from user where username=?&&password=?&&state=?";
	    long query = (long) runner.query(SQL, new ScalarHandler(),username,password,1);
	    int count=new Long(query).intValue();
		return count;
	}

}

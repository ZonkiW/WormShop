package com.buaa.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.buaa.domain.User;
import com.buaa.utils.DataSourceUtils;

public class UserDao {
    //返回影响行数
	    public int register(User user) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String SQL="insert into user values(?,?,?,?,?,?,?,?,?,?)";
		int update=runner.update(SQL, user.getUid(),user.getUsername(),user.getPassword(),
				user.getName(),user.getEmail(),user.getTelephone(),
				user.getBirthday(),user.getSex(),user.getState(),user.getCode());
		return update;
	    }

		public int active(String activeCode) throws SQLException {
			// TODO Auto-generated method stub
			QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
			String SQL="update user set state=? where code=? ";
			int update=runner.update(SQL, 1,activeCode);
			return update;
		}

		public long checkUserName(String userName) throws SQLException {
			// TODO Auto-generated method stub
			QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
			String SQL="select count(*) from user where username=?";
			long query = (long) runner.query(SQL, new ScalarHandler(), userName);
			return query;
		}

}

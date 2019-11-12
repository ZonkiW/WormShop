package com.buaa.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.buaa.domain.Category;
import com.buaa.utils.DataSourceUtils;

public class CategoryDao {

	public List<Category> getCategoryList() throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String SQL="select * from category";
		return runner.query(SQL, new BeanListHandler<Category>(Category.class));
	}

}

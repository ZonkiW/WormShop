package com.buaa.service;

import java.sql.SQLException;
import java.util.List;

import com.buaa.dao.CategoryDao;
import com.buaa.domain.Category;

public class CategoryService {
	
	public List<Category> getCategoryList() {
		// TODO Auto-generated method stub
		CategoryDao dao = new CategoryDao();
		List<Category> categoryList=null;
		try {
			categoryList= dao.getCategoryList();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return categoryList;
	}

}

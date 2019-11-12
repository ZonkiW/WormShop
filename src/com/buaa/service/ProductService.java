package com.buaa.service;

import java.sql.SQLException;
import java.util.List;

import com.buaa.dao.ProductDao;
import com.buaa.domain.MyOrderItem;
import com.buaa.domain.Order;
import com.buaa.domain.PageBean;
import com.buaa.domain.Product;
import com.buaa.utils.DataSourceUtils;

public class ProductService {
	ProductDao dao=new ProductDao();
	public List<Product> findHotProduct() {
		// TODO Auto-generated method stub
		List<Product> hotProductList=null;
		try {
			hotProductList= dao.findHotProduct();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hotProductList;
	}

	public List<Product> findNewProduct() {
		// TODO Auto-generated method stub
		List<Product> newProductList=null;
		try {
			newProductList= dao.findNewProduct();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newProductList;
	}

	public PageBean pageProductList(String cid, int intTargetPage) {
		// TODO Auto-generated method stub
		//封装一个PageBean并返回给Web
		PageBean<Product> pageBean = new PageBean<Product>();
		ProductDao dao = new ProductDao();
		
		int currentPage=intTargetPage;
		int currentCount=12;
		//封装当前页
		pageBean.setCurrentPage(currentPage);
		//封装显示条数
		pageBean.setCurrentCount(currentCount);
		//封装总条数
		int totalCount=0;
		try {
			totalCount = dao.getCount(cid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pageBean.setTotalCount(totalCount);
		//封装总页数
		int totalPage=(int) Math.ceil(1.0*totalCount/currentCount);
		pageBean.setTotalPage(totalPage);
		//封装当前页显示的数据
		int index=(currentPage-1)*currentCount;
		List<Product> pageProductList=null;
		try {
			pageProductList = dao.pageProductList(cid,index,currentCount);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pageBean.setList(pageProductList);
		
		return pageBean;
	}

	public Product productInfo(String pid) {
		// TODO Auto-generated method stub
		ProductDao dao = new ProductDao();
		Product productInfo=null;
		try {
			productInfo= dao.productInfo(pid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return productInfo;
	}

	public void submitOrder(Order order) {
		// TODO Auto-generated method stub
		ProductDao productDao = new ProductDao();
		//事务
		try {
			DataSourceUtils.startTransaction();
			productDao.addOrders(order);
			productDao.addOrderItem(order);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			try {
				DataSourceUtils.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally{
			try {
				DataSourceUtils.commitAndRelease();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public void updateOrder(Order order) {
		// TODO Auto-generated method stub
		ProductDao productDao = new ProductDao();
		try {
			productDao.updateOrder(order);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateOrderState(String r6_Order) {
		// TODO Auto-generated method stub
		ProductDao dao = new ProductDao();
		try {
			dao.updateOrderState(r6_Order);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<Order> findMyBaseOrder(String user) {
		// TODO Auto-generated method stub
		ProductDao dao = new ProductDao();
		List<Order> myBaseOrder = null;
		try {
			myBaseOrder = dao.findMyBaseOrder(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return myBaseOrder;
	}

	public List<MyOrderItem> findMyOrderItem(String user, String oid) {
		// TODO Auto-generated method stub
		ProductDao dao = new ProductDao();
		List<MyOrderItem> myOrderItems = null;
		try {
			myOrderItems = dao.findMyOrderItem(user,oid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return myOrderItems;
	}

}

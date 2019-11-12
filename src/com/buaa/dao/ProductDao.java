package com.buaa.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.buaa.domain.MyOrderItem;
import com.buaa.domain.Order;
import com.buaa.domain.OrderItem;
import com.buaa.domain.Product;
import com.buaa.utils.DataSourceUtils;

public class ProductDao {

	public List<Product> findHotProduct() throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String SQL="select * from product where is_hot=? limit ?,?";
		return runner.query(SQL, new BeanListHandler<Product>(Product.class), 1,0,9);
	}

	public List<Product> findNewProduct() throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String SQL="select * from product order by pdate desc limit ?,?";//Ä¬ÈÏÉýÐò
		return runner.query(SQL, new BeanListHandler<Product>(Product.class),0,9);
	}

	public int getCount(String cid) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String SQL="select count(*) from product where cid=?";
	    long query = (long) runner.query(SQL, new ScalarHandler(),cid);
	    int count=new Long(query).intValue();
		return count;
	}

	public List<Product> pageProductList(String cid, int index, int currentCount) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String SQL="select * from product where cid=? limit ?,?";
	    return runner.query(SQL, new BeanListHandler<Product>(Product.class),cid,index,currentCount);
		
	}

	public Product productInfo(String pid) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String SQL="select * from product where pid=?";
		Product productInfo=runner.query(SQL, new BeanHandler<Product>(Product.class),pid);
	    return productInfo;
	}

	public void addOrders(Order order) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner = new QueryRunner();
		String SQL="insert into orders values(?,?,?,?,?,?,?,?)";
		Connection conn = DataSourceUtils.getConnection();
//		System.out.println("2222");
		runner.update(conn, SQL, order.getOid(),order.getOrdertime(),order.getTotal()
				,order.getState(),order.getAddress(),order.getName(),order.getTelephone(),
				order.getUser());
	}

	public void addOrderItem(Order order) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner = new QueryRunner();
		String SQL="insert into orderitem values(?,?,?,?,?)";
		Connection conn = DataSourceUtils.getConnection();
		for(OrderItem orderItem:order.getOrderItems()){
			runner.update(conn, SQL, orderItem.getItemid(),orderItem.getCount(),
					orderItem.getSubtotal(),orderItem.getProduct().getPid(),order.getOid());			
		}		
	}

	public void updateOrder(Order order) throws SQLException {
		// TODO Auto-generated method stub
		
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String SQL="update orders set address=?,name=?,telephone=? where oid=?";
	    runner.update(SQL, order.getAddress(),order.getName(),order.getTelephone(),order.getOid());	
	}

	public void updateOrderState(String r6_Order) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String SQL="update orders set state=? where oid=?";
	    runner.update(SQL,1,r6_Order);	
	}

	public List<Order> findMyBaseOrder(String user) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String SQL="select * from orders where user=?";
	    return runner.query(SQL, new BeanListHandler<Order>(Order.class),user);
	}

	public List<MyOrderItem> findMyOrderItem(String user, String oid) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String SQL="SELECT p.pimage,p.pname,p.shop_price,om.count,om.subtotal "
				+ "FROM product p,orders od,orderitem om WHERE od.oid=om.oid AND "
				+ "om.pid=p.pid AND od.user=? AND "
						+ "od.oid=?";
	    return runner.query(SQL, new BeanListHandler<MyOrderItem>(MyOrderItem.class),user,oid);
	}

}

package com.buaa.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;

import com.buaa.domain.Cart;
import com.buaa.domain.CartItem;
import com.buaa.domain.Category;
import com.buaa.domain.MyOrder;
import com.buaa.domain.MyOrderItem;
import com.buaa.domain.Order;
import com.buaa.domain.OrderItem;
import com.buaa.domain.PageBean;
import com.buaa.domain.Product;
import com.buaa.service.CategoryService;
import com.buaa.service.ProductService;
import com.buaa.utils.CommonsUtils;
import com.buaa.utils.JedisPoolUtils;
import com.buaa.utils.PaymentUtil;
import com.google.gson.Gson;

import redis.clients.jedis.Jedis;

/**
 * Servlet implementation class ProductServlet
 */
public class ProductServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	//模块功能
	//--------------------------------------------我的订单功能------------------------------------------------
	public void myOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//判断是否登录
		String user=(String) this.getServletContext().getAttribute("username");
		if(user==null){
			
			response.sendRedirect(request.getContextPath()+"/login.jsp");
			return;
		}
		//创建订单对象封装数据
		List<MyOrder> myOrders=new ArrayList<MyOrder>();
		//查询用户所有order，返回list<order>，初步封装
		ProductService service = new ProductService();
		List<Order> myBaseOrder=service.findMyBaseOrder(user);
		for(Order order:myBaseOrder){
			MyOrder myOrder = new MyOrder();
			myOrder.setOid(order.getOid());
			myOrders.add(myOrder);
		}
		//封装myOrderItem
		for(MyOrder myOrder:myOrders){
			
			List<MyOrderItem> myOrderItems=service.findMyOrderItem(user,myOrder.getOid());
			myOrder.setMyOrderItems(myOrderItems);
						
		}
		//重定向显示
		request.setAttribute("myOrders", myOrders);
		request.getRequestDispatcher("/order_list.jsp").forward(request, response);
	}
	
	//--------------------------------------------确认订单功能------------------------------------------------
	public void confirmOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//更新收货人信息、在线支付、更改支付状态
		Map<String, String[]> map = request.getParameterMap();
		Order order = new Order();
		try {
			BeanUtils.populate(order, map);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ProductService service = new ProductService();
		service.updateOrder(order);
		
		//银行支付
	/*	String pd_FrpId = request.getParameter("pd_FrpId");
		switch (pd_FrpId) {
		case "ICBC-NET-B2C"://工商
			
			break;
		case "BOC-NET-B2C"://中行
			
			break;
		case "ABC-NET-B2C"://农行
			
			break;
		case "BOCO-NET-B2C"://交行
			
			break;
		case "PINGANBANK-NET"://平安银行
			
			break;
		case "CCB-NET-B2C"://建行
			
			break;
		case "CEB-NET-B2C"://光大银行
			
			break;
		case "CMBCHINA-NET-B2C"://招行
			
			break;

		default:
			break;
		}
		*/
		// 获得 支付必须基本数据
		String orderid = request.getParameter("oid");
		String money = "0.01";
		// 银行
		String pd_FrpId = request.getParameter("pd_FrpId");

		// 发给支付公司需要哪些数据
		String p0_Cmd = "Buy";
		String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString("p1_MerId");
		String p2_Order = orderid;
		String p3_Amt = money;
		String p4_Cur = "CNY";
		String p5_Pid = "";
		String p6_Pcat = "";
		String p7_Pdesc = "";
		// 支付成功回调地址 ---- 第三方支付公司会访问、用户访问
		// 第三方支付可以访问网址
		String p8_Url = ResourceBundle.getBundle("merchantInfo").getString("callback");
		String p9_SAF = "";
		String pa_MP = "";
		String pr_NeedResponse = "1";
		// 加密hmac 需要密钥
		String keyValue = ResourceBundle.getBundle("merchantInfo").getString(
				"keyValue");
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt,
				p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
				pd_FrpId, pr_NeedResponse, keyValue);
		
		
		String url = "https://www.yeepay.com/app-merchant-proxy/node?pd_FrpId="+pd_FrpId+
			"&p0_Cmd="+p0_Cmd+
			"&p1_MerId="+p1_MerId+
			"&p2_Order="+p2_Order+
			"&p3_Amt="+p3_Amt+
			"&p4_Cur="+p4_Cur+
			"&p5_Pid="+p5_Pid+
			"&p6_Pcat="+p6_Pcat+
			"&p7_Pdesc="+p7_Pdesc+
			"&p8_Url="+p8_Url+
			"&p9_SAF="+p9_SAF+
			"&pa_MP="+pa_MP+
			"&pr_NeedResponse="+pr_NeedResponse+
			"&hmac="+hmac;

		//重定向到第三方支付平台
		response.sendRedirect(url);
	}
	
	//--------------------------------------------提交订单功能------------------------------------------------
	public void submitOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		//封装order,传给service层
		Order order = new Order();
		//判断是否登录
		if(this.getServletContext().getAttribute("username")==null){
			
			response.sendRedirect(request.getContextPath()+"/login.jsp");
			
			return;
		}
		//封装private String oid;
		String oid=CommonsUtils.getUUID();
		order.setOid(oid);
		//封装private String ordertime;Date转String;
//		Date date = (Date) Calendar.getInstance().getTime();
		order.setOrdertime(new SimpleDateFormat("yyy-MM-dd hh:mm:ss").format(new Date()));
		//封装private double total;
		Cart cart=(Cart)session.getAttribute("cart");
		order.setTotal(cart.getTotal());
		//封装private int state;
		order.setState(0);
		//封装private String address;
		order.setAddress(null);
		//封装private String name;
		order.setName(null);
		//封装private String telephone;
		order.setTelephone(null);
		//封装private User user;
		String user=(String) this.getServletContext().getAttribute("username");
		order.setUser(user);
		//封装orderItem
		for(Map.Entry<String, CartItem> entry:cart.getCartItems().entrySet()){
			
			OrderItem orderItem = new OrderItem();
			CartItem cartItem = entry.getValue();
			
			orderItem.setItemid(CommonsUtils.getUUID());
			orderItem.setCount(cartItem.getBuyNum());
			orderItem.setSubtotal(cartItem.getSubtotal());
			orderItem.setProduct(cartItem.getProduct());
			orderItem.setOrder(order);
			
			order.getOrderItems().add(orderItem);
		}
		
		ProductService service = new ProductService();
		service.submitOrder(order);
//		System.out.println("111111111111");
		
		session.setAttribute("order", order);
		response.sendRedirect(request.getContextPath()+"/order_info.jsp");
	}

	//--------------------------------------------删除购物车所有商品功能------------------------------------------------
	public void delCartAllProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.setAttribute("cart",null);
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	
	
	//--------------------------------------------删除购物车商品功能------------------------------------------------
	public void delCartProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String pid = request.getParameter("pid");
		HttpSession session = request.getSession();
		Cart cart = (Cart) session.getAttribute("cart");
		
		cart.setTotal(cart.getTotal()-cart.getCartItems().get(pid).getSubtotal());
		cart.getCartItems().remove(pid);
		session.setAttribute("cart",cart);
		
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	
	//--------------------------------------------加入购物车功能------------------------------------------------
	public void sendToCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获得要添加的商品数据
		String pid=request.getParameter("pid");
		int buyNum=Integer.parseInt(request.getParameter("buyNum"));
		//获得product		
		ProductService service = new ProductService();
		Product product = service.productInfo(pid);
		//获得小计
		double subtotal=product.getShop_price()*buyNum;

		//向session中放Cart,先获取看有没有车，没有就创建
		HttpSession session = request.getSession();
		Cart cart = (Cart) session.getAttribute("cart");
		if(cart==null){
			cart = new Cart();
		}
		//封装CartItem
		CartItem cartItem = new CartItem();
		
		if(cart.getCartItems().containsKey(product.getPid())){
			buyNum+=cart.getCartItems().get(product.getPid()).getBuyNum();
		}
		
		cartItem.setBuyNum(buyNum);
		cartItem.setProduct(product);
		cartItem.setSubtotal(subtotal);
		//封装购物车
		cart.getCartItems().put(product.getPid(), cartItem);
		cart.setTotal(cart.getTotal()+cartItem.getSubtotal());
		session.setAttribute("cart",cart);
		
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	
	//--------------------------------------------分类功能-----------------------------------------------------
	public void Category(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CategoryService service = new CategoryService();
		//缓存数据
		//获得Jedis对象连接数据库
		Jedis jedis = JedisPoolUtils.getJedis();
		String categoryListJson = jedis.get("categoryListJson");
		
		//先判断是否为空
		if(categoryListJson==null){
			System.out.println("缓存中没有数据！");
			List<Category> categoryList=service.getCategoryList();
			//转json
			Gson gson = new Gson();
			categoryListJson = gson.toJson(categoryList);
			jedis.set("categoryListJson",categoryListJson) ;	
		}
				
		response.setContentType("text/html; charset=UTF-8");
		//写json
		response.getWriter().write(categoryListJson);
	}
	//--------------------------------------------首页功能------------------------------------------------------
	public void Index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductService service = new ProductService();
		//获得热门商品
		List<Product> hotProductList=service.findHotProduct();
		request.setAttribute("hotProductList", hotProductList);
		//获得最新商品
		List<Product> newProductList=service.findNewProduct();
		request.setAttribute("newProductList", newProductList);
		//登录信息
		//request.setAttribute("username", this.getServletContext().getAttribute("username"));
		//request.setAttribute("isLoginSuccess", this.getServletContext().getAttribute("isLoginSuccess"));
		//转发到index.jsp
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}
	//-------------------------------------------分页功能----------------------------------------------------
	public void PageProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cid = request.getParameter("cid");
		String targetPage = request.getParameter("targetPage");
		
		int intTargetPage=Integer.parseInt(targetPage);
		
		ProductService service = new ProductService();
		
		PageBean pageBean=service.pageProductList(cid,intTargetPage);
		
		request.setAttribute("pageBean", pageBean);
		request.setAttribute("cid", cid);
		
		//浏览记录处理
		List<Product> arrayList = new ArrayList<Product>();
		//解析cookie
		Cookie[] cookies = request.getCookies();
		if(cookies!=null){
			for(Cookie cookie:cookies){
				if("pids".equals(cookie.getName())){
					String pids = cookie.getValue();
					String[] split = pids.split("-");
					for(String pid:split){
						//System.out.println(pid);
						Product product = service.productInfo(pid);
						//System.out.println(product.getPname());
						arrayList.add(product);
					}
				}
			}
		}
		request.setAttribute("histroyProduct", arrayList);
		
		request.getRequestDispatcher("/product_list.jsp").forward(request, response);
	}
	//------------------------------------------产品信息功能------------------------------------------------------
	public void ProductInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pid=request.getParameter("pid");
		String cid=request.getParameter("cid");
		String currentPage=request.getParameter("currentPage");
		//写入cookies,判断是否为空,为空写入
		Cookie[] cookies = request.getCookies();
		
		String pids=pid;
		if(cookies!=null){
			for(Cookie cookie:cookies){
				if("pids".equals(cookie.getName())){
					//获取cookie值
					pids = cookie.getValue();
					//封装成链表
					String[] split = pids.split("-");
					List<String> asList = Arrays.asList(split);
					LinkedList<String> linkedList = new LinkedList<String>(asList);
					//更新链表
					if(linkedList.contains(pid)){
						linkedList.remove(pid);
					}
					linkedList.addFirst(pid);
					//转成字符串
					StringBuffer buffer = new StringBuffer();
					for(int i=0;i<linkedList.size()&&i<7;i++){
						buffer.append(linkedList.get(i));
						buffer.append("-");
					}
					pids=buffer.substring(0, buffer.length()-1);									
				}
			}
		}
		//发送cookie	
		Cookie cookie = new Cookie("pids",pids);
		response.addCookie(cookie);			
		//int intCid=Integer.parseInt(cid);
		//int intCurrentPage=Integer.parseInt(currentPage);
		//数据库中活动的商品信息，返回product
		ProductService service = new ProductService();
		Product productInfo=service.productInfo(pid);
		//转向productInfo
		request.setAttribute("cid", cid);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("productInfo", productInfo);
		request.getRequestDispatcher("/product_info.jsp").forward(request, response);
	}
}

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

	//ģ�鹦��
	//--------------------------------------------�ҵĶ�������------------------------------------------------
	public void myOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//�ж��Ƿ��¼
		String user=(String) this.getServletContext().getAttribute("username");
		if(user==null){
			
			response.sendRedirect(request.getContextPath()+"/login.jsp");
			return;
		}
		//�������������װ����
		List<MyOrder> myOrders=new ArrayList<MyOrder>();
		//��ѯ�û�����order������list<order>��������װ
		ProductService service = new ProductService();
		List<Order> myBaseOrder=service.findMyBaseOrder(user);
		for(Order order:myBaseOrder){
			MyOrder myOrder = new MyOrder();
			myOrder.setOid(order.getOid());
			myOrders.add(myOrder);
		}
		//��װmyOrderItem
		for(MyOrder myOrder:myOrders){
			
			List<MyOrderItem> myOrderItems=service.findMyOrderItem(user,myOrder.getOid());
			myOrder.setMyOrderItems(myOrderItems);
						
		}
		//�ض�����ʾ
		request.setAttribute("myOrders", myOrders);
		request.getRequestDispatcher("/order_list.jsp").forward(request, response);
	}
	
	//--------------------------------------------ȷ�϶�������------------------------------------------------
	public void confirmOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//�����ջ�����Ϣ������֧��������֧��״̬
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
		
		//����֧��
	/*	String pd_FrpId = request.getParameter("pd_FrpId");
		switch (pd_FrpId) {
		case "ICBC-NET-B2C"://����
			
			break;
		case "BOC-NET-B2C"://����
			
			break;
		case "ABC-NET-B2C"://ũ��
			
			break;
		case "BOCO-NET-B2C"://����
			
			break;
		case "PINGANBANK-NET"://ƽ������
			
			break;
		case "CCB-NET-B2C"://����
			
			break;
		case "CEB-NET-B2C"://�������
			
			break;
		case "CMBCHINA-NET-B2C"://����
			
			break;

		default:
			break;
		}
		*/
		// ��� ֧�������������
		String orderid = request.getParameter("oid");
		String money = "0.01";
		// ����
		String pd_FrpId = request.getParameter("pd_FrpId");

		// ����֧����˾��Ҫ��Щ����
		String p0_Cmd = "Buy";
		String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString("p1_MerId");
		String p2_Order = orderid;
		String p3_Amt = money;
		String p4_Cur = "CNY";
		String p5_Pid = "";
		String p6_Pcat = "";
		String p7_Pdesc = "";
		// ֧���ɹ��ص���ַ ---- ������֧����˾����ʡ��û�����
		// ������֧�����Է�����ַ
		String p8_Url = ResourceBundle.getBundle("merchantInfo").getString("callback");
		String p9_SAF = "";
		String pa_MP = "";
		String pr_NeedResponse = "1";
		// ����hmac ��Ҫ��Կ
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

		//�ض��򵽵�����֧��ƽ̨
		response.sendRedirect(url);
	}
	
	//--------------------------------------------�ύ��������------------------------------------------------
	public void submitOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		//��װorder,����service��
		Order order = new Order();
		//�ж��Ƿ��¼
		if(this.getServletContext().getAttribute("username")==null){
			
			response.sendRedirect(request.getContextPath()+"/login.jsp");
			
			return;
		}
		//��װprivate String oid;
		String oid=CommonsUtils.getUUID();
		order.setOid(oid);
		//��װprivate String ordertime;DateתString;
//		Date date = (Date) Calendar.getInstance().getTime();
		order.setOrdertime(new SimpleDateFormat("yyy-MM-dd hh:mm:ss").format(new Date()));
		//��װprivate double total;
		Cart cart=(Cart)session.getAttribute("cart");
		order.setTotal(cart.getTotal());
		//��װprivate int state;
		order.setState(0);
		//��װprivate String address;
		order.setAddress(null);
		//��װprivate String name;
		order.setName(null);
		//��װprivate String telephone;
		order.setTelephone(null);
		//��װprivate User user;
		String user=(String) this.getServletContext().getAttribute("username");
		order.setUser(user);
		//��װorderItem
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

	//--------------------------------------------ɾ�����ﳵ������Ʒ����------------------------------------------------
	public void delCartAllProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.setAttribute("cart",null);
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	
	
	//--------------------------------------------ɾ�����ﳵ��Ʒ����------------------------------------------------
	public void delCartProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String pid = request.getParameter("pid");
		HttpSession session = request.getSession();
		Cart cart = (Cart) session.getAttribute("cart");
		
		cart.setTotal(cart.getTotal()-cart.getCartItems().get(pid).getSubtotal());
		cart.getCartItems().remove(pid);
		session.setAttribute("cart",cart);
		
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	
	//--------------------------------------------���빺�ﳵ����------------------------------------------------
	public void sendToCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//���Ҫ��ӵ���Ʒ����
		String pid=request.getParameter("pid");
		int buyNum=Integer.parseInt(request.getParameter("buyNum"));
		//���product		
		ProductService service = new ProductService();
		Product product = service.productInfo(pid);
		//���С��
		double subtotal=product.getShop_price()*buyNum;

		//��session�з�Cart,�Ȼ�ȡ����û�г���û�оʹ���
		HttpSession session = request.getSession();
		Cart cart = (Cart) session.getAttribute("cart");
		if(cart==null){
			cart = new Cart();
		}
		//��װCartItem
		CartItem cartItem = new CartItem();
		
		if(cart.getCartItems().containsKey(product.getPid())){
			buyNum+=cart.getCartItems().get(product.getPid()).getBuyNum();
		}
		
		cartItem.setBuyNum(buyNum);
		cartItem.setProduct(product);
		cartItem.setSubtotal(subtotal);
		//��װ���ﳵ
		cart.getCartItems().put(product.getPid(), cartItem);
		cart.setTotal(cart.getTotal()+cartItem.getSubtotal());
		session.setAttribute("cart",cart);
		
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	
	//--------------------------------------------���๦��-----------------------------------------------------
	public void Category(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CategoryService service = new CategoryService();
		//��������
		//���Jedis�����������ݿ�
		Jedis jedis = JedisPoolUtils.getJedis();
		String categoryListJson = jedis.get("categoryListJson");
		
		//���ж��Ƿ�Ϊ��
		if(categoryListJson==null){
			System.out.println("������û�����ݣ�");
			List<Category> categoryList=service.getCategoryList();
			//תjson
			Gson gson = new Gson();
			categoryListJson = gson.toJson(categoryList);
			jedis.set("categoryListJson",categoryListJson) ;	
		}
				
		response.setContentType("text/html; charset=UTF-8");
		//дjson
		response.getWriter().write(categoryListJson);
	}
	//--------------------------------------------��ҳ����------------------------------------------------------
	public void Index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductService service = new ProductService();
		//���������Ʒ
		List<Product> hotProductList=service.findHotProduct();
		request.setAttribute("hotProductList", hotProductList);
		//���������Ʒ
		List<Product> newProductList=service.findNewProduct();
		request.setAttribute("newProductList", newProductList);
		//��¼��Ϣ
		//request.setAttribute("username", this.getServletContext().getAttribute("username"));
		//request.setAttribute("isLoginSuccess", this.getServletContext().getAttribute("isLoginSuccess"));
		//ת����index.jsp
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}
	//-------------------------------------------��ҳ����----------------------------------------------------
	public void PageProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cid = request.getParameter("cid");
		String targetPage = request.getParameter("targetPage");
		
		int intTargetPage=Integer.parseInt(targetPage);
		
		ProductService service = new ProductService();
		
		PageBean pageBean=service.pageProductList(cid,intTargetPage);
		
		request.setAttribute("pageBean", pageBean);
		request.setAttribute("cid", cid);
		
		//�����¼����
		List<Product> arrayList = new ArrayList<Product>();
		//����cookie
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
	//------------------------------------------��Ʒ��Ϣ����------------------------------------------------------
	public void ProductInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pid=request.getParameter("pid");
		String cid=request.getParameter("cid");
		String currentPage=request.getParameter("currentPage");
		//д��cookies,�ж��Ƿ�Ϊ��,Ϊ��д��
		Cookie[] cookies = request.getCookies();
		
		String pids=pid;
		if(cookies!=null){
			for(Cookie cookie:cookies){
				if("pids".equals(cookie.getName())){
					//��ȡcookieֵ
					pids = cookie.getValue();
					//��װ������
					String[] split = pids.split("-");
					List<String> asList = Arrays.asList(split);
					LinkedList<String> linkedList = new LinkedList<String>(asList);
					//��������
					if(linkedList.contains(pid)){
						linkedList.remove(pid);
					}
					linkedList.addFirst(pid);
					//ת���ַ���
					StringBuffer buffer = new StringBuffer();
					for(int i=0;i<linkedList.size()&&i<7;i++){
						buffer.append(linkedList.get(i));
						buffer.append("-");
					}
					pids=buffer.substring(0, buffer.length()-1);									
				}
			}
		}
		//����cookie	
		Cookie cookie = new Cookie("pids",pids);
		response.addCookie(cookie);			
		//int intCid=Integer.parseInt(cid);
		//int intCurrentPage=Integer.parseInt(currentPage);
		//���ݿ��л����Ʒ��Ϣ������product
		ProductService service = new ProductService();
		Product productInfo=service.productInfo(pid);
		//ת��productInfo
		request.setAttribute("cid", cid);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("productInfo", productInfo);
		request.getRequestDispatcher("/product_info.jsp").forward(request, response);
	}
}

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<!-- 登录 注册 购物车... -->
<div class="container-fluid">
	<div class="col-md-4">
		<img src="img/logo2.png" />
	</div>
	<div class="col-md-5">
		<img src="img/header.png" />
	</div>
	<div class="col-md-3" style="padding-top:20px;" >
	
			<c:if test="${isLoginSuccess==true}">
			<ol class="list-inline" style="width:500px;height:30px">
				    <li><a href="#">欢迎您！${username}
				    </a></li>
				    <li><a href="${pageContext.request.contextPath}/user?method=Exit">退出</a></li>
					<li><a href="register.jsp">注册</a></li>
					<li><a href="cart.jsp">购物车</a></li>
					<li><a href="${pageContext.request.contextPath}/product?method=myOrder">我的订单</a></li>
				</ol>			
			</c:if>		
			
			<c:if test="${isLoginSuccess==false||isLoginSuccess==null}">
			    <ol class="list-inline">
				    <li><a href="login.jsp">登录
				    </a></li>
					<li><a href="register.jsp">注册</a></li>
					<li><a href="cart.jsp">购物车</a></li>
					<li><a href="${pageContext.request.contextPath}/product?method=myOrder">我的订单</a></li>
				</ol>
			</c:if>			
			
	</div>
</div>

<script type="text/javascript">
$(function(){
	var content="";
	$.post(
		"${pageContext.request.contextPath}/product?method=Category",
		function(data){
			for(var i=0;i<data.length;i++){
				content+="<li><a href='${pageContext.request.contextPath}/product?method=PageProduct&cid="+data[i].cid+"&targetPage=${1}'>"+data[i].cname+"</a></li>";
			}		
			$("#category").html(content);
		},
		"json"
	);		
});
   </script>
		

<!-- 导航条 -->
<div class="container-fluid">
	<nav class="navbar navbar-inverse">
		<div class="container-fluid">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="${pageContext.request.contextPath}">首页</a>
			</div>

			<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
				<ul class="nav navbar-nav" id="category">
					<!-- <li class="active"><a href="product_list.htm">手机数码<span class="sr-only">(current)</span></a></li>
					<li><a href="#">电脑办公</a></li>
					<li><a href="#">电脑办公</a></li>
					<li><a href="#">电脑办公</a></li> -->
				</ul>
				<form class="navbar-form navbar-right" role="search">
					<div class="form-group">
						<input type="text" class="form-control" placeholder="Search">
					</div>
					<button type="submit" class="btn btn-default">Submit</button>
				</form>
			</div>
		</div>
		
	</nav>
</div>
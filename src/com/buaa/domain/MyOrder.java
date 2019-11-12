package com.buaa.domain;

import java.util.ArrayList;
import java.util.List;

public class MyOrder {
	
	private String oid;
	
	private List<MyOrderItem> myOrderItems=new ArrayList<MyOrderItem>();

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public List<MyOrderItem> getMyOrderItems() {
		return myOrderItems;
	}

	public void setMyOrderItems(List<MyOrderItem> myOrderItems) {
		this.myOrderItems = myOrderItems;
	}
	
	
}

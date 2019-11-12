package com.buaa.utils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;



public class JedisPoolUtils {
	
	private static JedisPool pool = null;
	
	static{
		
		//鍔犺浇閰嶇疆鏂囦欢
		InputStream in = JedisPoolUtils.class.getClassLoader().getResourceAsStream("redis.properties");
		Properties pro = new Properties();
		try {
			pro.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//鑾峰緱姹犲瓙瀵硅薄
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(Integer.parseInt(pro.get("redis.maxIdle").toString()));//鏈�ぇ闂茬疆涓暟
		poolConfig.setMinIdle(Integer.parseInt(pro.get("redis.minIdle").toString()));//鏈�皬闂茬疆涓暟
		poolConfig.setMaxTotal(Integer.parseInt(pro.get("redis.maxTotal").toString()));//鏈�ぇ杩炴帴鏁�
		pool = new JedisPool(poolConfig,pro.getProperty("redis.url") , Integer.parseInt(pro.get("redis.port").toString()));
	}

	//鑾峰緱jedis璧勬簮鐨勬柟娉�
	public static Jedis getJedis(){
		return pool.getResource();
	}
	/*
	public static void main(String[] args) {
		Jedis jedis = getJedis();
		System.out.println(jedis.get("xxx"));
	}
	*/
	
	
	
}

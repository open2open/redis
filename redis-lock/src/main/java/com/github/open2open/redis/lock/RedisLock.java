package com.github.open2open.redis.lock;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisLock {
	private Logger logger = Logger.getAnonymousLogger();
	private JedisPool jedisPool;
	private ThreadLocal<String> valueTreadLocal = new ThreadLocal<String>();

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	public boolean lock(String key,int count,long seconds){
		Jedis jedis = jedisPool.getResource();
		boolean flag = false;
		try {
			String uuid = UUID.randomUUID().toString();
			String value = key+":"+uuid;
			valueTreadLocal.set(value);
			for(int i = count;i >= 0;i--){
				String setnx = jedis.set(key, value, "NX", "EX", seconds);
				if(setnx.equals("OK")){
					flag = true;
					break;
				}else{
					flag = false;
				}
				if(flag == false){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						logger.log(Level.INFO, "wait fail", e);
					}
				}
			}
			return flag;
		} finally {
			jedis.close();
		}
	}
	
	public boolean unlock(){
		boolean flag = false;
		Jedis jedis = jedisPool.getResource();
		try {
			String value = valueTreadLocal.get();
			String key = value.split(":")[0];
			//为什么不用del方法解锁,因为如果一个线程获取到锁了之后,如果操作业务的时间较长,在解锁的时候,可能这个线程获取到的锁过期了,它解锁的话可能把其它线程获取到的锁给解了
			//用竞争锁并且将锁的过期时间设为0的方式是让锁自然过期
			String setnx = jedis.set(key, value, "NX", "EX", 0);
			valueTreadLocal.remove();
			if(setnx.equals("OK")){
				flag = true;
				return flag;
			}else{
				flag = false;
			}
			return flag;
		} finally {
			jedis.close();
		}
	}
}

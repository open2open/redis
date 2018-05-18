package com.github.open2open.redis.lock;

import java.util.UUID;

import redis.clients.jedis.Jedis;

public class RedisLock {
	private Jedis jedis ;
	private ThreadLocal<String> valueTreadLocal = new ThreadLocal<String>();

	public void setJedis( Jedis jedis) {
		this.jedis = jedis;
	}

	public boolean lock(String key,int count,long seconds){
		String uuid = UUID.randomUUID().toString();
		String value = key+":"+uuid;
		valueTreadLocal.set(value);
		boolean flag = false;
		while(true){
			if(count <= 0){
				break;
			}
			String setnx = jedis.set(key, value, "NX", "EX", seconds);
			if(setnx.equals("OK")){
				flag = true;
				return flag;
			}else{
				flag = false;
			}
		}
		return flag;
	}
	
	public boolean unlock(){
		boolean flag = false;
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
	}
}

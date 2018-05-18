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

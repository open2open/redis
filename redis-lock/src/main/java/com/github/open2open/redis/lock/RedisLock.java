package com.github.open2open.redis.lock;

import java.util.UUID;

import redis.clients.jedis.Jedis;

public class RedisLock {
	private Jedis jedis;
	private ThreadLocal<String> valueTreadLocal = new ThreadLocal<String>();

	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}

	public boolean lock(String key,int count,long timeMills){
		String uuid = UUID.randomUUID().toString();
		String value = key+":"+uuid;
		valueTreadLocal.set(value);
		boolean flag = false;
		while(true){
			if(count <= 0){
				break;
			}
			Long setnx = jedis.setnx(key, key);
			if(setnx == 1){
				flag = true;
				return flag;
			}else{
				flag = false;
			}
		}
		return flag;
	}
	
	public boolean unlock(){
		String value = valueTreadLocal.get();
		String key = value.split(":")[0];
		jedis.del(key);
		valueTreadLocal.remove();
		return true;
	}
}

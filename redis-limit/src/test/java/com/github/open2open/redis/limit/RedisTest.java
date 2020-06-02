package com.github.open2open.redis.limit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisTest {
	
	@Test
	public void testMset(){
		JedisPool jedisPool = null;
		InputStream in = null;
		try {
			jedisPool = new JedisPool("127.0.0.1", 6379);
			Jedis jedis = jedisPool.getResource();
			String script = "";
			in = this.getClass().getClassLoader().getResourceAsStream("redis-limit.lua");
			byte[] buffer = new byte[100];
			ByteArrayOutputStream bou = new ByteArrayOutputStream();
			int length = 0;
			while((length=in.read(buffer)) != -1){
				bou.write(buffer, 0, length);
			}
			byte[] bytes = bou.toByteArray();
			script = new String(bytes);
			Object eval = jedis.eval(script);
			System.out.println(eval);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(jedisPool != null){
				jedisPool.close();
			}
		}
	}
}

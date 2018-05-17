# redis工具类 #
需要写的项目  
## redis lock(基于redis的分布式锁) ##
### redis 分布式锁的实现思路  ###
如果没有获取到锁的话,设置尝试次数,执行几次,如果仍然没有获取到锁,则返回状态为false,表示没有获取到锁    
获取到锁之后,完成相应的业务操作后,需要释放锁,就删除锁  
![](https://raw.githubusercontent.com/open2open/redis/master/images/redis-lock1.jpg)
### 死锁的问题  ###
有节点获取到锁之后,但是宕机之后和redis服务器失去了联系,其它节点
获取不到锁,就不能进行后续的业务的操作  
### 锁过期的问题  ###
如果一个节点获取到锁之后,在没有完成后续的业务操作,锁就过期了,其它节点就获取到了这个锁,会不会照成并发的问题    
  
## redis queue(基于redis的分布式队列) ##
## redis limit(基于redis的限流工具) ##
## redis cache(基于redis的缓存工具) ##

# redis工具类 #
需要写的项目1  
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
### redis lock的注解方式的实现 ###
redis lock 基于spring proxy的方式实现
redis lock 基于aspect方式的实现   
## redis queue(基于redis的分布式队列) ##
## redis limit(基于redis的限流工具) ##
### 实现思路 ###
数据结构:key-value,其中key是需要限制的操作的对象的定义,value是需要限制的次数,还需要定义这个key的过期时间,表示在一定的时间内对限制对象访问频率的控制.  
每次取的时候当前时间的毫秒和上一次时间毫秒相减的差值乘以每毫秒发令牌的数量的乘积为这段时间可以取的令牌数量  
用redis事务的方法,将计数器增加和设置缓存时间,两个命令放在一个事务中执行.  
令牌桶的方式实现:设置一个固定容量的桶,以固定的速率将令牌放入桶中,如果桶已满了,则不再放入令牌,直到桶有剩余的空间时再继续放令牌.算法是以固定的速率添加令牌,但是不代表实际上以固定的速率添加将令牌添加到令牌桶中.因为如果每设置一个限速规则,就启动一个线程添加令牌的话,如果设置1000个限流规则的话,就需要启动1000个线程进行添加令牌的操作,这样会很消耗服务器资源的.所以改用触发式的添加令牌,即在获取令牌的时候进行添加令牌的操作.
## redis cache(基于redis的缓存工具) ##
## redis  sequence(基于redis的分布式id) ##

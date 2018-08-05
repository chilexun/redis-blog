package com.github.blog.configuration;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisSupport {

    private RedisTemplate<String, Object> redisTemplate;

    public RedisSupport(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public void multi(){
        redisTemplate.multi();
    }

    public List<Object> exec(){
        return redisTemplate.exec();
    }

    public void discard(){
        redisTemplate.discard();
    }

    public void expire(String key,long time){
        if (time>0){
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
    }

    /**
     * 根据key 获取过期时间
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key){
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key){
        return exists(key);
    }
    public boolean exists(String key) { return redisTemplate.hasKey(key);}

    /**
     * 删除缓存
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String ... key){
        if(key!=null&&key.length>0){
            if(key.length==1){
                redisTemplate.delete(key[0]);
            }else{
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    //============================String=============================
    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key){
        return key==null?null:redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public void set(String key,Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public void set(String key,Object value,long time){
        if(time>0){
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        }else{
            set(key, value);
        }
    }

    /**
     * 递增
     * @param key 键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     * @param key 键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    //================================Map=================================
    /**
     * HashGet
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key,String item){
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object,Object> hmget(String key){
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public void hmset(String key, Map<String,Object> map){
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * HashSet 并设置时间
     * @param key 键
     * @param map 对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public void hmset(String key, Map<String,Object> map, long time){
        redisTemplate.opsForHash().putAll(key, map);
        if(time>0){
            expire(key, time);
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true 成功 false失败
     */
    public void hset(String key,String item,Object value) {
        redisTemplate.opsForHash().put(key, item, value);
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public void hset(String key,String item,Object value,long time) {
        redisTemplate.opsForHash().put(key, item, value);
        if(time>0){
            expire(key, time);
        }
    }

    /**
     * 删除hash表中的值
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item){
        redisTemplate.opsForHash().delete(key,item);
    }

    /**
     * 判断hash表中是否有该项的值
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item){
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @param key 键
     * @param item 项
     * @param by 要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item,double by){
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     * @param key 键
     * @param item 项
     * @param by 要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item,double by){
        return redisTemplate.opsForHash().increment(key, item,-by);
    }

    //============================set=============================
    /**
     * 根据key获取Set中的所有值
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key){
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 根据value从一个set中查询,是否存在
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key,Object value){
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 将数据放入set缓存
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object...values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 将set数据放入缓存
     * @param key 键
     * @param time 时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key,long time,Object...values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        if(time>0)
            expire(key, time);
        return count;
    }

    /**
     * 获取set缓存的长度
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key){
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 将数据加入set中
     * @param key 键
     * @param values 值，可以是多个
     * @return 添加成功的数量
     */
    public long sAdd(String key, Object... values){
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 移除值为value的
     * @param key 键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long sRemove(String key, Object ...values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 判断元素是否是集合成员
     * @param key
     * @param value
     * @return
     */
    public boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 返回集合中的所有元素
     * @param key
     * @return
     */
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     * @param key 键
     * @param start 开始
     * @param end 结束  0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end){
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取list缓存的长度
     * @param key 键
     * @return
     */
    public long lGetListSize(String key){
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 通过索引 获取list中的值
     * @param key 键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key,long index){
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return
     */
    public long lPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public void lPush(String key, Object value, long time) {
        redisTemplate.opsForList().leftPush(key, value);
        if (time > 0)
            expire(key, time);
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return
     */
    public void lPushAll(String key, List<Object> value) {
        redisTemplate.opsForList().leftPushAll(key, value);
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public void lPushAll(String key, List<Object> value, long time) {
        redisTemplate.opsForList().leftPushAll(key, value);
        if (time > 0)
            expire(key, time);
    }
    /**
     * 从list的右边弹出一个元素，没有则等待timeout秒
     * @param key
     * @param timeoutSec 等待时间，单位秒
     * @return
     */
    public Object bRPop(String key, long timeoutSec) {
    	return redisTemplate.opsForList().rightPop(key, timeoutSec, TimeUnit.SECONDS);
    }

    /**
     * 根据索引修改list中的某条数据
     * @param key 键
     * @param index 索引
     * @param value 值
     * @return
     */
    public void lUpdateIndex(String key, long index,Object value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 移除N个值为value
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key,long count,Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    /**
     * 返回指定区间内的元素
     * @param key
     * @param startIndex
     * @param stopIndex
     * @return
     */
    public List<Object> lRange(String key, long startIndex, long stopIndex){
        return redisTemplate.opsForList().range(key, startIndex, stopIndex);
    }
   
//    =======================SortedSet======================
    /**
     * 往SortedSet中添加一个元素
     * @param key
     * @param value
     * @param score
     * @return
     */
    public boolean zAdd(String key, Object value, double score) {
    	return redisTemplate.opsForZSet().add(key, value, score);
    }
    
    /**
     * 返回有序集的基数
     * @param key
     * @return
     */
    public long zCard(String key) {
    	return redisTemplate.opsForZSet().zCard(key);
    }
    /**
     * 返回score值在min和max之间的数量
     * @param key
     * @param min
     * @param max
     * @return 
     */
    public long zCount(String key, double min, double max) {
    	return redisTemplate.opsForZSet().count(key, min, max);
    }
    
    /**
     * 给指定的元素增加score
     * @param key
     * @param item
     * @param incr
     * @return 增加之后的score
     */
    public Double zIncrby(String key, Object item, double incr) {
    	return redisTemplate.opsForZSet().incrementScore(key, item, incr);
    }
    /**
     * 获取下标在start和stop之间的元素
     * @param key
     * @param start
     * @param stop
     * @return
     */
    public Set<Object> zRange(String key, long start, long stop) {
    	return redisTemplate.opsForZSet().range(key, start, stop);
    }
    /**
     * 获取score在start和stop之间的元素
     * @param key
     * @param start
     * @param stop
     * @return
     */
    public Set<Object> zRangeByScore(String key, double start, double stop){
    	return redisTemplate.opsForZSet().rangeByScore(key, start, stop);
    }
    /**
     * 
     * @param key
     * @param start
     * @param stop
     * @return
     */   
    public Set<Object> zRangeByScore(String key, double start, double stop, int offset, int count){
    	return redisTemplate.opsForZSet().rangeByScore(key, start, stop, offset, count);
    }
    /**
     * 获取item的排行
     * @param key
     * @param item
     * @return -1元素不存在
     */
    public long zRank(String key, Object item) {
    	Long pos = redisTemplate.opsForZSet().rank(key, item);
    	if(pos == null)
    		return -1;
    	return pos.longValue();
    }
    /**
     * 移除一个或者多个元素
     * @param key
     * @param items
     * @return 移除的数量
     */
    public long zRem(String key, Object... items) {
    	return redisTemplate.opsForZSet().remove(key, items);
    }
    /**
     * 根据排名删除
     * @param key
     * @param start
     * @param stop
     * @return 删除的数量
     */
    public long zRemRangeByRank(String key, long start, long stop) {
    	return redisTemplate.opsForZSet().removeRange(key, start, stop);
    }
    /**
     * 根据分数移除元素
     * @param key
     * @param start
     * @param stop
     * @return 移除元素的数量
     */
    public long zRemRangeByScore(String key, double start, double stop) {
    	return redisTemplate.opsForZSet().removeRangeByScore(key, start, stop);
    }
    /**
     * 按位置从大到小返回元素
     * @param key
     * @param start
     * @param stop
     * @return
     */
    public Set<Object> zRevRange(String key, long start, long stop){
    	return redisTemplate.opsForZSet().reverseRange(key, start, stop);
    }
    /**
     * 按分数从大到小返回元素
     * @param key
     * @param start
     * @param stop
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zRevRangeWithScores(String key, long start, long stop){
    	return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, stop);
    }
    
    /**
     * 按分数从大到小返回元素
     * @param key
     * @param start
     * @param stop
     * @return
     */
    public Set<Object> zRevRange(String key, double start, double stop){
    	return redisTemplate.opsForZSet().reverseRangeByScore(key, start, stop);
    }
    
    public Set<Object> zRevRangeBySocore(String key, double start, double stop, long offset, long count){
    	return redisTemplate.opsForZSet().reverseRangeByScore(key, start, stop, offset, count);
    }
    public Set<ZSetOperations.TypedTuple<Object>> zRevRangeByScoreWithScores(String key, double start, double stop, long offset, long count){
    	return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, start, stop, offset, count);
    }
    /**
     * 返回倒序排名
     * @param key
     * @return
     */
    public long zRevRank(String key, Object item){
    	Long pos = redisTemplate.opsForZSet().reverseRank(key, item);
    	if(pos == null)
    		return -1;
    	return pos.longValue();
    }
    /**
     * 返回元素的score
     * @param key
     * @param item
     * @return
     */
    public Double zScore(String key, Object item) {
    	return redisTemplate.opsForZSet().score(key, item);
    }

    //    =======================Pipeline======================

    /**
     * 流水线执行，提高效率
     * @param callback
     * @return
     */
    public List<Object> executePipelined(RedisCallback<?> callback){
        return redisTemplate.executePipelined(callback);
    }

    public List<Object> executePipelined(SessionCallback<?> session){
        return redisTemplate.executePipelined(session);
    }
}

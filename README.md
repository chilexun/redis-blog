# Redis Blog

使用SpringBoot和Redis实现的个人博客系统后台

# 项目结构

configuration：springboot配置类，redis工具类  
dao: Dao接口和实现类，实现类用impl结尾  
dto：不使用entity，dao和service，service和controller数据传输都使用dto  
service:存放service接口和实现类，实现类名以impl结尾  
controller：存放controller层代码  

**ResultCode**  
|  code   |  注释   |  
| --- | --- |  
|  0   |  成功   |  
|  110   |  服务端异常   |  
|  120   |  身份验证失败   |   
|  130   |  权限不足   |  
|  140   |  参数错误   |  
|  150   |  版本错误   |  
|  160   |  业务逻辑异常   |  
|  170   |  系统维护中   |  
|  180   |  业务下线   |  
|  999   |  服务端未知错误   |  

# Redis使用规范  
redis只使用0号库  
KEY规范  <业务名>:<表名>:<id>  

**工具类：**  
SequenceSupport：用来生成自增长的主键id  
RedisSupport：reids命令封装类，Dao内可使用该类操作redis，也可以注入RedisTemplate<String,Object>  





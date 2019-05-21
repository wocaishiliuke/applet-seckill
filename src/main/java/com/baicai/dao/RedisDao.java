package com.baicai.dao;

import com.baicai.pojo.SkProduct;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.runtime.RuntimeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Description Redis操作类
 * @Author yuzhou
 * @Date 19-5-20
 */
public class RedisDao {

    private final Logger logger = LoggerFactory.getLogger(RedisDao.class);

    private RuntimeSchema<SkProduct> schema = RuntimeSchema.createFrom(SkProduct.class);

    private static final String PREFIX = "skproduct_";
    private static final int TIMEOUT = 60 * 60;

    private final JedisPool jedisPool;

    public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }
    
    /**
     * 从Redis中获取秒杀商品
     * @param id
     * @return
     */
    public SkProduct getSkProduct(Long id) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = PREFIX + id;
            byte[] bytes = jedis.get(key.getBytes());
            if (bytes != null) {
                // 反序列化后返回
                SkProduct skProduct = schema.newMessage();
                ProtobufIOUtil.mergeFrom(bytes, skProduct, schema);
                return skProduct;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 将秒杀商品存入Redis
     * @param skProduct
     * @return
     */
    public String putSkProduct(SkProduct skProduct) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = PREFIX + skProduct.getId();
            byte[] bytes = ProtobufIOUtil.toByteArray(skProduct, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            // 设置超时时间
            return jedis.setex(key.getBytes(), TIMEOUT, bytes);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}

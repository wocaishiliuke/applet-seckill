package com.baicai.mapper;

import com.baicai.pojo.Seckill;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description 秒杀商品Mapperl映射器
 * @Author yuzhou
 * @Date 19-5-17
 */
public interface SeckillMapper {

   /**
    * 根据秒杀商品ID，查询商品详情
    *
    * @param seckillId 秒杀商品ID
    * @return 商品信息
    */
   Seckill queryById(@Param("seckillId") long seckillId);

   /**
    * 查询秒杀商品分页列表
    *
    * @param offset 页码
    * @param limit  每页个数
    * @return 分页商品列表
    */
   List<Seckill> queryPageList(@Param("offset") int offset, @Param("limit") int limit);

   /**
    * 根据秒杀商品ID，减库存
    *
    * @param seckillId 秒杀商品ID
    * @param killTime  秒杀时间
    * @return 秒杀成功返回1,否则就返回0
    */
   int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") LocalDateTime killTime);
}
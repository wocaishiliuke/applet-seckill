package com.baicai.mapper;

import com.baicai.pojo.SkProduct;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description 秒杀商品Mapper（映射器）
 * @Author yuzhou
 * @Date 19-5-17
 */
public interface SkProductMapper {

   /**
    * 根据秒杀商品ID，查询商品详情
    *
    * @param id 秒杀商品ID
    * @return 商品信息
    */
   SkProduct queryById(@Param("id") long id);

   /**
    * 查询秒杀商品分页列表
    *
    * @param offset 起始值
    * @param limit 条数
    * @return 分页商品列表
    */
   List<SkProduct> queryPageList(@Param("offset") int offset, @Param("limit") int limit);

   /**
    * 根据秒杀商品ID，减库存
    *
    * @param id 秒杀商品ID
    * @param killTime 秒杀时间
    * @return 秒杀成功返回1，否则就返回0
    */
   int reduceNumber(@Param("id") long id, @Param("killTime") LocalDateTime killTime);
}
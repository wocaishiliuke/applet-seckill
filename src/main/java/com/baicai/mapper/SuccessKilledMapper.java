package com.baicai.mapper;

import com.baicai.pojo.SuccessKilled;
import org.apache.ibatis.annotations.Param;

/**
 * @Description 秒杀记录Mapper映射器
 * @Author yuzhou
 * @Date 19-5-17
 */
public interface SuccessKilledMapper {

    /**
     * 根据秒杀商品ID && 用户手机号，查询秒杀记录
     *
     * @param seckillId 秒杀商品ID
     * @param userPhone 用户手机号
     * @return 秒杀记录
     */
    SuccessKilled queryBySeckillIdAndUserPhone(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * 新增秒杀记录
     *
     * @param seckillId 秒杀商品ID
     * @param userPhone 用户手机号
     * @return 成功插入返回1, 否则返回0
     */
    int insert(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
}

package com.baicai.mapper;

import com.baicai.pojo.SkRecord;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * @Description 秒杀记录Mapper映射器
 * @Author yuzhou
 * @Date 19-5-17
 */
public interface SkRecordMapper {

    /**
     * 根据秒杀商品ID&&用户手机号，查询秒杀记录
     *
     * @param skproductId 秒杀商品ID
     * @param userPhone 用户手机号
     * @return 秒杀记录
     */
    SkRecord queryBySkproductIdAndUserPhone(@Param("skproductId") Long skproductId, @Param("userPhone") Long userPhone);

    /**
     * 新增秒杀记录
     *
     * @param skproductId 秒杀商品ID
     * @param userPhone 用户手机号
     * @param createTime 秒杀时间=减库存时间
     * @return 成功插入返回1，否则返回0
     */
    Integer insert(@Param("skproductId") Long skproductId, @Param("userPhone") Long userPhone, @Param("userPhone") LocalDateTime createTime);
}

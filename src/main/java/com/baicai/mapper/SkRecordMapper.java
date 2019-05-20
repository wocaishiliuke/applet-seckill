package com.baicai.mapper;

import com.baicai.pojo.SkRecord;
import org.apache.ibatis.annotations.Param;

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
    SkRecord queryBySkproductIdAndUserPhone(@Param("skproductId") long skproductId, @Param("userPhone") long userPhone);

    /**
     * 新增秒杀记录
     *
     * @param skproductId 秒杀商品ID
     * @param userPhone 用户手机号
     * @return 成功插入返回1，否则返回0
     */
    int insert(@Param("skproductId") long skproductId, @Param("userPhone") long userPhone);
}

package com.baicai.service;

import com.baicai.exception.SeckillClosedException;
import com.baicai.exception.SeckillException;
import com.baicai.exception.SeckillRepeatedException;
import com.baicai.pojo.SkProduct;
import com.baicai.pojo.dto.ExposeResult;
import com.baicai.pojo.dto.SeckillResult;

import java.util.List;

/**
 * @Description 秒杀商品（库存）业务层
 * @Author yuzhou
 * @Date 19-5-17
 */
public interface SkProductService {

    /**
     * 根据ID，查询秒杀商品
     *
     * @param id 秒杀商品ID
     * @return 秒杀商品
     */
    SkProduct getById(long id);

    /**
     * 查询秒杀商品分页列表
     *
     * @param offset 起始值
     * @param limit 条数
     * @return 分页商品列表
     */
    List<SkProduct> getPageList(int offset, int limit);

    /**
     * 在秒杀开启时，提供秒杀接口的地址，否则输出系统时间和秒杀地址
     *
     * @param id 秒杀商品ID
     * @return 秒杀接口地址 或 系统时间和秒杀地址
     */
    ExposeResult expose(long id);

    /**
     * 执行秒杀操作
     *
     * @param id 秒杀商品ID
     * @param userPhone 手机号码
     * @param md5 md5加密值
     * @return 秒杀结果
     */
    SeckillResult seckill(long id, long userPhone, String md5)throws SeckillException, SeckillRepeatedException, SeckillClosedException;

    /**
     * 使用储存过程执行秒杀
     * @param id
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillResult seckillByProcedure(Long id, long userPhone, String md5);
}

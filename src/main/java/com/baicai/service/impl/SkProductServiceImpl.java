package com.baicai.service.impl;

import com.baicai.dao.RedisDao;
import com.baicai.enums.SeckillStatusEnum;
import com.baicai.exception.*;
import com.baicai.mapper.SkProductMapper;
import com.baicai.mapper.SkRecordMapper;
import com.baicai.pojo.SkProduct;
import com.baicai.pojo.SkRecord;
import com.baicai.pojo.dto.CommonResult;
import com.baicai.pojo.dto.ExposeResult;
import com.baicai.pojo.dto.SeckillResult;
import com.baicai.service.SkProductService;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 秒杀商品（库存）业务实现
 * @Author yuzhou
 * @Date 19-5-17
 */
@Service
public class SkProductServiceImpl implements SkProductService {

    private static final Logger logger = LoggerFactory.getLogger(SkProductServiceImpl.class);
    /* 盐值 */
    private static final String SECKILL_SALT = "seckillSalt";

    @Autowired
    private SkProductMapper skProductMapper;
    @Autowired
    private SkRecordMapper skRecordMapper;
    @Autowired
    private RedisDao redisDao;

    /**
     * 根据ID，查询秒杀商品
     *
     * @param id 秒杀商品ID
     * @return 秒杀商品
     */
    @Override
    public SkProduct getById(Long id) {
        SkProduct skProduct = redisDao.getSkProduct(id);
        if (skProduct == null) {
            skProduct = skProductMapper.queryById(id);
            if (skProduct != null)
                // 放入redis
                redisDao.putSkProduct(skProduct);
        }
        return skProduct;
    }

    /**
     * 查询秒杀商品分页列表
     *
     * @param offset 起始值
     * @param limit 条数
     * @return 分页商品列表
     */
    @Override
    public List<SkProduct> getPageList(Integer offset, Integer limit) {
        return skProductMapper.queryPageList(offset, limit);
    }

    /**
     * 在秒杀开启时输出秒杀接口的地址,否则输出系统时间跟秒杀地址
     *
     * @param id 秒杀商品ID
     * @return 秒杀接口地址 或 系统时间和秒杀地址
     */
    @Override
    public ExposeResult expose(Long id) {
        // 判断是否存在该秒杀商品
        /*SkProduct skProduct = skProductMapper.queryById(id);
        if (skProduct == null) {
            logger.warn("查询不到该商品");
            return new ExposeResult(false, id);
        }*/
        SkProduct skProduct = redisDao.getSkProduct(id);
        if (skProduct == null) {
            // 访问数据库读取数据
            skProduct = skProductMapper.queryById(id);
            if (skProduct == null) {
                logger.warn("查询不到该商品");
                return new ExposeResult(false, id);
            } else {
                // 放入redis
                redisDao.putSkProduct(skProduct);
            }
        }

        // 判断是否在秒杀期间
        LocalDateTime startTime = skProduct.getStartTime();
        LocalDateTime endTime = skProduct.getEndTime();
        LocalDateTime nowTime = LocalDateTime.now();
        // 秒杀开启状态，返回商品id（加密）
        if (nowTime.isAfter(startTime) && nowTime.isBefore(endTime)) {
            return new ExposeResult(true, getSaltedMd5(id), id);
        }
        // 秒杀未开启状态，返回商品id
        return new ExposeResult(false, id, nowTime, startTime, endTime);
    }

    /**
     * 执行秒杀操作
     * 这里的自定义业务异常，都可以使用return代替，只是为了使用的异常统一处理
     *
     * @param id 秒杀商品ID
     * @param userPhone 用户手机号
     * @param md5 md5加密值
     * @return 秒杀结果
     */
    @Override
    @Transactional
    public SeckillResult seckill(Long id, Long userPhone, String md5) {

        if(userPhone == null) {
            logger.error("请求缺少手机号");
            throw new SeckillNoPhoneException("seckill data without phone...");
        }
        if (md5 == null || !md5.equals(getSaltedMd5(id))) {
            logger.error("id的md5不匹配，说明数据被篡改");
            throw new SeckillRewriteException("seckill data is rewrited...");
        }

        // 执行秒杀
        LocalDateTime nowTime = LocalDateTime.now();
        try {
            // 减库存
            Integer affectNumber = skProductMapper.reduceNumber(id, nowTime);
            if (affectNumber <= 0) {
                logger.warn("减库存失败, 说明秒杀结束");
                throw new SeckillClosedException("seckill is closed...");
            } else {
                // 减库存成功，新增秒杀记录
                Integer insertCount = skRecordMapper.insert(id, userPhone, nowTime);
                // 判断是否重复秒杀
                if (insertCount <= 0) {
                    logger.warn("新增秒杀记录失败, 说明重复秒杀");
                    throw new SeckillRepeatedException("seckill repeated...");
                } else {
                    // 秒杀成功（减库存成功&&不是重复秒杀）
                    SkRecord skRecord = this.skRecordMapper.queryBySkproductIdAndUserPhone(id, userPhone);
                    return new SeckillResult(id, SeckillStatusEnum.SUCCESS, skRecord);
                }
            }
        } catch (SeckillClosedException | SeckillRepeatedException e1) {
            throw e1;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 其他异常转换为自定义异常
            throw new SeckillException("seckill inner error: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public SeckillResult seckillByProcedure(Long id, Long userPhone, String md5) {
        if(userPhone == null) {
            logger.error("请求缺少手机号");
            throw new SeckillNoPhoneException("seckill data without phone...");
        }
        if (md5 == null || !md5.equals(getSaltedMd5(id))) {
            logger.error("id的md5不匹配，说明数据被篡改");
            throw new SeckillRewriteException("seckill data is rewrited...");
        }

        LocalDateTime killTime = LocalDateTime.now();
        Map<String, Object> map = new HashMap<>(8);
        map.put("id", id);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
        try {
            // 执行储存过程
            skProductMapper.seckillByProcedure(map);
            // 获取result（获取不到时，默认-2，表示sql错误INNER_ERROR）
            int result = MapUtils.getInteger(map, "result", -2);
            // 根据存储过程的返回值，抛异常或返回
            if (result == SeckillStatusEnum.SUCCESS.getStatus()) {
                SkRecord skRecord = skRecordMapper.queryBySkproductIdAndUserPhone(id, userPhone);
                return new SeckillResult(id, SeckillStatusEnum.SUCCESS, skRecord);
            } else if (result == SeckillStatusEnum.CLOSED.getStatus()) {
                logger.warn("减库存失败, 说明秒杀结束");
                throw new SeckillClosedException("seckill is closed...");
            } else if (result == SeckillStatusEnum.REPEAT_KILL.getStatus()) {
                logger.warn("新增秒杀记录失败, 说明重复秒杀");
                throw new SeckillRepeatedException("seckill repeated...");
            } else {
                logger.warn("内部错误");
                throw new SeckillException("seckill inner error...");
            }

        } catch (SeckillClosedException | SeckillRepeatedException e1) {
            throw e1;
        } catch (SeckillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 其他异常转换为自定义异常
            throw new SeckillException("seckill inner error: " + e.getMessage());
        }
    }

    private String getSaltedMd5(Long seckillId) {
        String base = seckillId + "/" + SECKILL_SALT;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }
}

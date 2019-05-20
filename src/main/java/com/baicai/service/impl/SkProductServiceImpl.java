package com.baicai.service.impl;

import com.baicai.dao.RedisDao;
import com.baicai.enums.SeckillStatusEnum;
import com.baicai.exception.SeckillClosedException;
import com.baicai.exception.SeckillException;
import com.baicai.exception.SeckillRepeatedException;
import com.baicai.mapper.SkProductMapper;
import com.baicai.mapper.SkRecordMapper;
import com.baicai.pojo.SkProduct;
import com.baicai.pojo.SkRecord;
import com.baicai.pojo.dto.ExposeResult;
import com.baicai.pojo.dto.SeckillResult;
import com.baicai.service.SkProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

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
    public SkProduct getById(long id) {
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
    public List<SkProduct> getPageList(int offset, int limit) {
        return skProductMapper.queryPageList(offset, limit);
    }

    /**
     * 在秒杀开启时输出秒杀接口的地址,否则输出系统时间跟秒杀地址
     *
     * @param id 秒杀商品ID
     * @return 秒杀接口地址 或 系统时间和秒杀地址
     */
    @Override
    public ExposeResult expose(long id) {
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
     *
     * @param id 秒杀商品ID
     * @param userPhone 用户手机号
     * @param md5 md5加密值
     * @return 秒杀结果
     */
    @Override
    @Transactional
    public SeckillResult seckill(long id, long userPhone, String md5) throws SeckillException {
        if (md5 == null || !md5.equals(getSaltedMd5(id))) {
            logger.error("秒杀数据被篡改");
            throw new SeckillException("seckill data rewrite...");
        }

        // 执行秒杀
        LocalDateTime nowTime = LocalDateTime.now();
        try {
            // 减库存
            int affectNumber = skProductMapper.reduceNumber(id, nowTime);
            if (affectNumber <= 0) {
                logger.warn("减库存失败, 说明秒杀结束");
                throw new SeckillClosedException("seckill is closed...");
            } else {
                // 减库存成功，新增秒杀记录
                int insertCount = skRecordMapper.insert(id, userPhone);
                // 判断是否重复秒杀
                if (insertCount <= 0) {
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
            throw new SeckillException("seckill inner error : " + e.getMessage());
        }
    }

    private String getSaltedMd5(long seckillId) {
        String base = seckillId + "/" + SECKILL_SALT;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }
}

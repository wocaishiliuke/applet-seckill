package com.baicai.mapper;

import com.baicai.pojo.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-dao.xml"})
public class SeckillMapperTest {

    @Autowired
    private SeckillMapper seckillMapper;

    @Test
    public void queryById() {
        long seckillId = 1000;
        Seckill seckill = seckillMapper.queryById(seckillId);
        System.out.println(seckill.toString());
    }

    @Test
    public void queryPageList() {
        List<Seckill> seckills = seckillMapper.queryPageList(0, 100);
        for (Seckill seckill : seckills) {
            System.out.println(seckill.toString());
        }
    }

    @Test
    public void reduceNumber() {
        long seckillId=1000;
        LocalDateTime localDateTime=LocalDateTime.now();
        int i = seckillMapper.reduceNumber(seckillId, localDateTime);
        System.out.println(i);
    }
}
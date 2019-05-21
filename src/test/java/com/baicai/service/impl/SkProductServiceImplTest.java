package com.baicai.service.impl;

import com.baicai.exception.SeckillClosedException;
import com.baicai.exception.SeckillRepeatedException;
import com.baicai.pojo.SkProduct;
import com.baicai.pojo.dto.ExposeResult;
import com.baicai.pojo.dto.SeckillResult;
import com.baicai.service.SkProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-service.xml", "classpath:spring/applicationContext-dao.xml"})
public class SkProductServiceImplTest {

    @Autowired
    private SkProductService skProductService;

    @Test
    public void getById() throws Exception {
        System.out.println(skProductService.getById(1000L).toString());
    }

    @Test
    public void getPageList() throws Exception {
        List<SkProduct> seckillList = skProductService.getPageList(0,4);
        System.out.println(seckillList.toString());
    }

    @Test
    public void expose() throws Exception {
        ExposeResult exposeResult = skProductService.expose(1000L);
        System.out.println(exposeResult.toString());
    }
    
    /**
     * 可分别制造SeckillClosedException、SeckillRepeatedException、其他RuntimeException
     * @return void
     */
    @Test
    public void seckill() throws Exception {
        Long id = 1000L;
        ExposeResult exposeResult = skProductService.expose(id);
        if (exposeResult.isExposed()) {
            long userPhone = 12222222222L;
            String md5 = "c5a57809123be9581aefb4991f09998c";
            try {
                SeckillResult seckillResult = skProductService.seckill(id, userPhone, md5);
                System.out.println(seckillResult.toString());
            } catch (SeckillClosedException | SeckillRepeatedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("秒杀未开启");
        }
    }
    
    /**
     * 测试使用存储过程的秒杀
     * @return void
     */
    @Test
    public void executeSeckillProcedureTest() {
        long id = 1001;
        long phone = 13688888888L;
        ExposeResult exposeResult = skProductService.expose(id);
        if (exposeResult.isExposed()) {
            String md5 = exposeResult.getMd5();
            SeckillResult seckillResult = skProductService.seckillByProcedure(id, phone, md5);
            System.out.println(seckillResult.getStateInfo());
        }
    }
}
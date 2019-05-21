package com.baicai.mapper;

import com.baicai.pojo.SkProduct;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-dao.xml"})
public class SkProductMapperTest {

    @Autowired
    private SkProductMapper skProductMapper;

    @Test
    public void queryById() {
        Long id = 1000L;
        SkProduct skProduct = skProductMapper.queryById(id);
        System.out.println(skProduct.toString());
    }

    @Test
    public void queryPageList() {
        List<SkProduct> skProducts = skProductMapper.queryPageList(0, 100);
        for (SkProduct skProduct : skProducts) {
            System.out.println(skProduct.toString());
        }
    }

    @Test
    public void reduceNumber() {
        Long id = 1000L;
        LocalDateTime localDateTime=LocalDateTime.now();
        int i = skProductMapper.reduceNumber(id, localDateTime);
        System.out.println(i);
    }
}
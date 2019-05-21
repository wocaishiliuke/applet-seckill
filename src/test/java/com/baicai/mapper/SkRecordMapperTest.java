package com.baicai.mapper;

import com.baicai.pojo.SkRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-dao.xml"})
public class SkRecordMapperTest {

    @Autowired
    private SkRecordMapper skRecordMapper;

    @Test
    public void queryBySkproductIdAndUserPhone() {
        Long id = 1000L;
        Long userPhone = 13888888888L;
        SkRecord skRecord = skRecordMapper.queryBySkproductIdAndUserPhone(id, userPhone);
        System.out.println(skRecord);
    }

    @Test
    public void insert() {
        Long id = 1000L;
        Long userPhone = 13888888888L;
        System.out.println(skRecordMapper.insert(id,userPhone, LocalDateTime.now()));
    }
}
package com.baicai.mapper;

import com.baicai.pojo.SkRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-dao.xml"})
public class SkRecordMapperTest {

    @Autowired
    private SkRecordMapper skRecordMapper;

    @Test
    public void queryBySkproductIdAndUserPhone() {
        long id = 1000L;
        long userPhone = 13476191877L;
        SkRecord skRecord = skRecordMapper.queryBySkproductIdAndUserPhone(id, userPhone);
        System.out.println(skRecord);
    }

    @Test
    public void insert() {
        long id=1000;
        long userPhone=13476191877L;
        System.out.println(skRecordMapper.insert(id,userPhone));
    }
}
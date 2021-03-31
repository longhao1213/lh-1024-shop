package lh.shop.biz;

import com.lh.UserApplication;
import com.lh.model.AddressDO;
import com.lh.service.AddressService;
import com.lh.vo.AddressVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: AddressTest.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/02/04 15:58
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApplication.class)
@Slf4j
public class AddressTest {

    @Autowired
    private AddressService addressService;

    @Test
    public void testAddressDetail() {
        AddressVO addressVO = addressService.detail(1l);
        log.info(addressVO.toString());
        Assert.assertNotNull(addressVO);

    }
}
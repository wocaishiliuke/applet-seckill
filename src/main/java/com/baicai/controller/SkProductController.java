package com.baicai.controller;

import com.baicai.enums.SeckillStatusEnum;
import com.baicai.exception.SeckillClosedException;
import com.baicai.exception.SeckillRepeatedException;
import com.baicai.pojo.SkProduct;
import com.baicai.pojo.dto.CommonResult;
import com.baicai.pojo.dto.ExposeResult;
import com.baicai.pojo.dto.SeckillResult;
import com.baicai.service.SkProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description 秒杀商品接口
 * @Author yuzhou
 * @Date 19-5-20
 */
@Controller
@RequestMapping("skProduct")
public class SkProductController {

    @Autowired
    private SkProductService skProductService;
    
    /**
     * 秒杀商品列表
     * @param model
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list(Model model) {
        List<SkProduct> skProductList = skProductService.getPageList(0, 4);
        model.addAttribute("list", skProductList);
        return "list";
    }
    
    /**
     * 秒杀商品详情
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model model) {
        if (id == null) {
            return "redirect:/skProduct/list";
        }
        SkProduct skProduct = skProductService.getById(id);
        if (skProduct == null) {
            return "forward:/skProduct/list";
        }
        model.addAttribute("skProduct", skProduct);
        return "detail";
    }
    
    /**
     * 暴露秒杀接口
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "expose/{id}", method = RequestMethod.GET)
    public CommonResult<ExposeResult> expose(@PathVariable("id") Long id) {
        CommonResult<ExposeResult> result;
        try {
            ExposeResult exposeResult = skProductService.expose(id);
            result = new CommonResult<>(true, exposeResult);
        } catch (Exception e) {
            e.printStackTrace();
            result = new CommonResult<>(false, e.getMessage());
        }
        return result;
    }
    
    /**
     * 秒杀
     * @param id
     * @param md5
     * @param phone
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "seckill/{id}/{md5}", method = RequestMethod.POST)
    public CommonResult<SeckillResult> seckill(@PathVariable("id") Long id, @PathVariable("md5") String md5,
                                               @CookieValue(value = "phone", required = false) Long phone) {
        if(phone == null) {
            return new CommonResult<>(false, "没有手机号");
        }
        // 根据用户的手机号码,秒杀商品的id跟md5进行秒杀商品,没异常就是秒杀成功
        try {
            // 这里换成储存过程
            SeckillResult seckillResult = skProductService.seckill(id, phone, md5);
            return new CommonResult<>(true, seckillResult);
        } catch (SeckillRepeatedException e1) {
            // 重复秒杀
            return new CommonResult<>(false, new SeckillResult(id, SeckillStatusEnum.REPEAT_KILL));
        } catch (SeckillClosedException e2) {
            // 秒杀关闭
            return new CommonResult<>(false, new SeckillResult(id, SeckillStatusEnum.END));
        } catch (Exception e) {
            // 不能判断的异常，包括SeckillException
            return new CommonResult<>(false, new SeckillResult(id, SeckillStatusEnum.INNER_ERROR));
        }
    }

    /**
     * 获取服务器端时间，防止用户篡改客户端时间提前参与秒杀
     * @return
     */
    @RequestMapping(value = "time/now", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<LocalDateTime> time() {
        return new CommonResult<>(true, LocalDateTime.now());
    }
}

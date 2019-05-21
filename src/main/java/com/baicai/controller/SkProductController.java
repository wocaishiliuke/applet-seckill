package com.baicai.controller;

import com.baicai.enums.SeckillStatusEnum;
import com.baicai.exception.SeckillClosedException;
import com.baicai.exception.SeckillNoPhoneException;
import com.baicai.exception.SeckillRepeatedException;
import com.baicai.exception.SeckillRewriteException;
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
    @GetMapping("list")
    public String list(Model model) {
        // TODO 分页：offset为页码，limit为每页条数，应有页面传来
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
        if (id == null)
            return "redirect:/skProduct/list";

        SkProduct skProduct = skProductService.getById(id);
        if (skProduct == null)
            return "forward:/skProduct/list";

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
     * TODO 异常统一处理（注意处理后的返回数据需要参数id）
     *
     * @param id
     * @param md5
     * @param userPhone 前端保存在Cookie
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "seckill/{id}/{md5}", method = RequestMethod.POST)
    public CommonResult<SeckillResult> seckill(@PathVariable("id") Long id, @PathVariable("md5") String md5,
                                               @CookieValue(value = "userPhone", required = false) Long userPhone) {
        try {
            // 这里换成储存过程
            //SeckillResult seckillResult = skProductService.seckill(id, userPhone, md5);
            SeckillResult seckillResult = skProductService.seckillByProcedure(id, userPhone, md5);
            return new CommonResult<>(true, seckillResult);
        } catch (SeckillNoPhoneException e1) {
            // 缺少手机号
            return new CommonResult<>(false, new SeckillResult(id, SeckillStatusEnum.NO_PHONE));
        } catch (SeckillRewriteException e2) {
            // 秒杀被篡改
            return new CommonResult<>(false, new SeckillResult(id, SeckillStatusEnum.DATE_REWRITE));
        } catch (SeckillClosedException e3) {
            // 秒杀关闭
            return new CommonResult<>(false, new SeckillResult(id, SeckillStatusEnum.CLOSED));
        } catch (SeckillRepeatedException e4) {
            // 重复秒杀
            return new CommonResult<>(false, new SeckillResult(id, SeckillStatusEnum.REPEAT_KILL));
        }
    }

    /**
     * 获取服务器端时间，防止用户篡改客户端时间提前参与秒杀
     * @return
     */
    @RequestMapping(value = "now", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<LocalDateTime> now() {
        return new CommonResult<>(true, LocalDateTime.now());
    }
}

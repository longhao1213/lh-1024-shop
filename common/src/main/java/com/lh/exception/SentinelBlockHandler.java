package com.lh.exception;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.lh.enums.BizCodeEnum;
import com.lh.utils.CommonUtil;
import com.lh.utils.JsonData;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

@Component
public class SentinelBlockHandler implements BlockExceptionHandler {


    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {

        JsonData jsonData = null;

        if(e instanceof FlowException){
            jsonData = JsonData.buildResult(BizCodeEnum.CONTROL_FLOW);

        }else if(e instanceof DegradeException){

            jsonData = JsonData.buildResult(BizCodeEnum.CONTROL_DEGRADE);

        } else if(e instanceof AuthorityException){

            jsonData = JsonData.buildResult(BizCodeEnum.CONTROL_AUTH);
        }

        httpServletResponse.setStatus(200);

        CommonUtil.sendJsonMessage(httpServletResponse,jsonData);

    }
}

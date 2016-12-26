package com.linda.rpc.webui.controller;

import com.linda.rpc.webui.biz.AppService;
import com.linda.rpc.webui.biz.HostService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * Created by lin on 2016/12/26.
 */
@Controller
public class WeightController {

    @Resource
    private AppService appService;

    @Resource
    private HostService hostService;



}

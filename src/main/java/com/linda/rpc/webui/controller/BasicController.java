package com.linda.rpc.webui.controller;

import com.linda.rpc.webui.biz.AppService;
import com.linda.rpc.webui.pojo.AppInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lin on 2016/12/23.
 */
@Controller
public class BasicController {

    @Resource
    private AppService appService;


    public void setApps(ModelMap model){
        List<AppInfo> appList = appService.getAppList();
        model.put("apps",appList);
    }


}

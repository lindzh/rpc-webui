package com.linda.rpc.webui.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.rpc.webui.biz.AppService;
import com.linda.rpc.webui.biz.LimitService;
import com.linda.rpc.webui.pojo.AppInfo;
import com.linda.rpc.webui.pojo.LimitInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2017/2/15.
 */
@Controller
@RequestMapping(value="/limit")
public class LimitController {

    @Resource
    private LimitService limitService;

    @Resource
    private AppService appService;

    /**
     * 限流列表
     * @param limit
     * @param offset
     * @param model
     * @return
     */
    @RequestMapping(value="/list",method = RequestMethod.GET)
    public String limitIndex(@RequestParam(value="limit",required = false,defaultValue = "50") int limit,
                       @RequestParam(value="offset",required = false,defaultValue = "0") int offset,ModelMap model){
        List<AppInfo> appList = appService.getAppList();
        model.put("appes",appList);
        model.put("limit",limit);
        model.put("offset",offset);
        return "limit_list";
    }

    /**
     * 限流详情
     * @param appId
     * @param model
     * @return
     */
    @RequestMapping(value="/detail",method = RequestMethod.GET)
    public String limitDetail( @RequestParam(value="appId")long appId,ModelMap model){
        AppInfo app = appService.getById(appId);
        List<AppInfo> allApps = appService.getAppList();
        List<LimitInfo> limits = limitService.getListByAppId(appId, 1000, 0);
        model.put("app",app);
        model.put("limits",limits);
        model.put("apps",allApps);
        return "limit_detail";
    }

    /**
     * 限流提交
     * @param appId
     * @param data
     * @param model
     * @return
     */
    @RequestMapping(value="/edit/{appId}",method = RequestMethod.POST)
    public String editSubmit(@PathVariable(value="appId")long appId, @RequestParam("data") String data, ModelMap model){
        List<LimitInfo> limitInfos = JSONUtils.fromJSON(data, new TypeReference<List<LimitInfo>>() {});
        AppInfo app = appService.getById(appId);
        if(app!=null){
            limitService.updateLimits(limitInfos,app);
            return "redirect:/limit/detail?appId="+appId;
        }else{
            return "redirect:/limit/list";
        }
    }
}

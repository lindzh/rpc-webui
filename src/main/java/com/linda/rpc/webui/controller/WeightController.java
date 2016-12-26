package com.linda.rpc.webui.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.rpc.webui.biz.AppService;
import com.linda.rpc.webui.biz.HostService;
import com.linda.rpc.webui.pojo.AppInfo;
import com.linda.rpc.webui.pojo.HostInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lin on 2016/12/26.
 */
@Controller
public class WeightController {

    @Resource
    private AppService appService;

    @Resource
    private HostService hostService;

    /**
     * 权重编辑页面
     * @param appId
     * @param model
     * @return
     */
    @RequestMapping(value="weight/{appId}",method = RequestMethod.GET)
    public String editWeightPage(@PathVariable("appId") long appId, ModelMap model){
        AppInfo app = appService.getById(appId);
        if(app!=null){
            model.put("app",app);

            List<HostInfo> hosts = hostService.getListByAppId(appId);
            model.put("hosts",hosts);
            model.put("hostCount",hosts.size());
        }
        return "weight_edit";
    }

    /**
     * 权重修改提交
     * @param appId
     * @param body
     * @param model
     * @return
     */
    @RequestMapping(value="weight/{appId}",method = RequestMethod.POST)
    public String weightEditSubmmit(@PathVariable("appId") long appId, @RequestBody String body, ModelMap model){
        List<HostInfo> hosts = JSONUtils.fromJSON(body, new TypeReference<List<HostInfo>>() {});
        for(HostInfo info:hosts){
            HostInfo hostInfo = hostService.getById(info.getId(), false);
            if(hostInfo==null){
                continue;
            }
            if(hostInfo.getAppId()!=appId){
                continue;
            }
            if(hostInfo.getWantWeight()==info.getWantWeight()){
                continue;
            }
            hostInfo.setWantWeight(info.getWantWeight());
            hostService.updateHost(hostInfo);
        }
        return "redirect:/host/list?appId="+appId;
    }

}

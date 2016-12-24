package com.linda.rpc.webui.controller;

import com.linda.rpc.webui.biz.AppService;
import com.linda.rpc.webui.biz.HostService;
import com.linda.rpc.webui.biz.ServiceInfoService;
import com.linda.rpc.webui.pojo.AppInfo;
import com.linda.rpc.webui.pojo.HostInfo;
import com.linda.rpc.webui.pojo.ServiceInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2016/12/23.
 */
@Controller
public class ServiceController extends BasicController{

    @Resource
    private ServiceInfoService serviceInfoService;

    @Resource
    private AppService appService;

    @Resource
    private HostService hostService;

    /**
     * 首页服务列表
     * @param keyword
     * @param appId
     * @param limit
     * @param offset
     * @param model
     * @return
     */
    @RequestMapping(value="service/list",method = RequestMethod.GET)
    public String serviceList(@RequestParam("keyword") String keyword, @RequestParam("appId") long appId,
                              @RequestParam("limit") int limit, @RequestParam("offset") int offset, ModelMap model){

        long total = serviceInfoService.getCountByKeywordAndAppId(keyword,  appId);
        if(total>0){
            List<ServiceInfo> services = serviceInfoService.getListByKeywordAndAppId(keyword, appId, limit, offset);
            model.put("services",services);
        }else{
            model.put("services",new ArrayList<ServiceInfo>());
        }

        this.setApps(model);

        model.put("total",total);
        model.put("keyword",keyword);
        model.put("appId",appId);
        model.put("limit",limit);
        model.put("offset",offset);
        return "service_list";
    }

    /**
     * 服务详情页面,展示服务提供者列表和消费者列表
     * @param serviceId
     * @param model
     * @return
     */
    @RequestMapping(value="/service/detail",method = RequestMethod.GET)
    public String serviceDetail(@RequestParam("serviceId") long serviceId,ModelMap model){
        ServiceInfo info = serviceInfoService.getById(serviceId,true);
        if(info!=null){
            model.put("info",info);
            List<HostInfo> providers = hostService.getProviderListByServiceId(serviceId);
            model.put("providers",providers);
            List<HostInfo> consumers = hostService.getConsumerListByServiceId(serviceId);
            model.put("consumers",consumers);
        }
        this.setApps(model);
        return "service_detail";
    }


}

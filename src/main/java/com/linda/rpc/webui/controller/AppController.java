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
import java.util.List;

/**
 * Created by lin on 2016/12/23.
 */
@Controller
public class AppController extends BasicController{

    @Resource
    private AppService appService;

    @Resource
    private ServiceInfoService serviceInfoService;

    @Resource
    private HostService hostService;

    @RequestMapping(value="/app/info",method = RequestMethod.GET)
    public String appInfo(@RequestParam("appId") long appId, ModelMap model){

        AppInfo app = appService.getById(appId);
        if(app!=null){
            model.put("app",app);

            List<HostInfo> providers = hostService.getProviderListByAppId(appId);
            //提供者列表
            model.put("providers",providers);

            //消费者列表????不是机器哪个应用消费了哪个服务???
            List<HostInfo> consumers = hostService.getConsumerListByAppId(appId);
            hostService.setApps(consumers);
            model.put("consumers",consumers);

            //提供服务列表
            List<ServiceInfo> services = serviceInfoService.getListByAppId(appId);
            model.put("provideServices",services);

            //依赖消费服务列表
            List<ServiceInfo> consumeServices = serviceInfoService.getConsumeServicesByAppId(appId);
            model.put("consumeServices",consumeServices);
        }
        this.setApps(model);
        return "app_detail";
    }

}

package com.linda.rpc.webui.utils;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.HostWeight;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.rpc.webui.pojo.AppInfo;
import com.linda.rpc.webui.pojo.HostInfo;
import com.linda.rpc.webui.pojo.ServiceInfo;

/**
 * Created by lin on 2016/12/16.
 */
public class ConUtils {

    public static AppInfo buildApp(String name){
        AppInfo info = new AppInfo();
        info.setName(name);
        return info;
    }

    public static ServiceInfo convertService(RpcService service,long appId){
        ServiceInfo info = new ServiceInfo();
        info.setName(service.getName());
        info.setGroup(service.getGroup());
        info.setVersion(service.getVersion());
        info.setImpl(service.getImpl());
        info.setAppId(appId);
        return info;
    }

    public static HostInfo buildHostInfo(RpcHostAndPort hostAndPort,long appId){
        HostInfo info = new HostInfo();
        info.setAppId(appId);
        info.setHost(hostAndPort.getHost());
        info.setPort(hostAndPort.getPort());
        info.setTime(System.currentTimeMillis());
        info.setStatus(0);
        info.setToken(hostAndPort.getToken());
        info.setWeight(hostAndPort.getWeight());
        return info;
    }

    public static HostInfo buildHostInfo(HostWeight hw,long appId){
        HostInfo info = new HostInfo();
        info.setAppId(appId);
        info.setHost(hw.getHost());
        info.setPort(hw.getPort());
        info.setTime(System.currentTimeMillis());
        info.setToken("");
        info.setStatus(0);
        info.setWeight(hw.getWeight());
        return info;
    }



}

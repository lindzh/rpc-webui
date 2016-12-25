package com.linda.rpc.webui.utils;

import com.linda.rpc.webui.pojo.AppInfo;
import com.linda.rpc.webui.pojo.HostInfo;

import java.util.List;

/**
 * Created by lin on 2016/12/25.
 */
public class DTOUtils {

    public static List<AppInfo> groupByApp(List<HostInfo> hosts){
        for(HostInfo host:hosts){
            host.getApp().getHosts().add(host);
        }
        List<AppInfo> apps = CollectionUtils.collect(hosts, "app", AppInfo.class);
        for(HostInfo host:hosts){
            host.setApp(null);
        }
        return apps;
    }
}

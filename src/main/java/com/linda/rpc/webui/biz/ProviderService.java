package com.linda.rpc.webui.biz;

import com.linda.framework.rpc.RpcService;

import java.util.List;

/**
 * Created by lin on 2016/12/16.
 */
public class ProviderService {

    private ServiceInfoService serviceInfoService;

    public void addOrUpdate(List<RpcService> service, long appId, long hostId){
        serviceInfoService.addOrupdateService(service,appId);
    }

    public void clearServices(long appId,long hostId){

    }
}

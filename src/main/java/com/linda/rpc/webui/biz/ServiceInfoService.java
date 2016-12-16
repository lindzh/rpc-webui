package com.linda.rpc.webui.biz;

import com.linda.framework.rpc.RpcService;
import com.linda.rpc.webui.dao.ServiceInfoDao;
import com.linda.rpc.webui.pojo.ServiceInfo;

import java.util.List;

/**
 * Created by lin on 2016/12/16.
 */
public class ServiceInfoService {

    private ServiceInfoDao serviceInfoDao;

    private ProviderService providerService;

    private ConsumerService consumerService;

    public List<ServiceInfo> addOrupdateService(List<RpcService> services, long appId){
        return null;
    }

    public void updateProviderCount(){

    }

    public void updateServiceStatus(){

    }

    public void updateConsumerCount(){

    }

}

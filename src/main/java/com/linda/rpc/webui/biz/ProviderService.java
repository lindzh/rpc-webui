package com.linda.rpc.webui.biz;

import com.linda.framework.rpc.RpcService;
import com.linda.rpc.webui.dao.ServiceProviderInfoDao;
import com.linda.rpc.webui.pojo.ServiceInfo;
import com.linda.rpc.webui.pojo.ServiceProviderInfo;

import java.util.List;

/**
 * Created by lin on 2016/12/16.
 */
public class ProviderService {

    private ServiceInfoService serviceInfoService;

    private ServiceProviderInfoDao serviceProviderInfoDao;

    public void addOrUpdate(List<RpcService> service, long appId, long hostId){
        List<ServiceInfo> infos = serviceInfoService.addOrupdateService(service, appId);
        for(ServiceInfo info:infos){
            ServiceProviderInfo providerInfo = serviceProviderInfoDao.getByAppHostAndServiceId(appId, hostId, info.getId());
            if(providerInfo==null){
                ServiceProviderInfo providerInfo1 = new ServiceProviderInfo();
                providerInfo1.setServiceId(info.getId());
                providerInfo1.setTime(System.currentTimeMillis());
                providerInfo1.setAppId(appId);
                providerInfo1.setHostId(hostId);
                serviceProviderInfoDao.addServiceProviderInfo(providerInfo1);
            }
        }
    }

    public void clearServices(long appId,long hostId){
        serviceProviderInfoDao.deleteByAppIdAndHostId(appId,hostId);
    }

    public int getServiceProviderCount(long appId,long ServiceId){
        return serviceProviderInfoDao.getServiceProviderCount(appId, ServiceId);
    }
}

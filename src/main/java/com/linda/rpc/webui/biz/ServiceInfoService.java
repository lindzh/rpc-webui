package com.linda.rpc.webui.biz;

import com.linda.framework.rpc.RpcService;
import com.linda.rpc.webui.dao.ServiceInfoDao;
import com.linda.rpc.webui.pojo.ServiceInfo;
import com.linda.rpc.webui.utils.ConUtils;
import com.linda.rpc.webui.utils.Const;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2016/12/16.
 */
@Service
public class ServiceInfoService {

    @Resource
    private ServiceInfoDao serviceInfoDao;

    @Resource
    private ProviderService providerService;

    @Resource
    private ConsumerService consumerService;

    /**
     * 查询,不存在就添加
     * @param services
     * @param appId
     * @return
     */
    public List<ServiceInfo> addOrupdateService(List<RpcService> services, long appId){
        ArrayList<ServiceInfo> infos = new ArrayList<ServiceInfo>();
        if(services!=null){
            for(RpcService service:services){
                ServiceInfo info = serviceInfoDao.getByAppIdGroupNameVersion(appId, service.getGroup(), service.getName(), service.getVersion());
                if(info==null){
                   info = ConUtils.convertService(service,appId);
                    info.setStatus(Const.SERVICE_OK);
                    info.setProviderCount(1);
                    serviceInfoDao.addServiceInfo(info);
                }
                infos.add(info);
            }
        }
        return infos;
    }

    public void updateProviderCount(){
        serviceInfoDao.updateProviderCount();
    }

    public void updateServiceStatus(){
        serviceInfoDao.updateServiceStatus();
    }

    public void updateConsumerCount(){
        serviceInfoDao.updateConsumerCount();
    }

    /**
     * 获取service列表
     * @param appId
     * @return
     */
    public List<ServiceInfo> getListByAppId(long appId){
        return serviceInfoDao.getListByAppIdAndStatus(appId,Const.SERVICE_ALL,10000,0);
    }

}

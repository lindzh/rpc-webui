package com.linda.rpc.webui.manager;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.ConsumeRpcObject;
import com.linda.framework.rpc.cluster.HostWeight;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.cluster.admin.RpcAdminService;
import com.linda.rpc.webui.biz.*;
import com.linda.rpc.webui.pojo.AppInfo;
import com.linda.rpc.webui.pojo.HostInfo;
import com.linda.rpc.webui.pojo.ServiceInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lin on 2016/12/16.
 */
@Service("managerService")
public class ManagerService {

    @Resource
    private RpcAdminService adminService;

    @Resource
    private AppService appService;

    @Resource
    private HostService hostService;

    @Resource
    private ServiceInfoService serviceInfoService;

    @Resource
    private ConsumerService consumerService;

    @Resource
    private ProviderService providerService;


    /**
     * 定时任务执行
     */
    public void doFetch(){

        List<RpcHostAndPort> rpcServers = adminService.getRpcServers();

        hostService.updateServersOff();
        hostService.updateServerOn(rpcServers);

        List<HostInfo> offServers = hostService.getOffServers();

        this.doOffServers(offServers);

        //在线机器的更新
        for(RpcHostAndPort hostAndPort:rpcServers){

            List<RpcService> rpcServices = adminService.getRpcServices(hostAndPort);

            if(rpcServices.size()>0){

                String application = rpcServices.get(0).getApplication();

                /**
                 * 应用更新与添加
                 */
                AppInfo appInfo = appService.getOrAddApp(application);

                /**
                 * 机器添加
                 */
                HostInfo hostInfo = hostService.getOrAdd(hostAndPort, appInfo.getId());

                /**
                 * 机器提供的服务添加
                 */
                List<ServiceInfo> infos = this.doUpdateProviderServices(rpcServices, appInfo.getId(), hostInfo.getId());

                /**
                 * 消费者列表
                 */
                this.fetchConsumers(infos,appInfo.getId());

                /**
                 * 权重
                 */
                this.doFetchHostWeights(appInfo);

                /**
                 * 同步权重
                 */
                this.syncWeights();
            }
        }

        //更新服务提供者与消费者数量以及服务状态
        this.doUpdateServiceCountAndStatus();
        //权重更新
        this.updateWeights();
    }

    /**
     * 更新服务提供者列表
     * @param services
     * @param appId
     * @param hostId
     */
    public List<ServiceInfo> doUpdateProviderServices(List<RpcService> services, long appId, long hostId){
        //先清除,再添加
        providerService.clearServices(appId,hostId);
        return providerService.addOrUpdate(services, appId, hostId);
    }

    /**
     * 获取权重列表
     * @param appInfo
     */
    public void doFetchHostWeights(AppInfo appInfo){
        //权重没必要清除旧的,统一更新
        List<HostWeight> weights = adminService.getWeights(appInfo.getName());
        for(HostWeight hw:weights){
            hostService.getOrAdd(hw,appInfo.getId());
        }
    }

    /**
     * 获取消费者列表
     * @param services
     * @param appId
     */
    public void fetchConsumers(List<ServiceInfo> services,long appId){
        //先清除老的,再添加新的。避免重复和状态不对的
        for(ServiceInfo info:services){
            consumerService.clearConsumers(appId, info.getId());
            List<ConsumeRpcObject> consumers = adminService.getConsumers(info.getGroup(), info.getName(), info.getVersion());
            for(ConsumeRpcObject consumer:consumers){
                consumerService.addOrUpdate(consumer,appId,info.getId());
            }
        }
    }

    /**
     * 消费者消费状态,更新,服务提供者服务更新
     * @param offServers
     */
    public void doOffServers(List<HostInfo> offServers){
        for(HostInfo server:offServers){
            consumerService.clearConsumers(server.getAppId(),server.getId());
            providerService.clearServices(server.getAppId(),server.getId());
        }
        this.doUpdateServiceCountAndStatus();
    }

    /**
     * 更新服务状态
     */
    public void doUpdateServiceCountAndStatus(){
        //更新服务的提供者数量,消费者数量,以及状态
        serviceInfoService.updateProviderCount();
        serviceInfoService.updateServiceStatus();
        serviceInfoService.updateConsumerCount();
    }

    /**
     * 机器权重更新
     */
    public void updateWeights(){
        List<AppInfo> list = appService.getAppList();
        for(AppInfo app:list){
            this.doFetchHostWeights(app);
        }
    }

    public void syncWeights(){
        List<HostInfo> needSyncList = hostService.getNeedSyncList();
        for(HostInfo host:needSyncList){
            AppInfo info = appService.getById(host.getId());
            HostWeight weight = new HostWeight();
            weight.setHost(host.getHost());
            weight.setPort(host.getPort());
            weight.setWeight((int)host.getWantWeight());
            adminService.setWeight(info.getName(),weight);
        }
    }
}

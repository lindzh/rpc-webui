package com.linda.rpc.webui.biz;

import com.linda.framework.rpc.cluster.HostWeight;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.rpc.webui.dao.HostInfoDao;
import com.linda.rpc.webui.pojo.HostInfo;
import com.linda.rpc.webui.utils.ConUtils;
import com.linda.rpc.webui.utils.Const;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lin on 2016/12/16.
 */
@Service
public class HostService {

    @Resource
    private HostInfoDao hostInfoDao;

    public HostInfo getOrAdd(RpcHostAndPort hostAndPort,long appId){
        HostInfo info = this.getOrAddHost(appId, hostAndPort.getHost());
        if(info!=null){
            //机器在线
            info.setPort(hostAndPort.getPort());
            info.setStatus(Const.HOST_STATUS_ON);
            info.setTime(System.currentTimeMillis());
            info.setToken(hostAndPort.getToken());
            //权重不更新
            hostInfoDao.updateById(info);
            return info;
        }else{
            HostInfo hostInfo = ConUtils.buildHostInfo(hostAndPort, appId);
            hostInfo.setStatus(Const.HOST_STATUS_ON);
            hostInfoDao.addHostInfo(hostInfo);
            if(hostInfo.getId()>0){
                return hostInfo;
            }else{
                return null;
            }
        }
    }

    /**
     * 通过权重获取机器
     * @param hw
     * @param appId
     * @return
     */
    public HostInfo getOrAdd(HostWeight hw,long appId){
        HostInfo info = this.getOrAddHost(appId, hw.getHost());
        if(info!=null){
            info.setPort(hw.getPort());
            info.setWeight(hw.getWeight());
            //权重更新,不代表机器在不在线
            return info;
        }else{
            HostInfo hostInfo = ConUtils.buildHostInfo(hw, appId);
            hostInfo.setStatus(Const.HOST_STATUS_OFF);
            hostInfo.setToken("null");
            hostInfo.setTime(System.currentTimeMillis());
            hostInfoDao.addHostInfo(hostInfo);
            if(hostInfo.getId()>0){
                return hostInfo;
            }else{
                return null;
            }
        }
    }

    public HostInfo getOrAddHost(long appId,String ip){
        HostInfo appAndIp = hostInfoDao.getByAppAndIp(appId, ip);
        if(appAndIp!=null){
            return appAndIp;
        }else{
            HostInfo info = new HostInfo();
            info.setAppId(appId);
            info.setHost(ip);
            info.setPort(666);
            info.setTime(System.currentTimeMillis());
            info.setStatus(Const.HOST_STATUS_OFF);
            info.setToken("null");
            info.setWeight(100);
            info.setWantWeight(100);
            hostInfoDao.addHostInfo(info);
            if(info.getId()>0){
                return info;
            }else{
                return null;
            }
        }
    }

    /**
     * 批量上线
     * @param hosts
     */
    public void updateServerOn(List<RpcHostAndPort> hosts){
        for(RpcHostAndPort host:hosts){
            List<HostInfo> infos = hostInfoDao.getListByHostAndPort(host.getHost(), host.getPort());
            for(HostInfo info:infos){
                info.setStatus(Const.HOST_STATUS_ON);
                hostInfoDao.updateById(info);
            }
        }
    }

    public void updateServersOff(){
        hostInfoDao.updateStatus(Const.HOST_STATUS_OFF);
    }

    public List<HostInfo> getOffServers(){
        return hostInfoDao.getListByStatus(Const.HOST_STATUS_OFF);
    }

    public List<HostInfo> getNeedSyncList(){
        return hostInfoDao.getNeedSyncList();
    }

}

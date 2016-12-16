package com.linda.rpc.webui.biz;

import com.linda.framework.rpc.cluster.HostWeight;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.rpc.webui.pojo.HostInfo;

import java.util.List;

/**
 * Created by lin on 2016/12/16.
 */
public class HostService {

    public HostInfo getOrAdd(RpcHostAndPort hostAndPort,long appId){
        return null;
    }

    public HostInfo getOrAdd(HostWeight hw,long appId){
        return null;
    }

    public void updateServerOn(List<RpcHostAndPort> hosts){

    }

    public void updateServersOff(){

    }

    public List<HostInfo> getOffServers(){
        return null;
    }


}

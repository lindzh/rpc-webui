package com.linda.rpc.webui.service;

import java.util.List;
import java.util.Map;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.RpcHostAndPort;

public interface RpcInfoListener {
	
	public void onServers(RpcConfig config,List<RpcHostAndPort> host);
	
	public void onServices(RpcConfig config,Map<RpcHostAndPort,List<RpcService>> hostServices);

}

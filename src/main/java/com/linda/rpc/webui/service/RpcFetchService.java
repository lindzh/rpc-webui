package com.linda.rpc.webui.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.Service;
import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.framework.rpc.cluster.admin.RpcAdminService;
import com.linda.framework.rpc.cluster.admin.SimpleRpcAdminService;
import com.linda.framework.rpc.cluster.etcd.EtcdRpcAdminService;
import com.linda.framework.rpc.cluster.redis.RedisRpcAdminService;
import com.linda.framework.rpc.cluster.zk.ZkRpcAdminService;
import com.linda.framework.rpc.exception.RpcException;
import com.linda.rpc.webui.service.RpcConfig.RpcProtocol;

public class RpcFetchService implements Service{
	
	private List<RpcConfig> rpcConfiguration;
	
	private ConcurrentHashMap<RpcConfig, RpcAdminService> configAdminCache = new ConcurrentHashMap<RpcConfig, RpcAdminService>();
	
	private Logger logger = Logger.getLogger(RpcFetchService.class);

	private List<RpcInfoListener> infoListeners = new ArrayList<RpcInfoListener>();
	
	private Timer timer = new Timer();
	
	private long fetchInterval = 10000;//10s
	
	public void setRpcConfigs(List<RpcConfig> configs){
		this.rpcConfiguration = configs;
	}
	
	public List<RpcConfig> getRpcConfigs(){
		return this.rpcConfiguration;
	}
	
	private TimerTask fetchTask = new TimerTask(){
		public void run() {
			RpcFetchService.this.fetServerAndServices();
		}
	};
	
	public void addInfoListener(RpcInfoListener listener){
		this.infoListeners.add(listener);
	}
	
	private void fireServerListeners(RpcConfig config,List<RpcHostAndPort> host){
		for(RpcInfoListener listener:this.infoListeners){
			listener.onServers(config, host);
		}
	}
	
	private void fireServicesListeners(RpcConfig config,RpcHostAndPort host,List<RpcService> services){
		for(RpcInfoListener listener:this.infoListeners){
			listener.onServices(config, host, services);
		}
	}
	
	@Override
	public void startService() {
		this.startAdminService();
		this.startFetchTask();
	}
	
	private void startFetchTask(){
		timer.scheduleAtFixedRate(fetchTask, 100, fetchInterval);
	}
	
	private void fetServerAndServices(){
		Set<RpcConfig> configs = configAdminCache.keySet();
		for(RpcConfig config:configs){
			RpcAdminService adminService = configAdminCache.get(config);
			if(adminService==null){
				this.initConfig(config);
			}else{
				List<RpcHostAndPort> servers = adminService.getRpcServers();
				this.fireServerListeners(config, servers);
				for(RpcHostAndPort server:servers){
					List<RpcService> services = adminService.getRpcServices(server);
					this.fireServicesListeners(config, server, services);
				}
			}
		}
	}
	
	private void initConfig(RpcConfig config){
		try{
			String protocol = config.getProtocol();
			RpcProtocol rpcProtocol = RpcProtocol.getByName(protocol);
			if(rpcProtocol==RpcProtocol.SIMPLE){
				SimpleRpcAdminService adminService = new SimpleRpcAdminService();
				adminService.setHost(config.getProviderHost());
				adminService.setPort(config.getProviderPort());
				adminService.setNamespace(config.getNamespace());
				adminService.startService();
				configAdminCache.put(config, adminService);
			}else if(rpcProtocol==RpcProtocol.ETCD){
				EtcdRpcAdminService adminService = new EtcdRpcAdminService();
				adminService.setNamespace(config.getNamespace());
				adminService.setEtcdUrl(config.getEtcdUrl());
				adminService.startService();
				configAdminCache.put(config, adminService);
			}else if(rpcProtocol==RpcProtocol.REDIS){
				RedisRpcAdminService adminService = new RedisRpcAdminService();
				adminService.setNamespace(config.getNamespace());
				adminService.setRedisHost(config.getRedisHost());
				adminService.setRedisPort(config.getRedisPort());
				adminService.setRedisMasterName(config.getSentinelMaster());
				adminService.setRedisSentinels(config.getSentinels());
				adminService.startService();
				configAdminCache.put(config, adminService);
			}else if(rpcProtocol==RpcProtocol.ZOOKEEPER){
				ZkRpcAdminService adminService = new ZkRpcAdminService();
				adminService.setNamespace(config.getNamespace());
				adminService.setConnectString(config.getZkConnectionString());
				adminService.startService();
				configAdminCache.put(config, adminService);
			}
		}catch(Exception e){
			logger.error("start admin error:"+JSONUtils.toJSON(config));
		}
	}
	
	private void startAdminService(){
		if(rpcConfiguration!=null&&rpcConfiguration.size()>0){
			for(RpcConfig config:rpcConfiguration){
				this.initConfig(config);
			}
		}else{
			throw new RpcException("configration null,please check configuration");
		}
	}
	
	private void stopAdmin(){
		Set<RpcConfig> configs = configAdminCache.keySet();
		for(RpcConfig config:configs){
			RpcAdminService adminService = configAdminCache.get(config);
			if(adminService!=null){
				adminService.stopService();
			}
		}
	}
	
	@Override
	public void stopService() {
		this.stopAdmin();
		timer.cancel();
		infoListeners.clear();
		configAdminCache.clear();
	}
	
}

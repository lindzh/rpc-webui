package com.linda.rpc.webui.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.MD5Utils;
import com.linda.framework.rpc.cluster.RpcHostAndPort;


public class RpcWebuiServiceImpl implements RpcWebuiService,RpcInfoListener{
	
	private ConcurrentHashMap<String,RpcConfig> md5ConfigCache = new ConcurrentHashMap<String,RpcConfig>();
	
	private ConcurrentHashMap<String, Set<RpcService>> namespaceServices = new ConcurrentHashMap<String, Set<RpcService>>();
	
	private ConcurrentHashMap<String,Set<RpcHostAndPort>> namespaceServiceHosts = new ConcurrentHashMap<String,Set<RpcHostAndPort>>();
	
	private ConcurrentHashMap<String,List<RpcHostAndPort>> configProvidersCache = new ConcurrentHashMap<String,List<RpcHostAndPort>>();
	
	private ReadWriteLock readwriteLock = new ReentrantReadWriteLock(false);
	
	private ConcurrentHashMap<String,Set<RpcService>> namespaceHostServicesCache = new ConcurrentHashMap<String,Set<RpcService>>();
	
	@Override
	public void onServers(RpcConfig config, List<RpcHostAndPort> hosts) {
		Lock lock = readwriteLock.writeLock();
		try{
			lock.lock();
			String md5 = MD5Utils.md5(config.toString());
			config.setMd5(md5);
			md5ConfigCache.put(md5, config);
			configProvidersCache.put(md5, hosts);
		}finally{
			lock.unlock();
		}
	}

	@Override
	public void onServices(RpcConfig config, RpcHostAndPort host, List<RpcService> services) {
		Lock lock = readwriteLock.writeLock();
		try{
			lock.lock();
			//namespace services
			String md5 = MD5Utils.md5(config.toString());
			Set<RpcService> set = namespaceServices.get(md5);
			if(set==null){
				namespaceServices.put(md5, new HashSet<RpcService>());
				set = namespaceServices.get(md5);
			}
			set.addAll(services);
			
			//name space service host list
			for(RpcService service:services){
				String genKey = this.genKey(md5, service.getName(), service.getVersion());
				Set<RpcHostAndPort> hosts = namespaceServiceHosts.get(genKey);
				if(hosts==null){
					hosts = new HashSet<RpcHostAndPort>();
					namespaceServiceHosts.put(genKey, hosts);
				}
				hosts.add(host);
			}
			//host service key
			String servicesKey = this.genhostServicesKey(md5, host.getHost()+":"+host.getPort());
			namespaceHostServicesCache.put(servicesKey, set);
		}finally{
			lock.unlock();
		}
	}

	@Override
	public List<RpcService> search(String namespace, String keyword) {
		LinkedList<RpcService> result = new LinkedList<RpcService>();
		Lock lock = readwriteLock.readLock();
		try{
			lock.lock();
			Set<RpcService> services = namespaceServices.get(namespace);
			if(services!=null){
				for(RpcService service:services){
					if(keyword==null||keyword.length()<1){
						result.add(service);
					}else{
						String name = service.getName().toLowerCase();
						String key = keyword.toLowerCase();
						if(name.contains(key)){
							result.add(service);
						}
					}
				}
			}
		}finally{
			lock.unlock();
		}
		return result;
	}
	
	private String genhostServicesKey(String namespace,String hostAndPort){
		return MD5Utils.md5(namespace+"_"+hostAndPort);
	}

	@Override
	public List<RpcService> getServicesByHost(String namespace, String hostAndPort) {
		String servicesKey = this.genhostServicesKey(namespace, hostAndPort);
		List<RpcService> list = new ArrayList<RpcService>();
		Lock lock = readwriteLock.readLock();
		try{
			lock.lock();
			Set<RpcService> set = namespaceHostServicesCache.get(servicesKey);
			if(set!=null){
				list.addAll(set);
			}
		}finally{
			lock.unlock();
		}
		return list;
	}

	@Override
	public List<String> getNamespaces() {
		List<String> list = new ArrayList<String>();
		Lock lock = readwriteLock.readLock();
		try{
			lock.lock();
			Set<String> keys = md5ConfigCache.keySet();
			if(keys!=null){
				list.addAll(keys);
			}
		}finally{
			lock.unlock();
		}
		return list;
	}
	
	private String genKey(String namespace, String serviceName, String serviceVersion){
		return MD5Utils.md5(namespace+"_"+serviceName+"_"+serviceVersion);
	}

	@Override
	public List<RpcHostAndPort> getRpcHostsByRpc(String namespace, String serviceName, String serviceVersion) {
		ArrayList<RpcHostAndPort> hosts = new ArrayList<RpcHostAndPort>();
		String genKey = this.genKey(namespace, serviceName, serviceVersion);
		Lock lock = readwriteLock.readLock();
		try{
			lock.lock();
			Set<RpcHostAndPort> containHosts = namespaceServiceHosts.get(genKey);
			if(containHosts!=null){
				hosts.addAll(containHosts);
			}
		}finally{
			lock.unlock();
		}
		return hosts;
	}

	@Override
	public List<RpcHostAndPort> getHostsByNamespace(String namespace) {
		List<RpcHostAndPort> list = new ArrayList<RpcHostAndPort>();
		Lock lock = readwriteLock.readLock();
		try{
			lock.lock();
			List<RpcHostAndPort> hosts = configProvidersCache.get(namespace);
			if(hosts!=null){
				list.addAll(hosts);
			}
		}finally{
			lock.unlock();
		}
		return list;
	}

	@Override
	public RpcConfig getNamespaceConfig(String namespace) {
		return md5ConfigCache.get(namespace);
	}

	@Override
	public List<RpcConfig> getRpcConfigs() {
		List<RpcConfig> list = new ArrayList<RpcConfig>();
		Lock lock = readwriteLock.readLock();
		try{
			lock.lock();
			Collection<RpcConfig> configs = md5ConfigCache.values();
			if(configs!=null){
				list.addAll(configs);
			}
		}finally{
			lock.unlock();
		}
		return list;
	}
	
}

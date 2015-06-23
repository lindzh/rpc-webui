package com.linda.rpc.webui.servlet;

import java.util.List;
import java.util.Map;

import com.linda.framework.rpc.RpcService;
import com.linda.framework.rpc.cluster.RpcHostAndPort;
import com.linda.rpc.webui.service.RpcConfig;
import com.linda.rpc.webui.service.RpcWebuiService;

public class RpcWebuiController {
	
	private RpcWebuiService rpcWebuiService;
	
	public RpcWebuiService getRpcWebuiService() {
		return rpcWebuiService;
	}

	public void setRpcWebuiService(RpcWebuiService rpcWebuiService) {
		this.rpcWebuiService = rpcWebuiService;
	}

	private String chooseNamespaces(String namespace,Map<String,Object> model){
		List<String> namespaces = rpcWebuiService.getNamespaces();
		model.put("namespaces", namespaces);
		if((namespace==null||!namespaces.contains(namespace))&&namespaces.size()>0){
			namespace = namespaces.get(0);
		}
		RpcConfig namespaceConfig = rpcWebuiService.getNamespaceConfig(namespace);
		model.put("namespaceConfig", namespaceConfig);
		return namespace;
	}
	
	public String search(String namespace,String keyword,Map<String,Object> model){
		namespace = this.chooseNamespaces(namespace, model);
		List<RpcService> services = rpcWebuiService.search(namespace, keyword);
		model.put("services", services);
		model.put("keyword", keyword);
		model.put("namespace", namespace);
		return "services";
	}
	
	public String getHostsByService(String namespace,String serviceName,String serviceVersion,Map<String,Object> model){
		namespace = this.chooseNamespaces(namespace, model);
		List<RpcHostAndPort> hosts = rpcWebuiService.getRpcHostsByRpc(namespace, serviceName, serviceVersion);
		model.put("hosts", hosts);
		model.put("namespace", namespace);
		model.put("serviceName", serviceName);
		model.put("serviceVersion", serviceVersion);
		return "service_hosts";
	}
	
	public String getHostServices(String namespace,String hostAndPort,Map<String,Object> model){
		namespace = this.chooseNamespaces(namespace, model);
		List<RpcService> services = rpcWebuiService.getServicesByHost(namespace, hostAndPort);
		model.put("namespace", namespace);
		model.put("services", services);
		model.put("hostAndPort", hostAndPort);
		String[] hostPort = hostAndPort.split(":");
		model.put("host", hostPort[0]);
		model.put("port", hostPort[1]);
		return "host_services";
	}
	
	public String getNamespaceHosts(String namespace,Map<String,Object> model){
		namespace = this.chooseNamespaces(namespace, model);
		List<RpcHostAndPort> hosts = rpcWebuiService.getHostsByNamespace(namespace);
		model.put("hosts", hosts);
		model.put("namespace", namespace);
		return "hosts";
	}
}

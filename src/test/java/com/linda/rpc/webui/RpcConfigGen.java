package com.linda.rpc.webui;

import java.util.ArrayList;

import com.linda.framework.rpc.cluster.JSONUtils;
import com.linda.rpc.webui.service.RpcConfig;

public class RpcConfigGen {
	
	public static void main(String[] args) {
		ArrayList<RpcConfig> configs = new ArrayList<RpcConfig>();
		configs.add(new RpcConfig());
		System.out.println(JSONUtils.toJSON(configs));
	}

}

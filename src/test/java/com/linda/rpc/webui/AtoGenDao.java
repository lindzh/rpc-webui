package com.linda.rpc.webui;

import com.linda.common.mybatis.generator.bean.MybatisPojo;
import com.linda.common.mybatis.generator.processor.DefaultMybatisGenerator;
import com.linda.rpc.webui.pojo.*;

/**
 * Created by lin on 2016/12/15.
 */
public class AtoGenDao {

    public static void main(String[] args) {
        DefaultMybatisGenerator generator = new DefaultMybatisGenerator();
        generator.startService();
        MybatisPojo code = generator.genCode(ServiceProviderInfo.class, "com.linda.rpc.webui.dao", "/Users/lin/Work/java/rpc-webui/src/main/resources/sqlmap/", "/Users/lin/Work/java/rpc-webui/src/main/java/com/linda/rpc/webui/dao/");
        System.out.println("===========gen finished==================");
    }
}

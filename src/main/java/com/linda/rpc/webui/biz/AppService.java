package com.linda.rpc.webui.biz;

import com.linda.rpc.webui.dao.AppInfoDao;
import com.linda.rpc.webui.pojo.AppInfo;
import com.linda.rpc.webui.utils.ConUtils;

import java.util.List;

/**
 * Created by lin on 2016/12/16.
 */
public class AppService {

    private AppInfoDao appInfoDao;

    public AppInfo getOrAddApp(String app){
        AppInfo info = appInfoDao.getByName(app);
        if(info!=null){
            return info;
        }else{
            info = ConUtils.buildApp(app);
            appInfoDao.addAppInfo(info);
            if(info.getId()>0){
                return info;
            }
            return null;
        }
    }

    public List<AppInfo> getAppList(){
        return null;
    }

}

package com.linda.rpc.webui.biz;

import com.linda.rpc.webui.dao.LimitInfoDao;
import com.linda.rpc.webui.pojo.AppInfo;
import com.linda.rpc.webui.pojo.LimitInfo;
import com.linda.rpc.webui.utils.Const;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2017/2/15.
 */
@Service
public class LimitService {

    @Resource
    private AppService appService;

    @Resource
    private LimitInfoDao limitInfoDao;

    public List<LimitInfo> getListByAppId(long appId, int limit, int offset){
        return limitInfoDao.getListByAppId(appId,limit,offset);
    }

    /**
     * 更新限流信息
     * @param list
     * @param app
     */
    public void updateLimits(List<LimitInfo> list, AppInfo app){
        long now = System.currentTimeMillis();
        for(LimitInfo info:list){
            info.setAppId(app.getId());
            info.setUpdateTime(now);
        }
        limitInfoDao.deleteByAppId(app.getId());
        limitInfoDao.batchAdd(list);
        app.setLimitCount(list.size());
        app.setLimitSyncStatus(Const.APP_LIMIT_SYNCED_NO);
        appService.updateApp(app);
    }
}

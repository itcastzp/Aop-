package org.cnbi.service.impl;

import org.apache.commons.beanutils.BeanUtils;
import org.cnbi.model.SysLogs;
import org.cnbi.mybatis.SysLogsMapper;
import org.cnbi.service.LogService;
import org.cnbi.utils.date.DateUtil;
import org.cnbi.utils.log.Log;
import org.cnbi.utils.log.LogBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by FangQiang on 2019/1/29
 */
@Service
public class LogServiceImpl implements LogService<Log> {

    @Autowired
    private SysLogsMapper sysLogsMapper;

    @Override
    public Log save(Log log) {
        // 写个转换吧！！
        System.out.println(log);
        SysLogs sysLog = parseLogToSysLog(log);
        sysLogsMapper.insertSelective(sysLog);
        return log;
    }

    @Override
    public Log saveException(Log log, Throwable throwable) {
        System.out.println(log);
        SysLogs sysLog = parseLogToSysLog(log);
        sysLogsMapper.insert(sysLog);
        if(throwable != null) {
            throwable.printStackTrace();
        }
        return log;
    }

    private SysLogs parseLogToSysLog(Log log) {
        SysLogs sysLog = SysLogs.builder()
                .nlevel(Long.parseLong(String.valueOf(log.getLevel())))
                .ntype(Long.parseLong(String.valueOf(log.getType())))
                .sbroswer(log.getBrowser())
                .scontent(log.getDescription())
                .shostip(log.getIp())
                .shostname(log.getIp())
                .soperatetime(DateUtil.transferLongToDate(log.getCreateTime(), null))
                .suser(log.getOperator())
                .build();
        return sysLog;
    }


    @Override
    public Log getLog(LogBean logBean, Map<String, String> otherInfo, Throwable throwable) {
        Log log = new Log();
        try {
            BeanUtils.copyProperties(log, logBean);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        log.setIp(otherInfo.get("ip"));
        log.setBrowser(otherInfo.get("browser"));
        log.setOperator(otherInfo.get("username"));
        log.setCreateTime(Calendar.getInstance().getTimeInMillis());
        if(throwable != null) {
            log.setExceptionClass(logBean.getClassName());
            log.setExceptionDetail(throwable.getMessage());
            log.setExecutedTime(logBean.getExecutedTime());
        }
        return log;
    }

}

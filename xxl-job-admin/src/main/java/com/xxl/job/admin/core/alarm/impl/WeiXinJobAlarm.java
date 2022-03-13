package com.xxl.job.admin.core.alarm.impl;

import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.model.ReturnT;

import lombok.extern.slf4j.Slf4j;

/**
 * job alarm by email
 *
 * @author xuxueli 2020-01-19
 */
@Component
@Slf4j
public class WeiXinJobAlarm implements JobAlarm {

    @Autowired
    private WeiXinRobotAdapter weiXinRobotAdapter;

    /**
     * fail alarm
     *
     * @param jobLog
     */
    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        boolean alarmResult = true;

        // send monitor email

        // alarmContent
        String alarmContent = "Alarm Job LogId=" + jobLog.getId();
        if (jobLog.getTriggerCode() != ReturnT.SUCCESS_CODE) {
            alarmContent += "<br>TriggerMsg=<br>" + jobLog.getTriggerMsg();
        }
        if (jobLog.getHandleCode() > 0 && jobLog.getHandleCode() != ReturnT.SUCCESS_CODE) {
            alarmContent += "<br>HandleCode=" + jobLog.getHandleMsg();
        }

        // email info
        XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(Integer.valueOf(info.getJobGroup()));
        String content = MessageFormat.format(loadEmailJobAlarmTemplate(), group != null ? group.getTitle() : "null", info.getId(), info.getJobDesc(),
                alarmContent);

        // make mail
        try {
            weiXinRobotAdapter.sendText(content);

        } catch (Exception e) {
            log.error(">>>>>>>>>>> xxl-job, job fail alarm email send error, JobLogId:{}", jobLog.getId(), e);

            alarmResult = false;
        }

        return alarmResult;
    }

    /**
     * load email job alarm template
     *
     * @return
     */
    private static final String loadEmailJobAlarmTemplate() {
        String mailBodyTemplate = "<h5>" + I18nUtil.getString("jobconf_monitor_detail") + "：</span>" + "<table border=\"1\" cellpadding=\"3\" " +
                "style=\"border-collapse:collapse; width:80%;\" >\n" + "   <thead style=\"font-weight: bold;color: #ffffff;background-color: #ff8c00;\" >" +
                "      <tr>\n" + "         <td width=\"20%\" >" + I18nUtil.getString("jobinfo_field_jobgroup") + "</td>\n" + "         <td width=\"10%\" >" + I18nUtil.getString("jobinfo_field_id") + "</td>\n" + "         <td width=\"20%\" >" + I18nUtil.getString("jobinfo_field_jobdesc") + "</td>\n" + "         <td width=\"10%\" >" + I18nUtil.getString("jobconf_monitor_alarm_title") + "</td>\n" + "         <td width=\"40%\" >" + I18nUtil.getString("jobconf_monitor_alarm_content") + "</td>\n" + "      </tr>\n" + "   </thead>\n" + "   <tbody>\n" + "      <tr>\n" + "         <td>{0}</td>\n" + "         <td>{1}</td>\n" + "         <td>{2}</td>\n" + "         <td>" + I18nUtil.getString("jobconf_monitor_alarm_type") + "</td>\n" + "         <td>{3}</td>\n" + "      </tr>\n" + "   </tbody>\n" + "</table>";

        return mailBodyTemplate;
    }

}

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
     */
    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        boolean alarmResult = true;

        // send monitor email

        // alarmContent
        String alarmContent = "Alarm Job LogId=" + jobLog.getId();
        if (jobLog.getTriggerCode() != ReturnT.SUCCESS_CODE) {
            alarmContent += "#### " + jobLog.getTriggerMsg();
        }
        if (jobLog.getHandleCode() > 0 && jobLog.getHandleCode() != ReturnT.SUCCESS_CODE) {
            alarmContent += "#### HandleCode=" + jobLog.getHandleMsg();
        }

        String mailBodyTemplate =
                "### " + I18nUtil.getString("jobconf_monitor_detail") + "### " + I18nUtil.getString("jobinfo_field_jobgroup") + "### " + I18nUtil.getString(
                        "jobinfo_field_id") + "### " + I18nUtil.getString("jobinfo_field_jobdesc") + "### " + I18nUtil.getString("jobconf_monitor_alarm_title"
                ) + "### " + I18nUtil.getString("jobconf_monitor_alarm_content") + "### {0} ### {1} ### {2}" + I18nUtil.getString("jobconf_monitor_alarm_type"
                ) + " ### {3}";
        // email info
        XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(info.getJobGroup());
        String content = MessageFormat.format(mailBodyTemplate, group != null ? group.getTitle() : "null", info.getId(), info.getJobDesc(), alarmContent);

        // make mail
        try {
            weiXinRobotAdapter.sendPureMarkdown(content);

        } catch (Exception e) {
            log.error(">>>>>>>>>>> xxl-job, job fail alarm email send error, JobLogId:{}", jobLog.getId(), e);

            alarmResult = false;
        }

        return alarmResult;
    }
}

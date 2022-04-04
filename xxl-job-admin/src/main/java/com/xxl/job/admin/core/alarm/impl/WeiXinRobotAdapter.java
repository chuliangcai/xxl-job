package com.xxl.job.admin.core.alarm.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.xxl.job.admin.core.util.DateTimeUtils;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 开发文档：https://developer.work.weixin.qq.com/document/path/91770
 *
 * @author chuyuancheng
 */
@Component
@Slf4j
public class WeiXinRobotAdapter {

    @Autowired
    private RestTemplate restTemplate;

    @Getter
    private enum MsgType {
        /**
         * 文本
         */
        TEXT("text"),
        /**
         * 图片
         */
        IMAGE("image"),
        /**
         * markdown
         */
        MARKDOWN("markdown");
        private final String value;

        MsgType(String value) {
            this.value = value;
        }
    }

    @Data
    private static class RequestObj {

        @SuppressWarnings("SpellCheckingInspection")
        private String msgtype;
        private RequestContent text;

        public static RequestObj createTextRequest(String contentText) {
            RequestObj requestObj = new RequestObj();
            requestObj.setMsgtype(MsgType.TEXT.value);
            RequestContent content = new RequestContent();
            content.setContent(contentText);
            requestObj.setText(content);
            return requestObj;
        }

        public static RequestObj createMarkdownRequest(String contentText) {
            RequestObj requestObj = new RequestObj();
            requestObj.setMsgtype(MsgType.MARKDOWN.value);
            RequestContent content = new RequestContent();
            content.setContent(contentText);
            requestObj.setText(content);
            return requestObj;
        }
    }

    @Data
    private static class RequestContent {
        private String content;
    }

    public void sendMarkdown(String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("**时间:**").append(DateTimeUtils.format(LocalDateTime.now()));
        sb.append("**内容:**").append(content);
        log.info("send msg:{} to WeiXin robot", sb);
        String resp = restTemplate.postForObject("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=5ab774de-f077-48e9-b5e9-5bef2bb5f9f3",
                RequestObj.createMarkdownRequest(sb.toString()), String.class);
        log.info("WeiXin robot response: {}", resp);
    }

    public void sendPureMarkdown(String content) {
        log.info("send msg:{} to WeiXin robot", content);
        String resp = restTemplate.postForObject("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=5ab774de-f077-48e9-b5e9-5bef2bb5f9f3",
                RequestObj.createMarkdownRequest(content), String.class);
        log.info("WeiXin robot response: {}", resp);
    }

    public void sendText(String msg) {
        msg = "[" + DateTimeUtils.format(LocalDateTime.now()) + "]" + msg;
        log.info("send msg:{} to WeiXin robot", msg);
        String resp = restTemplate.postForObject("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=5ab774de-f077-48e9-b5e9-5bef2bb5f9f3",
                RequestObj.createTextRequest(msg), String.class);
        log.info("WeiXin robot response: {}", resp);
    }
}

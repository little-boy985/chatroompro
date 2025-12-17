package com.example.chatroom1;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;
import java.util.*;

@WebListener
public class AppInitListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent sce){
        ServletContext ctx = sce.getServletContext();
        // 存储在线用户：用户名 -> 登录时间
        Map<String,Long> userMap = Collections.synchronizedMap(new LinkedHashMap<>());
        ctx.setAttribute("onlineUsers", userMap);

        // 存储消息列表
        List<Message> list = Collections.synchronizedList(new ArrayList<>());
        ctx.setAttribute("messages", list);

        // 消息ID计数器
        ctx.setAttribute("msgId", 0L);
    }
    public void contextDestroyed(ServletContextEvent sce){}
}
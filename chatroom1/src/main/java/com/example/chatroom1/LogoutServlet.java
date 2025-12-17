package com.example.chatroom1;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.*;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if(session != null){
            String username = (String) session.getAttribute("username");
            ServletContext ctx = getServletContext();
            if(username != null){
                Map<String,Long> users = (Map<String,Long>) ctx.getAttribute("onlineUsers");
                synchronized(users){
                    users.remove(username);
                }
                // 发送离线广播
                sendSystemMessage(ctx, username + " 离开了聊天室");
            }
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/index.html");
    }

    private void sendSystemMessage(ServletContext ctx, String text) {
        List<Message> list = (List<Message>) ctx.getAttribute("messages");
        if(list == null) return;
        synchronized (list) {
            Long id = (Long) ctx.getAttribute("msgId") + 1;
            ctx.setAttribute("msgId", id);
            list.add(new Message(id, Message.SYSTEM_USER, null, text, System.currentTimeMillis()));
        }
    }
}
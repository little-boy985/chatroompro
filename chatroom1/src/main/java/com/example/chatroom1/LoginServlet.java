package com.example.chatroom1;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        if (username == null || username.trim().isEmpty()) {
            resp.sendRedirect("index.html?error=empty");
            return;
        }

        username = username.trim();
        // 禁止使用保留用户名
        if (Message.SYSTEM_USER.equals(username)) {
            resp.sendRedirect("index.html?error=reserved");
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("username", username);

        // 简单的管理员判定
        if ("admin".equalsIgnoreCase(username)) {
            session.setAttribute("role", "admin");
        } else {
            session.setAttribute("role", "user");
        }

        ServletContext ctx = getServletContext();
        Map<String, Long> users = (Map<String, Long>) ctx.getAttribute("onlineUsers");

        synchronized (users) {
            users.put(username, System.currentTimeMillis());
        }

        // 发送系统广播：用户加入
        sendSystemMessage(ctx, username + " 加入了聊天室");

        // 重定向到 ChatPageServlet 映射的路径
        resp.sendRedirect("chat");
    }

    private void sendSystemMessage(ServletContext ctx, String text) {
        List<Message> list = (List<Message>) ctx.getAttribute("messages");
        synchronized (list) {
            Long id = (Long) ctx.getAttribute("msgId") + 1;
            ctx.setAttribute("msgId", id);
            list.add(new Message(id, Message.SYSTEM_USER, null, text, System.currentTimeMillis()));
        }
    }
}
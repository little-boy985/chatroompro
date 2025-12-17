package com.example.chatroom1;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.*;

@WebServlet("/postMessage")
public class PostMessageServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String username = (String) session.getAttribute("username");
        String content = req.getParameter("message");
        String receiver = req.getParameter("receiver");

        if (content == null) content = "";
        content = content.trim();
        // 空消息不处理
        if (content.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        if ("".equals(receiver)) receiver = null; // 修正空字符串为null

        ServletContext ctx = getServletContext();
        List<Message> list = (List<Message>) ctx.getAttribute("messages");

        synchronized (list) {
            Long id = (Long) ctx.getAttribute("msgId") + 1;
            ctx.setAttribute("msgId", id);
            Message m = new Message(id, username, receiver, content, System.currentTimeMillis());
            list.add(m);
            // 限制历史记录数量，防止内存溢出
            if (list.size() > 200) list.remove(0);
        }

        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
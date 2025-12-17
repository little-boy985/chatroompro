package com.example.chatroom1;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.*;

@WebServlet("/fetchMessages")
public class FetchMessagesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");
        ServletContext ctx = getServletContext();

        List<Message> list = (List<Message>) ctx.getAttribute("messages");
        Map<String, Long> users = (Map<String, Long>) ctx.getAttribute("onlineUsers");

        StringBuilder sb = new StringBuilder();
        sb.append("{");

        // 1. 构建消息列表 JSON
        sb.append("\"messages\":[");
        synchronized (list) {
            boolean first = true;
            for (Message m : list) {
                // 权限判断：群聊、系统消息、我是发送者、我是接收者、或者我是管理员
                boolean visible =
                        m.getReceiver() == null ||
                                m.isSystem() ||
                                m.getSender().equals(username) ||
                                m.getReceiver().equals(username) ||
                                "admin".equals(role);

                if (!visible) continue;

                if (!first) sb.append(",");
                first = false;

                sb.append("{");
                sb.append("\"id\":").append(m.getId()).append(",");
                sb.append("\"sender\":\"").append(escape(m.getSender())).append("\",");
                sb.append("\"receiver\":").append(m.getReceiver() == null ? "null" : "\"" + escape(m.getReceiver()) + "\"").append(",");
                sb.append("\"content\":\"").append(escape(m.getContent())).append("\",");
                sb.append("\"isSystem\":").append(m.isSystem()).append(",");
                sb.append("\"time\":\"").append(m.getTimeString()).append("\"");
                sb.append("}");
            }
        }
        sb.append("],");

        // 2. 构建用户列表 JSON
        sb.append("\"users\":[");
        synchronized (users) {
            boolean first = true;
            for (String u : users.keySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append("\"").append(escape(u)).append("\"");
            }
        }
        sb.append("],");

        // 3. 当前用户信息
        sb.append("\"currentUser\":\"").append(escape(username)).append("\",");
        sb.append("\"isAdmin\":").append("admin".equals(role));

        sb.append("}");

        resp.getWriter().write(sb.toString());
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
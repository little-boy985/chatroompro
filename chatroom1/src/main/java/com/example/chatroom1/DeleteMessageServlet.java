package com.example.chatroom1;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.*;

@WebServlet("/adminAction")
public class DeleteMessageServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "你没有管理员权限！");
            return;
        }

        String action = req.getParameter("action");
        ServletContext ctx = getServletContext();

        if ("delete".equalsIgnoreCase(action)) {
            handleDelete(req, ctx, resp);
        } else if ("kick".equalsIgnoreCase(action)) {
            handleKick(req, ctx, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "未知操作");
        }
    }

    private void handleDelete(HttpServletRequest req, ServletContext ctx, HttpServletResponse resp) {
        try {
            long id = Long.parseLong(req.getParameter("id"));
            List<Message> list = (List<Message>) ctx.getAttribute("messages");
            synchronized (list) {
                list.removeIf(m -> m.getId() == id);
            }
            resp.setStatus(200);
        } catch (Exception e) {
            resp.setStatus(400);
        }
    }

    private void handleKick(HttpServletRequest req, ServletContext ctx, HttpServletResponse resp) {
        String target = req.getParameter("username");
        Map<String, Long> users = (Map<String, Long>) ctx.getAttribute("onlineUsers");
        synchronized (users) {
            users.remove(target);
        }
        resp.setStatus(200);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
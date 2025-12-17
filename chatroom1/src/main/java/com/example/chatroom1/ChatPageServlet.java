package com.example.chatroom1;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/chat")
public class ChatPageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if(session==null || session.getAttribute("username")==null){
            resp.sendRedirect(req.getContextPath()+"/index.html");
            return;
        }
        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");

        resp.setContentType("text/html;charset=UTF-8");
        String ctx = req.getContextPath();

        // 动态构建 HTML
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html lang='zh'><head><meta charset='utf-8'><title>聊天室 - "+username+"</title>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<link rel='stylesheet' href='"+ctx+"/assets/style.css'>");
        html.append("</head><body>");

        // 顶部
        html.append("<div class='nav'>");
        html.append("  <div class='nav-title'>ChatRoom v2.0</div>");
        html.append("  <div class='nav-user'>用户: <b>"+username+"</b> ("+role+") <a href='"+ctx+"/logout' class='btn-logout'>退出</a></div>");
        html.append("</div>");

        // 主容器
        html.append("<div class='container'>");

        // 左侧聊天区
        html.append("  <div class='chat-panel'>");
        html.append("    <div id='messages'></div>"); // 消息显示区
        html.append("    <div id='status-bar'>正在发言给: <span id='target-user'>所有人</span> <button id='cancel-private' style='display:none'>取消私聊</button></div>");
        html.append("    <form id='sendForm' onsubmit='return false;'>");
        html.append("      <input type='hidden' id='receiverInput' name='receiver' value=''>");
        html.append("      <input id='msgInput' name='message' autocomplete='off' placeholder='输入消息...' />");
        html.append("      <button type='submit' id='sendBtn'>发送</button>");
        html.append("    </form>");
        html.append("  </div>");

        // 右侧用户列表
        html.append("  <div class='side-panel'>");
        html.append("    <h3>在线用户</h3>");
        html.append("    <ul id='users' class='online-list'></ul>");
        html.append("  </div>");

        html.append("</div>");

        // 注入全局变量
        html.append("<script>");
        html.append("  var CONTEXTPATH = '"+ctx+"';");
        html.append("  var CURRENT_USER = '"+username+"';");
        html.append("  var IS_ADMIN = "+ ("admin".equals(role)) +";");
        html.append("</script>");
        html.append("<script src='"+ctx+"/assets/chat-client.js'></script>");
        html.append("</body></html>");

        resp.getWriter().write(html.toString());
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
}
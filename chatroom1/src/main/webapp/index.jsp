<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
   注意：在 JSTL 2.0 (Jakarta EE 9) 中，URI 仍然沿用 java.sun.com 的写法，
   虽然底层代码已经是 Jakarta 的了。请不要修改下面这行 URI。
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <title>登录 - 聊天室</title>
    <link rel="stylesheet" href="assets/style.css">
    <style>
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            background-color: #f0f2f5;
            margin: 0;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .login-box {
            background: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            width: 300px;
            text-align: center;
        }
        .login-box h2 { margin-bottom: 20px; color: #333; }
        .login-box input {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        .login-box button {
            width: 100%;
            padding: 10px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        .login-box button:hover { background-color: #0056b3; }
        .error {
            color: #e74c3c;
            font-size: 13px;
            margin-top: 10px;
            min-height: 20px;
        }
    </style>
</head>
<body>
<div class="login-box">
    <h2>加入聊天室</h2>

    <form method="post" action="login">
        <input type="text" name="username" placeholder="请输入昵称" required autocomplete="off">
        <button type="submit">进入</button>
    </form>

    <div class="error">
        <%-- 情况1：通过 URL 参数传递的错误 (如 ?error=empty) --%>
        <c:if test="${param.error == 'empty'}">
            用户名不能为空！
        </c:if>
        <c:if test="${param.error == 'reserved'}">
            该用户名不可用！
        </c:if>

        <%-- 情况2：通过 Request 属性传递的错误 (Servlet 转发) --%>
        <c:if test="${not empty requestScope.error}">
            ${requestScope.error}
        </c:if>
    </div>
</div>
</body>
</html>
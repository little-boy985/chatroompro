(function() {
    const msgList = document.getElementById("messages");
    const userList = document.getElementById("users");
    const msgInput = document.getElementById("msgInput");
    const sendBtn = document.getElementById("sendBtn");
    const sendForm = document.getElementById("sendForm");

    // 私聊相关 DOM
    const receiverInput = document.getElementById("receiverInput");
    const targetUserSpan = document.getElementById("target-user");
    const cancelPrivateBtn = document.getElementById("cancel-private");

    let lastMsgId = 0; // 记录最后一条消息ID，避免重复
    let isScrolledToBottom = true;

    // 1. 发送消息逻辑
    async function sendMessage() {
        const text = msgInput.value;
        if (!text.trim()) return;

        const receiver = receiverInput.value;
        const body = new URLSearchParams();
        body.append("message", text);
        body.append("receiver", receiver);

        try {
            const resp = await fetch(CONTEXTPATH + "/postMessage", {
                method: "POST",
                body: body
            });
            if (resp.ok) {
                msgInput.value = "";
                msgInput.focus();
                fetchData(); // 发送后立即刷新
            } else {
                alert("发送失败，可能是会话已过期");
                location.reload();
            }
        } catch (e) {
            console.error(e);
        }
    }

    sendForm.addEventListener("submit", (e) => {
        e.preventDefault();
        sendMessage();
    });

    // 2. 获取数据逻辑（轮询）
    async function fetchData() {
        try {
            const resp = await fetch(CONTEXTPATH + "/fetchMessages");
            if (resp.status === 403) {
                window.location.href = CONTEXTPATH + "/index.html";
                return;
            }
            const data = await resp.json();
            renderMessages(data.messages);
            renderUsers(data.users, data.currentUser);
        } catch (e) {
            console.error("Fetch error", e);
        }
    }

    // 3. 渲染消息
    function renderMessages(messages) {
        // 检测用户是否正在向上看历史记录
        const currentScroll = msgList.scrollTop + msgList.clientHeight;
        const isAtBottom = (Math.abs(msgList.scrollHeight - currentScroll) < 50);

        // 清空重绘比较暴力，但在无前端框架时最简单防止状态不一致
        // 优化点：可以只 append 大于 lastMsgId 的消息
        msgList.innerHTML = "";

        messages.forEach(msg => {
            const div = document.createElement("div");

            if (msg.isSystem) {
                div.className = "msg system";
                div.innerHTML = `<div class="content">${msg.content} <span style="font-size:10px">(${msg.time})</span></div>`;
            } else {
                const isSelf = (msg.sender === CURRENT_USER);
                div.className = "msg " + (isSelf ? "self" : "other");

                // 私聊标识
                const privateTag = (msg.receiver && msg.receiver !== "null")
                    ? `<span class="private-tag">[私聊]</span>` : "";

                // 管理员删除按钮
                const delBtn = IS_ADMIN ? `<button class="admin-btn admin-msg-btn" onclick="deleteMsg(${msg.id})">删除</button>` : "";

                div.innerHTML = `
                    <div class="info">${msg.sender} ${msg.time} ${delBtn}</div>
                    <div class="content">
                        ${privateTag}
                        ${msg.content}
                    </div>
                `;
            }
            msgList.appendChild(div);
        });

        // 如果之前在底部，或者刚初始化，则自动滚动到底部
        if (isAtBottom || lastMsgId === 0) {
            msgList.scrollTop = msgList.scrollHeight;
        }

        if (messages.length > 0) {
            lastMsgId = messages[messages.length - 1].id;
        }
    }

    // 4. 渲染用户列表
    function renderUsers(users, current) {
        userList.innerHTML = "";
        users.forEach(u => {
            const li = document.createElement("li");
            li.textContent = u;
            if (u === current) {
                li.style.fontWeight = "bold";
                li.textContent += " (我)";
            } else {
                // 点击用户进行私聊
                li.onclick = () => setPrivateChat(u);
            }

            // 当前选中的私聊对象高亮
            if (receiverInput.value === u) {
                li.classList.add("active");
            }

            // 管理员踢人按钮
            if (IS_ADMIN && u !== current) {
                const kickBtn = document.createElement("button");
                kickBtn.className = "admin-btn";
                kickBtn.textContent = "踢出";
                kickBtn.onclick = (e) => {
                    e.stopPropagation(); // 防止触发私聊点击
                    kickUser(u);
                };
                li.appendChild(kickBtn);
            }

            userList.appendChild(li);
        });
    }

    // 5. 私聊功能
    window.setPrivateChat = function(user) {
        receiverInput.value = user;
        targetUserSpan.textContent = user;
        targetUserSpan.style.color = "#e74c3c";
        cancelPrivateBtn.style.display = "inline-block";
        msgInput.placeholder = "发送私聊给 " + user + "...";
        msgInput.focus();
    };

    cancelPrivateBtn.onclick = function() {
        receiverInput.value = "";
        targetUserSpan.textContent = "所有人";
        targetUserSpan.style.color = "#007bff";
        cancelPrivateBtn.style.display = "none";
        msgInput.placeholder = "输入消息...";
    };

    // 6. 管理员功能
    window.deleteMsg = async function(id) {
        if(!confirm("确定删除这条消息？")) return;
        await fetch(CONTEXTPATH + "/adminAction?action=delete&id=" + id, {method: "POST"});
        fetchData();
    };

    window.kickUser = async function(username) {
        if(!confirm("确定踢出用户 " + username + "?")) return;
        await fetch(CONTEXTPATH + "/adminAction?action=kick&username=" + username, {method: "POST"});
        fetchData();
    };

    // 启动轮询
    fetchData();
    setInterval(fetchData, 2000); // 每2秒刷新一次

})();
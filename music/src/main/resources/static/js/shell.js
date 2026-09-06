/* ==========================================================================
   QQ 音乐风格左侧导航栏（全站共用）
   页面在 <head> 中以 <script defer src="js/shell.js"></script> 引入，
   脚本会自动把侧栏插到 <body> 最前面，并根据当前文件名高亮对应菜单。
   ========================================================================== */
(function () {
  // 奖品中心 / 金币记录属于 prize_center 应用，运行在 8082 端口
  const PRIZE_HOST = `http://${location.hostname}:8082`;

  const NAV = [
    {
      group: "在线音乐",
      items: [
        { ic: "🎧", text: "发现音乐", href: "music.html", match: ["music.html", "detail.html", ""] },
        { ic: "🏆", text: "排行榜", href: "rank.html", match: ["rank.html"] },
        { ic: "🔔", text: "系统通知", href: "notification.html", match: ["notification.html"] }
      ]
    },
    {
      group: "我的音乐",
      items: [
        { ic: "👤", text: "我的主页", href: "index.html", match: ["index.html"] },
        { ic: "🏷️", text: "兴趣标签", href: "interest.html", match: ["interest.html"] }
      ]
    },
    {
      group: "福利中心",
      items: [
        { ic: "🪙", text: "奖品中心", href: PRIZE_HOST + "/prize.html", match: [] },
        { ic: "🧾", text: "金币记录", href: PRIZE_HOST + "/orderRecord.html", match: [] }
      ]
    }
  ];

  const page = location.pathname.split("/").pop();
  const user = sessionStorage.getItem("musicUser") || "";

  function esc(s) {
    return String(s ?? "").replace(/[&<>"]/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", "\"": "&quot;" }[c]));
  }

  const navHtml = NAV.map(g => `
    <div class="group">${g.group}</div>
    ${g.items.map(it => `
      <a class="${it.match.includes(page) ? "active" : ""}" href="${it.href}">
        <span class="ic">${it.ic}</span>${it.text}
      </a>`).join("")}
  `).join("");

  const side = document.createElement("aside");
  side.className = "qm-side";
  side.innerHTML = `
    <a class="qm-brand" href="music.html">
      <span class="logo">♪</span>
      <span class="name">音乐管理<small>MUSIC CENTER</small></span>
    </a>
    <nav class="qm-nav">${navHtml}</nav>
    <div class="qm-side-foot">
      <div class="avatar">${esc((user || "?").charAt(0).toUpperCase())}</div>
      <div class="who">
        <div class="n" title="${esc(user)}">${esc(user) || "未登录"}</div>
        <div class="r">已登录</div>
      </div>
      <button class="out" onclick="qmLogout()" title="退出登录" aria-label="退出登录">⏻</button>
    </div>`;

  document.body.insertBefore(side, document.body.firstChild);

  window.qmLogout = function () {
    sessionStorage.removeItem("musicUser");
    location.replace("login.html");
  };
})();
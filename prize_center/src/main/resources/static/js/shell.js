/* ==========================================================================
   QQ 音乐风格左侧导航栏（prize_center 版）
   与 music 应用的 js/shell.js 保持一致的外观；因为分属两个 Spring 应用，
   音乐相关菜单指向 8080 端口的 music 应用，奖品相关菜单为本应用内的相对路径。
   ========================================================================== */
(function () {
  // music 应用运行在 8080 端口
  const MUSIC_HOST = `http://${location.hostname}:8080`;

  const NAV = [
    {
      group: "在线音乐",
      items: [
        { ic: "🎧", text: "发现音乐", href: MUSIC_HOST + "/music.html", match: [] },
        { ic: "🏆", text: "排行榜", href: MUSIC_HOST + "/rank.html", match: [] },
        { ic: "🔔", text: "系统通知", href: MUSIC_HOST + "/notification.html", match: [] }
      ]
    },
    {
      group: "我的音乐",
      items: [
        { ic: "👤", text: "我的主页", href: MUSIC_HOST + "/index.html", match: [] },
        { ic: "🏷️", text: "兴趣标签", href: MUSIC_HOST + "/interest.html", match: [] }
      ]
    },
    {
      group: "福利中心",
      items: [
        { ic: "🪙", text: "奖品中心", href: "prize.html", match: ["prize.html", ""] },
        { ic: "🧾", text: "金币记录", href: "orderRecord.html", match: ["orderRecord.html"] }
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
    <a class="qm-brand" href="${MUSIC_HOST}/music.html">
      <span class="logo">♪</span>
      <span class="name">音乐管理<small>MUSIC CENTER</small></span>
    </a>
    <nav class="qm-nav">${navHtml}</nav>
    <div class="qm-side-foot">
      <div class="avatar">${esc((user || "?").charAt(0).toUpperCase())}</div>
      <div class="who">
        <div class="n" title="${esc(user)}">${esc(user) || "未登录"}</div>
        <div class="r">福利中心</div>
      </div>
      <button class="out" onclick="qmLogout()" title="退出登录" aria-label="退出登录">⏻</button>
    </div>`;

  document.body.insertBefore(side, document.body.firstChild);

  window.qmLogout = function () {
    sessionStorage.removeItem("musicUser");
    location.replace(MUSIC_HOST + "/login.html");
  };
})();
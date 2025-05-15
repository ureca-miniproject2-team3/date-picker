window.onload = async () => {
  const userId = sessionStorage.getItem("userId");
  document.getElementById("loginBtn").style.display = userId ? "none" : "inline-block";
  document.getElementById("logoutBtn").style.display = userId ? "inline-block" : "none";
  document.getElementById("myPageBtn").style.display = userId ? "inline-block" : "none";

  if (userId) {
    showCurrentUser(userId);
    document.getElementById("logoutBtn").addEventListener("click", logout);

    try {
      const csrfToken = await getCsrfToken();
      const formData = new URLSearchParams();
      formData.append('_csrf', csrfToken);

      const res = await fetch('/api/events/status', {
        method: 'PUT',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData
      });

      const result = await res.json();
      if (result.result === 'success') {
        console.log('이벤트 상태 업데이트 성공');
      } else {
        console.warn('이벤트 상태 업데이트 실패:', result);
      }
    } catch (err) {
      console.error('이벤트 상태 업데이트 요청 실패:', err);
    }
  }

  document.querySelectorAll('[data-tab]').forEach(tab => {
    tab.addEventListener('click', (e) => {
      document.querySelectorAll('[data-tab]').forEach(t => {
        t.classList.remove('text-[#7c6dfa]', 'border-b-2', 'border-[#7c6dfa]', 'font-medium');
        t.classList.add('text-gray-500');
      });

      e.target.classList.remove('text-gray-500');
      e.target.classList.add('text-[#7c6dfa]', 'border-b-2', 'border-[#7c6dfa]', 'font-medium');

      document.getElementById('uncheckedEvents').classList.add('hidden');
      document.getElementById('checkedEvents').classList.add('hidden');
      document.getElementById('completedEvents').classList.add('hidden');
      document.getElementById('expiredEvents').classList.add('hidden');

      const selectedTab = e.target.getAttribute('data-tab');
      document.getElementById(selectedTab + 'Events').classList.remove('hidden');

      fetchEvents();
    });
  });

  fetchEvents();
};

async function showCurrentUser(userId) {
  try {
    const res = await fetch(`/api/users/${userId}`, { credentials: "same-origin" });
    const result = await res.json();
    if (result.result === "success" && result.userDto?.name) {
      document.getElementById("userInfo").textContent = `${result.userDto.name}님 환영합니다!`;
    }
  } catch (err) {
    console.error("유저 정보 불러오기 실패:", err);
  }
}

async function getCsrfToken() {
  const res = await fetch('/api/auth/csrf-token', { credentials: 'same-origin' });
  const data = await res.json();
  return data.token;
}

async function fetchOverlap(eventId) {
  try {
    const res = await fetch(`/api/schedules/overlap/${eventId}`, { credentials: 'same-origin' });
    const data = await res.json();

    if (data.result === 'success' && Array.isArray(data.timeSlots) && data.timeSlots.length > 0) {
      const best = data.timeSlots[0];
      const startDate = new Date(best.start);
      const endDate = new Date(best.end);

      const dateStr = `${startDate.getFullYear()}.${String(startDate.getMonth() + 1).padStart(2, '0')}.${String(startDate.getDate()).padStart(2, '0')}`;
      const startTime = `${String(startDate.getHours()).padStart(2, '0')}:${String(startDate.getMinutes()).padStart(2, '0')}`;
      const endTime = `${String(endDate.getHours()).padStart(2, '0')}:${String(endDate.getMinutes()).padStart(2, '0')}`;

      return `${dateStr} ${startTime} ~ ${endTime}`;
    }
  } catch (err) {
    console.warn(`overlap fetch 실패 for event ${eventId}:`, err);
  }
  return null;
}

async function fetchEvents() {
  const userId = sessionStorage.getItem("userId");
  const uncheckedList = document.getElementById("uncheckedEvents");
  const checkedList = document.getElementById("checkedEvents");
  const completedList = document.getElementById("completedEvents");
  const expiredList = document.getElementById("expiredEvents");

  if (!userId) {
    const loginMessage = `
      <div class="bg-white p-6 rounded-2xl shadow text-center col-span-full">
        <p class="text-lg font-semibold mb-2">로그인이 필요합니다</p>
        <p class="text-sm text-gray-500">이벤트 목록을 보려면 로그인 해주세요.</p>
      </div>`;
    uncheckedList.innerHTML = loginMessage;
    checkedList.innerHTML = loginMessage;
    completedList.innerHTML = loginMessage;
    expiredList.innerHTML = loginMessage;
    return;
  }

  try {
    const res = await fetch(`/api/users/${userId}/events`, {
      method: "GET",
      credentials: "same-origin"
    });

    const result = await res.json();
    const events = result?.eventDtoList;

    if (result.result === "success" && Array.isArray(events)) {
      if (events.length === 0) {
        const emptyMessage = `
          <div class="bg-white p-6 rounded-2xl shadow text-center col-span-full text-gray-500">
            등록된 이벤트가 없습니다.
          </div>`;
        uncheckedList.innerHTML = emptyMessage;
        checkedList.innerHTML = emptyMessage;
        completedList.innerHTML = emptyMessage;
        expiredList.innerHTML = emptyMessage;
      } else {
        const uncheckedEvents = events.filter(e => e.status === "UNCHECKED");
        const checkedEvents = events.filter(e => e.status === "CHECKED")
            .sort((a, b) => {
              if (!a.timeline || !b.timeline) return 0;
              return new Date(a.timeline.startTime) - new Date(b.timeline.startTime);
            });
        const completedEvents = events.filter(e => e.status === "COMPLETED");
        const expiredEvents = events.filter(e => e.status === "EXPIRED");

        renderEventList(uncheckedList, uncheckedEvents);
        renderEventList(checkedList, checkedEvents);
        renderEventList(completedList, completedEvents);
        renderEventList(expiredList, expiredEvents);
      }
    } else {
      const errorMessage = `
        <div class="bg-white p-6 rounded-2xl shadow text-center col-span-full text-red-500">
          <p class="text-lg font-semibold">이벤트 목록을 불러오는데 실패했습니다.</p>
        </div>`;
      uncheckedList.innerHTML = errorMessage;
      checkedList.innerHTML = errorMessage;
      completedList.innerHTML = errorMessage;
      expiredList.innerHTML = errorMessage;
    }
  } catch (err) {
    const errorMessage = `
      <div class="bg-white p-6 rounded-2xl shadow text-center col-span-full text-red-500">
        <p class="text-lg font-semibold">예외 발생</p>
        <pre class="text-sm mt-2">${err.message}</pre>
      </div>`;
    uncheckedList.innerHTML = errorMessage;
    checkedList.innerHTML = errorMessage;
    completedList.innerHTML = errorMessage;
    expiredList.innerHTML = errorMessage;
    console.error("예외:", err);
  }
}

async function renderEventList(container, events) {
  if (events.length === 0) {
    container.innerHTML = `
      <div class="bg-white p-6 rounded-2xl shadow text-center col-span-full text-gray-500">
        등록된 이벤트가 없습니다.
      </div>`;
    return;
  }

  const eventHtmls = await Promise.all(events.map(async (e) => {
    const memberCount = e.userIds?.length ?? 0;
    const memberNames = e.userNames?.join(', ') ?? '';
    const overlapTime = e.status !== "CHECKED" && e.status !== "COMPLETED" ? await fetchOverlap(e.eventId) : null;

    let statusClass, statusText, statusColor;
    switch(e.status) {
      case "CHECKED":
        statusClass = "bg-green-50";
        statusText = "확정";
        statusColor = "text-blue-600";
        break;
      case "COMPLETED":
        statusClass = "bg-emerald-50/70";
        statusText = "완료";
        statusColor = "text-green-600";
        break;
      case "EXPIRED":
        statusClass = "bg-red-50/70";
        statusText = "만료";
        statusColor = "text-red-600";
        break;
      default:
        statusClass = "bg-white";
        statusText = "미확정";
        statusColor = "text-gray-600";
    }

    const timelineInfo = e.timeline ? (() => {
      const startDate = new Date(e.timeline.startTime);
      const endDate = new Date(e.timeline.endTime);
      const dateStr = `${startDate.getFullYear()}.${String(startDate.getMonth() + 1).padStart(2, '0')}.${String(startDate.getDate()).padStart(2, '0')}`;
      const startTime = `${String(startDate.getHours()).padStart(2, '0')}:${String(startDate.getMinutes()).padStart(2, '0')}`;
      const endTime = `${String(endDate.getHours()).padStart(2, '0')}:${String(endDate.getMinutes()).padStart(2, '0')}`;
      return `<div class="text-sm text-indigo-600 mt-2">🕓 ${dateStr} ${startTime} ~ ${endTime}</div>`;
    })() : '';

    return `
      <div onclick="location.href='event.html?id=${e.eventId}'"
         class="relative ${statusClass} w-full h-36 flex flex-col p-6 rounded-2xl shadow-md hover:bg-[#ebe4db] cursor-pointer transition">
        <div class="flex justify-between items-start mb-4">
          <div class="text-xl font-semibold">${e.title}</div>
          <div class="flex items-center gap-3">
            <div class="text-xs text-gray-600">👥 ${memberCount}명</div>
            <div class="text-xs ${statusColor}">${statusText}</div>
          </div>
        </div>
        <div class="flex-1"></div>
        <div class="space-y-2">
          <div class="text-sm text-gray-500">${memberNames}</div>
          ${timelineInfo}
          ${overlapTime ? `<div class="text-sm text-indigo-600">🕓 ${overlapTime}</div>` : ''}
        </div>
      </div>`;
  }));

  container.innerHTML = eventHtmls.join('');
}

async function logout() {
  const csrf = await getCsrfToken();
  const urlParams = new URLSearchParams({ _csrf: csrf });

  const res = await fetch("/logout", {
    method: "POST",
    body: urlParams,
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    credentials: "same-origin"
  });

  sessionStorage.clear();
  window.location.href = res.redirected ? res.url : "/";
}

// DOM이 로드된 후 실행
document.addEventListener('DOMContentLoaded', () => {
  // 탭 버튼들
  const tabButtons = document.querySelectorAll('[data-tab]');
  // 이벤트 컨테이너들
  const eventContainers = {
    unchecked: document.getElementById('uncheckedEvents'),
    checked: document.getElementById('checkedEvents'),
    completed: document.getElementById('completedEvents'),
    expired: document.getElementById('expiredEvents')
  };

  // 탭 전환 함수
  function switchTab(tabName) {
    // 모든 탭 버튼 스타일 초기화
    tabButtons.forEach(button => {
      button.classList.remove('text-[#7c6dfa]', 'border-b-2', 'border-[#7c6dfa]', 'font-medium');
      button.classList.add('text-gray-500');
    });

    // 선택된 탭 버튼 스타일 적용
    const selectedButton = document.querySelector(`[data-tab="${tabName}"]`);
    selectedButton.classList.remove('text-gray-500');
    selectedButton.classList.add('text-[#7c6dfa]', 'border-b-2', 'border-[#7c6dfa]', 'font-medium');

    // 모든 이벤트 컨테이너 숨기기
    Object.values(eventContainers).forEach(container => {
      container.classList.add('hidden');
    });

    // 선택된 이벤트 컨테이너 표시
    eventContainers[tabName].classList.remove('hidden');
  }

  // 탭 버튼 클릭 이벤트 리스너 등록
  tabButtons.forEach(button => {
    button.addEventListener('click', () => {
      const tabName = button.getAttribute('data-tab');
      switchTab(tabName);
    });
  });

  // 초기 탭 설정 (미확정 일정)
  switchTab('unchecked');

  // 로그인 상태 확인 및 UI 업데이트
  function updateLoginUI() {
    const userInfo = localStorage.getItem('userInfo');
    const loginBtn = document.getElementById('loginBtn');
    const myPageBtn = document.getElementById('myPageBtn');
    const logoutBtn = document.getElementById('logoutBtn');
    const userInfoDiv = document.getElementById('userInfo');

    if (userInfo) {
      const user = JSON.parse(userInfo);
      userInfoDiv.textContent = `${user.name}님 환영합니다`;
      loginBtn.classList.add('hidden');
      myPageBtn.classList.remove('hidden');
      logoutBtn.classList.remove('hidden');
    } else {
      userInfoDiv.textContent = '';
      loginBtn.classList.remove('hidden');
      myPageBtn.classList.add('hidden');
      logoutBtn.classList.add('hidden');
    }
  }

  // 로그아웃 기능
  document.getElementById('logoutBtn').addEventListener('click', () => {
    localStorage.removeItem('userInfo');
    updateLoginUI();
  });

  // 초기 로그인 상태 확인
  updateLoginUI();
});

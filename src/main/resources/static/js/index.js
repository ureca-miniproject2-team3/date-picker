// index.js
window.onload = async () => {
  // 디버깅을 위한 로그 추가
  console.log('페이지 로드 시작');
  console.log('현재 sessionStorage:', sessionStorage);
  
  const userId = sessionStorage.getItem("userId");
  console.log('userId 값:', userId);
  
  // 버튼 요소 가져오기
  const loginBtn = document.getElementById("loginBtn");
  const logoutBtn = document.getElementById("logoutBtn");
  const myPageBtn = document.getElementById("myPageBtn");
  
  // 모든 버튼 숨기기
  loginBtn.style.display = "none";
  logoutBtn.style.display = "none";
  myPageBtn.style.display = "none";
  
  // 로그인 상태 체크
  const isLoggedIn = userId && userId !== "null" && userId !== "undefined" && userId.trim() !== "";
  console.log('로그인 상태:', isLoggedIn);
  
  if (isLoggedIn) {
    console.log('로그인 상태: 버튼 표시 설정');
    // 로그인 상태일 때
    logoutBtn.style.display = "inline-block";
    myPageBtn.style.display = "inline-block";
    
    // 유저 정보 표시
    showCurrentUser(userId);
    
    // 로그아웃 이벤트 리스너
    logoutBtn.addEventListener("click", logout);
    
    // 이벤트 상태 업데이트
    try {
      const csrfToken = await getCsrfToken();
      const formData = new URLSearchParams();
      formData.append('_csrf', csrfToken);
      
      const res = await fetch('/api/events/status', {
        method: 'PUT',
        credentials: 'same-origin',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
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
  } else {
    console.log('로그아웃 상태: 로그인 버튼만 표시');
    // 로그아웃 상태일 때
    loginBtn.style.display = "inline-block";
    // sessionStorage 초기화
    sessionStorage.clear();
  }

  // 탭 클릭 이벤트 리스너 추가
  document.querySelectorAll('[data-tab]').forEach(tab => {
    tab.addEventListener('click', (e) => {
      // 모든 탭의 스타일 초기화
      document.querySelectorAll('[data-tab]').forEach(t => {
        t.classList.remove('text-[#7c6dfa]', 'border-b-2', 'border-[#7c6dfa]', 'font-medium');
        t.classList.add('text-gray-500');
      });
      
      // 클릭된 탭 스타일 적용
      e.target.classList.remove('text-gray-500');
      e.target.classList.add('text-[#7c6dfa]', 'border-b-2', 'border-[#7c6dfa]', 'font-medium');
      
      // 모든 이벤트 컨테이너 숨기기
      document.getElementById('uncheckedEvents').classList.add('hidden');
      document.getElementById('checkedEvents').classList.add('hidden');
      document.getElementById('completedEvents').classList.add('hidden');
      document.getElementById('expiredEvents').classList.add('hidden');
      
      // 선택된 탭의 이벤트 컨테이너 보이기
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
    const res = await fetch(`/api/schedules/overlap/${eventId}`, {
      credentials: 'same-origin'
    });
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

    if (!res.ok) {
      throw new Error(`HTTP error! status: ${res.status}`);
    }

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
        // 이벤트를 상태별로 분류
        const uncheckedEvents = events.filter(e => e.status === "UNCHECKED");
        const checkedEvents = events.filter(e => e.status === "CHECKED")
          .sort((a, b) => {
            if (!a.timeline || !b.timeline) return 0;
            return new Date(a.timeline.startTime) - new Date(b.timeline.startTime);
          });
        const completedEvents = events.filter(e => e.status === "COMPLETED");
        const expiredEvents = events.filter(e => e.status === "EXPIRED");

        // 각 상태별 이벤트 렌더링
        await renderEventList(uncheckedList, uncheckedEvents);
        await renderEventList(checkedList, checkedEvents);
        await renderEventList(completedList, completedEvents);
        await renderEventList(expiredList, expiredEvents);
      }
    } else {
      throw new Error(result.message || '이벤트 목록을 불러오는데 실패했습니다.');
    }
  } catch (err) {
    console.error("이벤트 목록 불러오기 실패:", err);
    const errorMessage = `
      <div class="bg-white p-6 rounded-2xl shadow text-center col-span-full text-red-500">
        <p class="text-lg font-semibold">이벤트 목록을 불러오는데 실패했습니다.</p>
        <p class="text-sm mt-2">${err.message}</p>
      </div>`;
    uncheckedList.innerHTML = errorMessage;
    checkedList.innerHTML = errorMessage;
    completedList.innerHTML = errorMessage;
    expiredList.innerHTML = errorMessage;
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

  const eventHtmls = await Promise.all(events.map(async (event) => {
    const memberCount = event.userIds?.length ?? 0;
    const memberNames = event.userNames?.join(', ') ?? '';
    const overlapTime = event.status !== "CHECKED" && event.status !== "COMPLETED" ? 
      await fetchOverlap(event.eventId) : null;
    
    const timelineInfo = event.timeline ? (() => {
      const startDate = new Date(event.timeline.startTime);
      const endDate = new Date(event.timeline.endTime);
      const dateStr = `${startDate.getFullYear()}.${String(startDate.getMonth() + 1).padStart(2, '0')}.${String(startDate.getDate()).padStart(2, '0')}`;
      const startTime = `${String(startDate.getHours()).padStart(2, '0')}:${String(startDate.getMinutes()).padStart(2, '0')}`;
      const endTime = `${String(endDate.getHours()).padStart(2, '0')}:${String(endDate.getMinutes()).padStart(2, '0')}`;
      return `<div class="text-sm text-indigo-600 mt-2">🕓 ${dateStr} ${startTime} ~ ${endTime}</div>`;
    })() : '';

    // 상태에 따른 스타일 적용
    let statusClass, statusText, statusColor;
    switch(event.status) {
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

    return `
      <div onclick="location.href='event.html?id=${event.eventId}'"
           class="relative ${statusClass} w-full h-36 flex flex-col p-6 rounded-2xl shadow-md hover:bg-[#ebe4db] cursor-pointer transition">
        <div class="flex justify-between items-start mb-4">
          <div class="text-xl font-semibold">${event.title}</div>
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
  try {
    const csrfToken = await getCsrfToken();
    const formData = new URLSearchParams();
    formData.append('_csrf', csrfToken);
    
    const res = await fetch('/logout', {
      method: 'POST',
      credentials: 'same-origin',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      body: formData
    });
    
    if (res.ok) {
      sessionStorage.removeItem("userId");
      window.location.href = '/';
    }
  } catch (err) {
    console.error("로그아웃 실패:", err);
  }
} 
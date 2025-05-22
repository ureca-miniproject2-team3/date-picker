// 공통코드를 저장할 전역 변수
let eventStatusCodes = {};
let currentActiveTab = '001'; // 기본 활성 탭 (미확정)

window.onload = async () => {
  // 공통코드 초기화
  await initCommonCodes();
  
  // 탭 동적 생성
  createEventTabs();

  const userId = sessionStorage.getItem("userId");
  document.getElementById("loginBtn").style.display = userId ? "none" : "inline-block";
  document.getElementById("logoutBtn").style.display = userId ? "inline-block" : "none";
  document.getElementById("myPageBtn").style.display = userId ? "inline-block" : "none";

  if (userId) {
    showCurrentUser(userId);
    document.getElementById("logoutBtn").addEventListener("click", logout);

    // WebSocket 연결 설정
    const socket = new SockJS("/ws");
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
      console.log("WebSocket 연결 성공");

      // 사용자별 알림 구독
      stompClient.subscribe("/topic/user/" + userId, async (message) => {
        const data = JSON.parse(message.body);
        alertify.message("🔔 [" + data.title + "] 이벤트에 초대되었습니다!", "success", 5);

        // 서버에 수신 처리 (URL-encoded 형식으로)
        try {
          const csrfToken = await getCsrfToken();
          const formData = new URLSearchParams();
          formData.append('userId', userId);
          formData.append('eventId', data.eventId);
          formData.append('_csrf', csrfToken);

          const response = await fetch("/api/notifications/received", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: formData,
            credentials: "same-origin"
          });

          if (response.ok) {
            console.log("알림 수신 처리 완료");
            // 이벤트 목록 새로고침
            fetchEvents();
          } else {
            console.error("알림 수신 처리 실패:", response.status);
          }
        } catch (error) {
          console.error("알림 수신 처리 중 오류 발생:", error);
        }
      });
    }, (error) => {
      console.error("WebSocket 연결 실패:", error);
    });

    // 이벤트 상태 업데이트
    try {
      console.log('이벤트 상태 업데이트 요청 시작...');
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
      console.log('상태 업데이트 응답 상태:', res.status);

      const result = await res.json();
      console.log('상태 업데이트 응답:', result);

      if (result.result === 'success') {
        console.log('이벤트 상태 업데이트 성공');
      } else {
        console.warn('이벤트 상태 업데이트 실패:', result);
      }
    } catch (err) {
      console.error('이벤트 상태 업데이트 요청 실패:', err);
    }
  }

  fetchEvents();
};

/**
 * 공통코드를 초기화하는 함수
 */
async function initCommonCodes() {
  try {
    const csrfToken = await getCsrfToken();
    
    const response = await fetch('/api/commoncodes', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-XSRF-TOKEN': csrfToken
      },
      credentials: 'same-origin',
      body: JSON.stringify(['010']) // 이벤트 상태 그룹코드
    });

    if (!response.ok) {
      throw new Error(`공통코드 조회 실패: ${response.status}`);
    }

    const result = await response.json();
    
    if (result.result === 'success' && result.commonCodeDtoListMap && result.commonCodeDtoListMap['010']) {
      // orderNo 순으로 정렬하여 이벤트 상태 코드를 매핑
      const sortedCodes = result.commonCodeDtoListMap['010'].sort((a, b) => a.orderNo - b.orderNo);
      sortedCodes.forEach(code => {
        eventStatusCodes[code.code] = {
          codeName: code.codeName,
          codeNameBrief: code.codeNameBrief,
          orderNo: code.orderNo
        };
      });
      console.log('공통코드 초기화 완료:', eventStatusCodes);
    } else {
      console.error('공통코드 응답 형식 오류:', result);
      // 기본값 설정
      setDefaultStatusCodes();
    }
  } catch (error) {
    console.error('공통코드 초기화 중 오류:', error);
    // 기본값 설정
    setDefaultStatusCodes();
  }
}

/**
 * 기본 상태 코드 설정 (공통코드 조회 실패 시)
 */
function setDefaultStatusCodes() {
  eventStatusCodes = {
    '001': { codeName: '미확정', codeNameBrief: '미확정', orderNo: 1 },
    '002': { codeName: '확정', codeNameBrief: '확정', orderNo: 2 },
    '003': { codeName: '완료', codeNameBrief: '완료', orderNo: 3 },
    '004': { codeName: '만료', codeNameBrief: '만료', orderNo: 4 }
  };
}

/**
 * 공통코드를 기반으로 탭을 동적 생성
 */
function createEventTabs() {
  const tabContainer = document.getElementById('eventTabs');
  if (!tabContainer) return;

  // 기존 탭 제거
  tabContainer.innerHTML = '';

  // orderNo 순으로 정렬된 코드로 탭 생성
  const sortedCodes = Object.entries(eventStatusCodes)
    .sort(([,a], [,b]) => a.orderNo - b.orderNo);

  sortedCodes.forEach(([code, codeInfo], index) => {
    const tabButton = document.createElement('button');
    const tabKey = getTabKeyByCode(code);
    
    tabButton.className = index === 0 
      ? 'px-4 py-2 text-[#7c6dfa] border-b-2 border-[#7c6dfa] font-medium'
      : 'px-4 py-2 text-gray-500 hover:text-gray-700';
    
    tabButton.setAttribute('data-tab', tabKey);
    tabButton.setAttribute('data-code', code);
    tabButton.textContent = `${codeInfo.codeName} 일정`;
    
    // 탭 클릭 이벤트 리스너 추가
    tabButton.addEventListener('click', (e) => {
      handleTabClick(e.target);
    });
    
    tabContainer.appendChild(tabButton);
  });

  // 이벤트 컨테이너도 동적 생성
  createEventContainers();
}

/**
 * 이벤트 컨테이너 동적 생성
 */
function createEventContainers() {
  const mainContainer = document.querySelector('.space-y-6');
  const tabContainer = document.getElementById('eventTabs');
  
  if (!mainContainer || !tabContainer) return;

  // 기존 이벤트 컨테이너 제거 (탭 다음 요소들)
  const existingContainers = mainContainer.querySelectorAll('[id$="Events"]');
  existingContainers.forEach(container => container.remove());

  // 새 컨테이너 생성
  Object.keys(eventStatusCodes).forEach((code, index) => {
    const tabKey = getTabKeyByCode(code);
    const container = document.createElement('div');
    container.id = `${tabKey}Events`;
    container.className = index === 0 
      ? 'grid grid-cols-1 gap-6' 
      : 'grid grid-cols-1 gap-6 hidden';
    
    // 탭 컨테이너 다음에 삽입
    tabContainer.parentNode.insertBefore(container, tabContainer.nextSibling);
  });
}

/**
 * 코드에 따른 탭 키 반환
 */
function getTabKeyByCode(code) {
  const tabKeyMap = {
    '001': 'unchecked',
    '002': 'checked', 
    '003': 'completed',
    '004': 'expired'
  };
  return tabKeyMap[code] || 'unknown';
}

/**
 * 탭 클릭 핸들러
 */
function handleTabClick(clickedTab) {
  // 모든 탭의 스타일 초기화
  document.querySelectorAll('[data-tab]').forEach(tab => {
    tab.classList.remove('text-[#7c6dfa]', 'border-b-2', 'border-[#7c6dfa]', 'font-medium');
    tab.classList.add('text-gray-500');
  });

  // 클릭된 탭 스타일 적용
  clickedTab.classList.remove('text-gray-500');
  clickedTab.classList.add('text-[#7c6dfa]', 'border-b-2', 'border-[#7c6dfa]', 'font-medium');

  // 모든 이벤트 컨테이너 숨기기
  Object.keys(eventStatusCodes).forEach(code => {
    const tabKey = getTabKeyByCode(code);
    const container = document.getElementById(`${tabKey}Events`);
    if (container) {
      container.classList.add('hidden');
    }
  });

  // 선택된 탭의 이벤트 컨테이너 보이기
  const selectedTab = clickedTab.getAttribute('data-tab');
  const selectedContainer = document.getElementById(`${selectedTab}Events`);
  if (selectedContainer) {
    selectedContainer.classList.remove('hidden');
  }

  // 현재 활성 탭 업데이트
  currentActiveTab = clickedTab.getAttribute('data-code');
  
  fetchEvents();
}

/**
 * 코드로 상태명 가져오기
 */
function getStatusName(code) {
  return eventStatusCodes[code]?.codeName || '알 수 없음';
}

/**
 * 상태명으로 코드 가져오기
 */
function getStatusCode(name) {
  for (const [code, codeInfo] of Object.entries(eventStatusCodes)) {
    if (codeInfo.codeName === name) {
      return code;
    }
  }
  return null;
}

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
  
  // 모든 이벤트 컨테이너 찾기
  const containers = {};
  Object.keys(eventStatusCodes).forEach(code => {
    const tabKey = getTabKeyByCode(code);
    containers[code] = document.getElementById(`${tabKey}Events`);
  });

  if (!userId) {
    const loginMessage = `
        <div class="bg-white p-6 rounded-2xl shadow text-center col-span-full">
          <p class="text-lg font-semibold mb-2">로그인이 필요합니다</p>
          <p class="text-sm text-gray-500">이벤트 목록을 보려면 로그인 해주세요.</p>
        </div>`;
    Object.values(containers).forEach(container => {
      if (container) container.innerHTML = loginMessage;
    });
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
        Object.values(containers).forEach(container => {
          if (container) container.innerHTML = emptyMessage;
        });
      } else {
        // 이벤트를 상태별로 분류 (공통코드 사용)
        const eventsByStatus = {};
        
        // 각 상태별 빈 배열 초기화
        Object.keys(eventStatusCodes).forEach(code => {
          eventsByStatus[code] = [];
        });

        // 이벤트를 상태별로 분류
        events.forEach(event => {
          if (eventsByStatus[event.code]) {
            eventsByStatus[event.code].push(event);
          }
        });

        // 확정된 이벤트는 타임라인 기준으로 정렬
        if (eventsByStatus['002']) {
          eventsByStatus['002'].sort((a, b) => {
            if (!a.timeline || !b.timeline) return 0;
            return new Date(a.timeline.startTime) - new Date(b.timeline.startTime);
          });
        }

        // 각 상태별 이벤트 렌더링
        Object.keys(eventStatusCodes).forEach(code => {
          const container = containers[code];
          if (container) {
            renderEventList(container, eventsByStatus[code] || []);
          }
        });
      }
    } else {
      const errorMessage = `
          <div class="bg-white p-6 rounded-2xl shadow text-center col-span-full text-red-500">
            <p class="text-lg font-semibold">이벤트 목록을 불러오는데 실패했습니다.</p>
          </div>`;
      Object.values(containers).forEach(container => {
        if (container) container.innerHTML = errorMessage;
      });
    }
  } catch (err) {
    const errorMessage = `
        <div class="bg-white p-6 rounded-2xl shadow text-center col-span-full text-red-500">
          <p class="text-lg font-semibold">예외 발생</p>
          <pre class="text-sm mt-2">${err.message}</pre>
        </div>`;
    Object.values(containers).forEach(container => {
      if (container) container.innerHTML = errorMessage;
    });
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
    
    // 공통코드를 사용하여 상태명 가져오기
    const statusName = getStatusName(e.code);
    
    // 확정, 완료가 아닌 경우에만 겹치는 시간 조회
    const overlapTime = (e.code !== '002' && e.code !== '003') ? await fetchOverlap(e.eventId) : null;

    // 상태에 따른 스타일 적용 (공통코드 기반)
    let statusClass, statusColor;
    switch(e.code) {
      case '002': // 확정
        statusClass = "bg-green-50";
        statusColor = "text-blue-600";
        break;
      case '003': // 완료
        statusClass = "bg-emerald-50/70";
        statusColor = "text-green-600";
        break;
      case '004': // 만료
        statusClass = "bg-red-50/70";
        statusColor = "text-red-600";
        break;
      default: // 001: 미확정
        statusClass = "bg-white";
        statusColor = "text-gray-600";
    }

    // 타임라인 정보가 있는 경우 표시
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
              <div class="text-xs ${statusColor}">${statusName}</div>
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
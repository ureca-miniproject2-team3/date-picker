let currentDate = new Date();
let events = []; // 전체 이벤트 데이터를 저장할 배열
let filteredEvents = []; // 선택한 날짜의 이벤트를 저장할 배열
let selectedDateElement = null;

// 공통코드를 저장할 전역 변수
let eventStatusCodes = {};

/**
 * 공통코드를 초기화하는 함수
 */
async function initEventStatusCodes() {
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
      // 이벤트 상태 코드를 매핑
      result.commonCodeDtoListMap['010'].forEach(code => {
        eventStatusCodes[code.code] = code.codeName;
      });
      console.log('마이페이지 상태 공통코드 초기화 완료:', eventStatusCodes);
    } else {
      console.error('공통코드 응답 형식 오류:', result);
      // 기본값 설정
      setDefaultEventStatusCodes();
    }
  } catch (error) {
    console.error('공통코드 초기화 중 오류:', error);
    // 기본값 설정
    setDefaultEventStatusCodes();
  }
}

/**
 * 기본 상태 코드 설정 (공통코드 조회 실패 시)
 */
function setDefaultEventStatusCodes() {
  eventStatusCodes = {
    '001': '미확정',
    '002': '확정',
    '003': '완료',
    '004': '만료'
  };
}

/**
 * 코드로 상태명 가져오기
 */
function getEventStatusName(code) {
  return eventStatusCodes[code] || '알 수 없음';
}

window.onload = async () => {
    // 공통코드 초기화
    await initEventStatusCodes();
    
    const userId = sessionStorage.getItem("userId");
    document.getElementById("loginBtn").style.display = userId ? "none" : "inline-block";
    document.getElementById("logoutBtn").style.display = userId ? "inline-block" : "none";

    if (userId) {
        showCurrentUser(userId);
        document.getElementById("logoutBtn").addEventListener("click", logout);
        
        // 이벤트 불러오기
        await fetchEvents();
    }

    // 이벤트 리스너 등록
    document.getElementById("prevMonth").addEventListener("click", () => {
        currentDate.setMonth(currentDate.getMonth() - 1);
        renderCalendar();
    });
    
    document.getElementById("nextMonth").addEventListener("click", () => {
        currentDate.setMonth(currentDate.getMonth() + 1);
        renderCalendar();
    });

    renderCalendar();
    
    // 페이지 로드 시 오늘 날짜의 이벤트 표시
    const today = new Date();
    filterEventsByDate(today);
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

async function fetchEvents() {
    const userId = sessionStorage.getItem("userId");
    if (!userId) {
        showLoginRequired();
        return;
    }

    try {
        const res = await fetch(`/api/users/${userId}/events`, {
            method: "GET",
            credentials: "same-origin"
        });

        const result = await res.json();
        if (result.result === "success" && Array.isArray(result.eventDtoList)) {
            // 확정 및 완료된 이벤트만 필터링 (공통코드 사용)
            events = result.eventDtoList.filter(e => 
                (e.code === '002' || e.code === '003') && e.timeline // 002: 확정, 003: 완료
            );
            
            // 캘린더에 이벤트 표시 업데이트
            renderCalendar();
            
            // 현재 날짜의 이벤트도 업데이트
            filterEventsByDate(new Date());
        } else {
            showError("이벤트 목록을 불러오는데 실패했습니다.");
        }
    } catch (err) {
        showError(`예외 발생: ${err.message}`);
        console.error("예외:", err);
    }
}

function renderCalendar() {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();
    
    // 월/년 표시 업데이트
    document.getElementById("currentMonthYear").textContent = 
        `${year}년 ${month + 1}월`;
    
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    
    // 캘린더 그리드 초기화
    const calendarGrid = document.getElementById("calendarGrid");
    calendarGrid.innerHTML = "";
    
    // 첫째 날의 요일 (0: 일요일, 6: 토요일)
    const firstDayOfWeek = firstDay.getDay();
    
    // 이전 달의 마지막 날짜
    const prevMonthLastDay = new Date(year, month, 0).getDate();
    
    // 이전 달의 날짜 (회색으로 표시)
    for (let i = 0; i < firstDayOfWeek; i++) {
        const dayElement = document.createElement("div");
        dayElement.className = "calendar-day relative h-16 p-2 text-gray-400 text-right rounded-lg";
        dayElement.textContent = prevMonthLastDay - firstDayOfWeek + i + 1;
        calendarGrid.appendChild(dayElement);
    }
    
    // 오늘 날짜 가져오기
    const today = new Date();
    const todayDate = today.getDate();
    const todayMonth = today.getMonth();
    const todayYear = today.getFullYear();
    
    // 현재 달의 날짜
    for (let i = 1; i <= lastDay.getDate(); i++) {
        const currentDateToCheck = new Date(year, month, i);
        const dayElement = document.createElement("div");
        
        // 기본 스타일 설정
        dayElement.className = "calendar-day relative h-16 p-2 text-right rounded-lg";
        
        // 주말인 경우 색상 변경
        const dayOfWeek = new Date(year, month, i).getDay();
        if (dayOfWeek === 0) { // 일요일
            dayElement.classList.add("text-red-500");
        } else if (dayOfWeek === 6) { // 토요일
            dayElement.classList.add("text-blue-500");
        }
        
        dayElement.textContent = i;
        
        // 이벤트가 있는 날짜인지 확인
        const hasEvents = events.some(event => {
            if (!event.timeline) return false;
            const eventDate = new Date(event.timeline.startTime);
            return eventDate.getDate() === i && 
                   eventDate.getMonth() === month && 
                   eventDate.getFullYear() === year;
        });
        
        if (hasEvents) {
            dayElement.classList.add("has-events");
        }
        
        // 오늘 날짜인 경우 하이라이트
        if (todayDate === i && todayMonth === month && todayYear === year) {
            dayElement.classList.add("font-bold");
            
            // 처음 로드할 때만 오늘 날짜를 선택된 상태로 만듦
            if (!selectedDateElement) {
                dayElement.classList.add("active");
                selectedDateElement = dayElement;
            }
        }
        
        // 날짜 클릭 이벤트
        dayElement.addEventListener("click", () => {
            // 활성화된 날짜 스타일 제거
            if (selectedDateElement) {
                selectedDateElement.classList.remove("active");
            }
            
            // 현재 선택된 날짜 활성화
            dayElement.classList.add("active");
            selectedDateElement = dayElement;
            
            // 선택된 날짜 이벤트 필터링 및 표시
            filterEventsByDate(currentDateToCheck);
        });
        
        calendarGrid.appendChild(dayElement);
    }
    
    // 다음 달의 시작 날짜 (회색으로 표시)
    const totalCells = 42; // 6주 x 7일
    const remainingCells = totalCells - (firstDayOfWeek + lastDay.getDate());
    
    for (let i = 1; i <= remainingCells; i++) {
        const dayElement = document.createElement("div");
        dayElement.className = "calendar-day relative h-16 p-2 text-gray-400 text-right rounded-lg";
        dayElement.textContent = i;
        calendarGrid.appendChild(dayElement);
    }
}

function filterEventsByDate(date) {
    // 선택된 날짜 표시
    const options = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' };
    document.getElementById("selectedDate").textContent = date.toLocaleDateString('ko-KR', options);
    
    // 해당 날짜의 이벤트 필터링
    filteredEvents = events.filter(event => {
        if (!event.timeline) return false;
        
        const eventStartDate = new Date(event.timeline.startTime);
        return eventStartDate.getDate() === date.getDate() && 
               eventStartDate.getMonth() === date.getMonth() && 
               eventStartDate.getFullYear() === date.getFullYear();
    });
    
    renderEventList();
}

function renderEventList() {
    const eventListContainer = document.getElementById("eventList");
    eventListContainer.innerHTML = "";
    
    if (filteredEvents.length === 0) {
        eventListContainer.innerHTML = `
            <div class="text-center text-gray-500 py-6">
                <p>이 날짜에 예정된 일정이 없습니다.</p>
            </div>
        `;
        return;
    }
    
    // 시간순으로 정렬
    filteredEvents.sort((a, b) => {
        return new Date(a.timeline.startTime) - new Date(b.timeline.startTime);
    });
    
    filteredEvents.forEach(event => {
        const startDate = new Date(event.timeline.startTime);
        const endDate = new Date(event.timeline.endTime);
        
        const startTime = `${String(startDate.getHours()).padStart(2, '0')}:${String(startDate.getMinutes()).padStart(2, '0')}`;
        const endTime = `${String(endDate.getHours()).padStart(2, '0')}:${String(endDate.getMinutes()).padStart(2, '0')}`;
        
        const memberCount = event.userIds?.length ?? 0;
        const memberNames = event.userNames?.join(', ') ?? '';
        
        // 공통코드를 사용하여 상태명 가져오기
        const statusName = getEventStatusName(event.code);
        
        const eventElement = document.createElement("div");
        eventElement.className = `event-item p-3 pl-4 bg-white rounded-lg shadow-sm hover:shadow-md transition ${event.code === '003' ? "completed" : ""}`;
        eventElement.innerHTML = `
            <div class="flex justify-between items-start">
                <div class="font-medium">${event.title}</div>
                <div class="text-xs ${event.code === '003' ? "text-emerald-600" : "text-green-600"}">
                    ${statusName}
                </div>
            </div>
            <div class="text-sm text-indigo-600 mt-1">
                🕓 ${startTime} ~ ${endTime}
            </div>
            <div class="text-sm text-gray-600 mt-1">
                👥 ${memberCount}명: ${memberNames}
            </div>
        `;
        
        // 클릭 시 이벤트 상세 페이지로 이동
        eventElement.addEventListener("click", () => {
            location.href = `event.html?id=${event.eventId}`;
        });
        
        eventListContainer.appendChild(eventElement);
    });
}

function showLoginRequired() {
    const eventListContainer = document.getElementById("eventList");
    eventListContainer.innerHTML = `
        <div class="text-center py-8">
            <p class="text-lg font-semibold mb-2">로그인이 필요합니다</p>
            <p class="text-sm text-gray-500">일정을 보려면 로그인 해주세요.</p>
        </div>
    `;
}

function showError(message) {
    const eventListContainer = document.getElementById("eventList");
    eventListContainer.innerHTML = `
        <div class="text-center py-8">
            <p class="text-lg font-semibold mb-2 text-red-500">${message}</p>
        </div>
    `;
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
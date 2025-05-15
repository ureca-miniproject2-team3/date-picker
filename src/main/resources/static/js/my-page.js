let currentDate = new Date();
let events = []; // 전체 이벤트 데이터를 저장할 배열
let filteredEvents = []; // 선택한 날짜의 이벤트를 저장할 배열
let selectedDateElement = null;

window.onload = async () => {
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
            // 확정 및 완료된 이벤트만 필터링
            events = result.eventDtoList.filter(e => 
                (e.status === "CHECKED" || e.status === "COMPLETED") && e.timeline
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
        
        // 오늘 날짜인 경우 스타일 추가
        if (i === todayDate && month === todayMonth && year === todayYear) {
            dayElement.classList.add("active");
        }
        
        // 날짜 클릭 이벤트
        dayElement.addEventListener("click", () => {
            if (selectedDateElement) {
                selectedDateElement.classList.remove("active");
            }
            selectedDateElement = dayElement;
            dayElement.classList.add("active");
            
            const selectedDate = new Date(year, month, i);
            filterEventsByDate(selectedDate);
        });
        
        calendarGrid.appendChild(dayElement);
    }
}

function filterEventsByDate(date) {
    // 선택된 날짜 표시 업데이트
    const options = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' };
    document.getElementById("selectedDate").textContent = date.toLocaleDateString('ko-KR', options);
    
    // 해당 날짜의 이벤트 필터링
    filteredEvents = events.filter(event => {
        if (!event.timeline) return false;
        const eventDate = new Date(event.timeline.startTime);
        return eventDate.getDate() === date.getDate() && 
               eventDate.getMonth() === date.getMonth() && 
               eventDate.getFullYear() === date.getFullYear();
    });
    
    // 이벤트 목록 렌더링
    renderEventList();
}

function renderEventList() {
    const eventList = document.getElementById("eventList");
    eventList.innerHTML = "";
    
    if (filteredEvents.length === 0) {
        eventList.innerHTML = '<div class="text-gray-500 text-center py-4">등록된 이벤트가 없습니다.</div>';
        return;
    }
    
    filteredEvents.forEach(event => {
        const eventElement = document.createElement("div");
        eventElement.className = `event-item p-4 bg-white rounded-lg shadow-sm ${event.status === "COMPLETED" ? "completed" : ""}`;
        
        const startTime = new Date(event.timeline.startTime);
        const endTime = new Date(event.timeline.endTime);
        
        const timeOptions = { hour: '2-digit', minute: '2-digit' };
        const timeStr = `${startTime.toLocaleTimeString('ko-KR', timeOptions)} - ${endTime.toLocaleTimeString('ko-KR', timeOptions)}`;
        
        eventElement.innerHTML = `
            <div class="font-semibold">${event.title}</div>
            <div class="text-sm text-gray-600">${timeStr}</div>
            <div class="text-sm text-gray-500">${event.status === "COMPLETED" ? "완료" : "확정"}</div>
        `;
        
        eventList.appendChild(eventElement);
    });
}

function showLoginRequired() {
    alert("로그인이 필요한 서비스입니다.");
    window.location.href = "/login.html";
}

function showError(message) {
    alert(message);
}

function logout() {
    sessionStorage.removeItem("userId");
    window.location.href = "/";
} 
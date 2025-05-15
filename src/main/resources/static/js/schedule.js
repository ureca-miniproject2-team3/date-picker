const selectedDateInput = document.getElementById("selectedDate");
const calendar = document.getElementById("calendar");
const calendarWrapper = document.getElementById("calendarWrapper");
const timeGrid = document.getElementById("timeGrid");
const dragStartInput = document.getElementById("dragStart");
const dragEndInput = document.getElementById("dragEnd");
const csrfInput = document.getElementById("_csrf");
const dropdownStart = document.getElementById("dropdownStartTime");
const dropdownEnd = document.getElementById("dropdownEndTime");

let currentYear, currentMonth;
let validDates = [];
let selectedDate = null;
let isDragging = false;
let startCell = null;

const urlParams = new URLSearchParams(location.search);
const eventId = urlParams.get("eventId");

if (!eventId) {
    Swal.fire({ icon: 'error', title: '오류', text: 'eventId가 없습니다.' });
    throw new Error("eventId가 없습니다.");
}

async function fetchCsrfToken() {
    const res = await fetch("/api/auth/csrf-token", { credentials: "same-origin" });
    const data = await res.json();
    csrfInput.value = data.token;
}

async function fetchEventData(eventId) {
    const res = await fetch(`/api/events/${eventId}`);
    if (!res.ok) throw new Error(`이벤트 정보 로드 실패: ${res.status}`);
    const { eventDto } = await res.json();
    return eventDto;
}

function setInitialMonthFromEventDates(dates) {
    if (!dates || dates.length === 0) return;
    const sorted = [...dates].sort((a, b) => new Date(a) - new Date(b));
    const nearest = new Date(sorted[0]);
    currentYear = nearest.getFullYear();
    currentMonth = nearest.getMonth() + 1;
}

function updateSelectedDate(dateStr) {
    document.querySelectorAll(".selected-date").forEach(el => el.classList.remove("selected-date"));
    document.querySelectorAll(".event-date-btn").forEach(b => b.classList.remove("bg-blue-500", "text-white"));
    const calendarBtn = document.querySelector(`#calendar button[data-date='${dateStr}']`);
    if (calendarBtn) calendarBtn.classList.add("selected-date");
    const match = document.querySelector(`.event-date-btn[data-date='${dateStr}']`);
    if (match) match.classList.add("bg-blue-500", "text-white");
    selectedDateInput.value = dateStr;
    selectedDate = dateStr;
}

function clearSelectedDate() {
    document.querySelectorAll(".selected-date").forEach(el => el.classList.remove("selected-date"));
    document.querySelectorAll(".event-date-btn").forEach(b => b.classList.remove("bg-blue-500", "text-white"));
    selectedDateInput.value = "";
    selectedDate = null;
}

function toggleDateSelection(dateStr) {
    if (selectedDate === dateStr) {
        clearSelectedDate();
    } else {
        updateSelectedDate(dateStr);
    }
}

function renderEventDateButtons(dates) {
    const container = document.getElementById("eventDateButtons");
    container.innerHTML = "";
    dates.forEach(dateStr => {
        const btn = document.createElement("button");
        btn.className = "event-date-btn px-3 py-1 rounded-full border text-sm bg-white hover:bg-blue-100 transition";
        btn.dataset.date = dateStr;
        btn.textContent = dateStr;
        btn.addEventListener("click", () => toggleDateSelection(dateStr));
        container.appendChild(btn);
    });
}

const generateCalendar = (year, month) => {
    calendar.innerHTML = "";
    document.getElementById("calendar-title").textContent = `${year}.${String(month).padStart(2, "0")}`;
    const firstDay = new Date(year, month - 1, 1);
    const lastDay = new Date(year, month, 0);
    const startDay = firstDay.getDay();
    const totalDays = lastDay.getDate();
    for (let i = 0; i < startDay; i++) calendar.appendChild(document.createElement("div"));
    for (let d = 1; d <= totalDays; d++) {
        const dateStr = `${year}-${String(month).padStart(2, "0")}-${String(d).padStart(2, "0")}`;
        const btn = document.createElement("button");
        btn.textContent = d;
        btn.className = "py-1 hover:bg-blue-100 transition";
        btn.dataset.date = dateStr;
        if (!validDates.includes(dateStr)) {
            btn.classList.add("date-disabled");
        } else {
            btn.addEventListener("click", () => toggleDateSelection(dateStr));
        }
        calendar.appendChild(btn);
    }
};

document.getElementById("prevMonth").addEventListener("click", () => {
    currentMonth--;
    if (currentMonth < 1) {
        currentMonth = 12;
        currentYear--;
    }
    generateCalendar(currentYear, currentMonth);
});

document.getElementById("nextMonth").addEventListener("click", () => {
    currentMonth++;
    if (currentMonth > 12) {
        currentMonth = 1;
        currentYear++;
    }
    generateCalendar(currentYear, currentMonth);
});

document.getElementById("calendarToggleBtn").addEventListener("click", () => {
    calendarWrapper.classList.toggle("hidden");
});

const times = [];
for (let h = 0; h < 24; h++) {
    times.push(`${String(h).padStart(2, "0")}:00`);
    times.push(`${String(h).padStart(2, "0")}:30`);
}
times.forEach(t => {
    const div = document.createElement("div");
    div.className = "time-cell";
    div.dataset.time = t;
    div.textContent = t;
    timeGrid.appendChild(div);
});

function fillDropdown(select, times) {
    times.forEach(t => {
        const option = document.createElement("option");
        option.value = t;
        option.textContent = t;
        select.appendChild(option);
    });
}
fillDropdown(dropdownStart, times);
fillDropdown(dropdownEnd, times);

function connectWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        
        // 개인 알림 구독
        const userId = sessionStorage.getItem('userId');
        if (userId) {
            stompClient.subscribe(`/user/${userId}/queue/notifications`, function(notification) {
                const message = JSON.parse(notification.body);
                handleNotification(message);
            });
        }
        
        // 이벤트 관련 알림 구독
        if (eventId) {
            stompClient.subscribe(`/topic/events/${eventId}`, function(notification) {
                const message = JSON.parse(notification.body);
                handleEventNotification(message);
            });
        }
    }, function(error) {
        console.error('WebSocket 연결 실패:', error);
        setTimeout(connectWebSocket, 5000); // 5초 후 재연결 시도
    });
}

function handleNotification(message) {
    // 알림 메시지 처리
    alertify.notify(message.content, message.type || 'info', 5, function() {
        console.log('알림이 닫혔습니다');
    });
    
    // 필요한 경우 페이지 새로고침 또는 데이터 업데이트
    if (message.type === 'refresh') {
        location.reload();
    }
}

function handleEventNotification(message) {
    // 이벤트 관련 알림 처리
    switch (message.type) {
        case 'SCHEDULE_ADDED':
            alertify.success('새로운 스케줄이 추가되었습니다');
            // 스케줄 목록 새로고침
            loadExistingSchedules();
            break;
        case 'SCHEDULE_UPDATED':
            alertify.info('스케줄이 수정되었습니다');
            loadExistingSchedules();
            break;
        case 'SCHEDULE_DELETED':
            alertify.warning('스케줄이 삭제되었습니다');
            loadExistingSchedules();
            break;
        default:
            alertify.notify(message.content, message.type || 'info', 5);
    }
}

// 초기화
(async () => {
    await fetchCsrfToken();
    const eventData = await fetchEventData(eventId);
    document.getElementById("scheduleTitle").textContent = `${eventData.title} 스케줄 등록`;
    validDates = eventData.dates;
    setInitialMonthFromEventDates(validDates);
    generateCalendar(currentYear, currentMonth);
    renderEventDateButtons(validDates);
    
    // WebSocket 연결
    connectWebSocket();
})();

// 페이지 언로드 시 WebSocket 연결 해제
window.addEventListener('beforeunload', function() {
    if (stompClient) {
        stompClient.disconnect();
    }
}); 
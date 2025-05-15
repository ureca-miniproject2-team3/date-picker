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

// 초기화
(async () => {
    await fetchCsrfToken();
    const eventData = await fetchEventData(eventId);
    document.getElementById("scheduleTitle").textContent = `${eventData.title} 스케줄 등록`;
    validDates = eventData.dates;
    setInitialMonthFromEventDates(validDates);
    generateCalendar(currentYear, currentMonth);
    renderEventDateButtons(validDates);
})(); 
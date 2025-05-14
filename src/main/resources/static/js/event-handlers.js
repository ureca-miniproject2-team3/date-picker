// event-handlers.js - event.html을 위한 이벤트 핸들러 및 모달 관리

// 이벤트 편집을 위한 전역 변수
let editEventId = null;
let existingDates = new Set();
let selectedNewDates = new Set();
let editCurrentYear = 2025;
let editCurrentMonth = 5;

// 오늘 날짜 계산
const editToday = new Date();
const editTodayYear = editToday.getFullYear();
const editTodayMonth = editToday.getMonth() + 1;
const editTodayDate = editToday.getDate();

// 스케줄 편집을 위한 전역 변수
let editingScheduleId = null;
let editingScheduleDate = null;
let isDragging = false;
let startCell = null;
let cells = [];

/**
 * 초대 모달 표시
 */
function showInviteModal() {
    // 이벤트 상태가 미확정(unchecked)인지 확인
    if (currentEvent && currentEvent.status !== 'unchecked') {
        Swal.fire({
            title: '상태 오류',
            text: '미확정 상태의 이벤트만 사용자를 초대할 수 있습니다.',
            icon: 'warning',
            confirmButtonColor: '#7c6dfa'
        });
        return;
    }

    document.getElementById('inviteModal').classList.remove('hidden');
    document.getElementById('searchEmail').focus();
    document.getElementById('searchEmail').addEventListener('input', searchUsers);
}

/**
 * 초대 모달 닫기
 */
function closeInviteModal() {
    document.getElementById('inviteModal').classList.add('hidden');
    document.getElementById('searchEmail').removeEventListener('input', searchUsers);
}

/**
 * 이벤트 편집 - 모달 열기 및 데이터 초기화
 * @param {string} eventId - 이벤트 ID
 * @param {string} currentTitle - 현재 이벤트 제목
 * @param {string} eventIdForDates - 날짜를 위한 이벤트 ID
 */
function editEvent(eventId, currentTitle, eventIdForDates) {
    // 이벤트 상태가 미확정(unchecked)인지 확인
    if (currentEvent && currentEvent.status !== 'unchecked') {
        Swal.fire({
            title: '상태 오류',
            text: '확정되지 않은 이벤트만 수정할 수 있습니다.',
            icon: 'warning',
            confirmButtonColor: '#7c6dfa'
        });
        return;
    }

    // 모달 상태 초기화
    editEventId = eventId;
    existingDates = new Set();
    selectedNewDates = new Set();

    // 현재 날짜로 캘린더 초기화
    editCurrentYear = editTodayYear;
    editCurrentMonth = editTodayMonth;

    // 기존 날짜 가져오기
    const editButton = document.querySelector(`button[data-dates][onclick*="'${eventId}'"]`);
    const dates = JSON.parse(editButton.getAttribute('data-dates'));

    // 기존 날짜 설정
    dates.forEach(date => existingDates.add(date));

    // 제목 설정
    document.getElementById('editEventTitle').value = currentTitle;

    // 캘린더 생성
    generateEditCalendar(editCurrentYear, editCurrentMonth);

    // 모달 표시
    document.getElementById('editEventModal').classList.remove('hidden');
}

/**
 * 이벤트 편집 모달 닫기
 */
function closeEditEventModal() {
    document.getElementById('editEventModal').classList.add('hidden');
}

/**
 * 편집 캘린더 생성
 * @param {number} year - 연도
 * @param {number} month - 월
 */
function generateEditCalendar(year, month) {
    const calendar = document.getElementById("edit-calendar");
    calendar.innerHTML = "";
    const firstDay = new Date(year, month - 1, 1);
    const lastDay = new Date(year, month, 0);
    const startWeekday = firstDay.getDay();
    const totalDays = lastDay.getDate();

    // 첫 번째 날 이전에 빈 셀 추가
    for (let i = 0; i < startWeekday; i++) calendar.appendChild(document.createElement("div"));

    // 날짜 버튼 생성
    for (let day = 1; day <= totalDays; day++) {
        const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
        const btn = document.createElement("button");
        btn.textContent = day;
        btn.dataset.date = dateStr;
        btn.className = "date-btn py-2 rounded-full hover:bg-[#e5dbff] cursor-pointer btn-transition";

        // 지난 날짜 비활성화
        const isPast = year < editTodayYear || 
                      (year === editTodayYear && month < editTodayMonth) || 
                      (year === editTodayYear && month === editTodayMonth && day < editTodayDate);

        if (isPast) {
            btn.classList.add("date-disabled");
        } else {
            // 기존 날짜를 선택 해제할 수 없도록 표시
            if (existingDates.has(dateStr)) {
                btn.classList.add("existing-date");
            } 
            // 새로 선택한 날짜 표시
            else if (selectedNewDates.has(dateStr)) {
                btn.classList.add("selected");
            }

            // 클릭 이벤트 추가
            btn.addEventListener("click", () => {
                // 기존 날짜는 선택 해제할 수 없음
                if (existingDates.has(dateStr)) {
                    return;
                }

                // 새로 선택한 날짜 토글
                if (selectedNewDates.has(dateStr)) {
                    selectedNewDates.delete(dateStr);
                    btn.classList.remove("selected");
                } else {
                    selectedNewDates.add(dateStr);
                    btn.classList.add("selected");
                }
            });
        }

        calendar.appendChild(btn);
    }

    // 캘린더 제목 업데이트
    document.getElementById("edit-calendar-title").textContent = `${year}.${String(month).padStart(2, '0')}`;
}

/**
 * 스케줄 편집 - 모달 열기 및 데이터 초기화
 * @param {string} scheduleId - 스케줄 ID
 * @param {string} startTime - 시작 시간
 * @param {string} endTime - 종료 시간
 * @param {string} scheduleUserId - 스케줄 생성자의 사용자 ID
 */
function editSchedule(scheduleId, startTime, endTime, scheduleUserId) {
    // 현재 사용자가 스케줄 생성자인지 확인
    if (userId !== scheduleUserId) {
        Swal.fire({
            title: '권한 오류',
            text: '스케줄을 생성한 사용자만 수정할 수 있습니다.',
            icon: 'warning',
            confirmButtonColor: '#7c6dfa'
        });
        return;
    }

    editingScheduleId = scheduleId;

    // 날짜 추출 (YYYY-MM-DD 형식)
    const date = new Date(startTime);
    const formattedDate = date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long'
    });

    // ISO 형식 날짜 (YYYY-MM-DD)
    const isoDate = startTime.split('T')[0];
    editingScheduleDate = isoDate;

    // 시간 추출 (HH:MM 형식)
    const startTimeStr = startTime.split('T')[1].substring(0, 5);
    const endTimeStr = endTime.split('T')[1].substring(0, 5);

    // 날짜 표시
    document.getElementById('scheduleDate').textContent = formattedDate;
    document.getElementById('originalDate').value = isoDate;

    // 시간 그리드 생성
    createTimeGrid();

    // 기존 시간 범위 선택
    selectTimeRange(startTimeStr, endTimeStr);

    // 모달 표시
    document.getElementById('editScheduleModal').classList.remove('hidden');
}

/**
 * 스케줄 편집을 위한 시간 그리드 생성
 */
function createTimeGrid() {
    const grid = document.getElementById("timeGrid");
    grid.innerHTML = "";
    cells = [];

    // 30분 단위로 시간 배열 생성
    const times = [];
    for (let h = 0; h < 24; h++) {
        times.push(`${h.toString().padStart(2, '0')}:00`);
        times.push(`${h.toString().padStart(2, '0')}:30`);
    }

    // 시간 셀 생성
    times.forEach(t => {
        const div = document.createElement("div");
        div.className = "time-cell";
        div.dataset.time = t;
        div.textContent = t;
        div.style.height = "40px";
        div.style.borderBottom = "1px solid #e2e8f0";
        div.style.cursor = "pointer";
        div.style.paddingLeft = "1rem";
        div.style.display = "flex";
        div.style.alignItems = "center";

        grid.appendChild(div);
        cells.push(div);
    });

    // 드래그 선택 구현
    cells.forEach(cell => {
        cell.addEventListener("mousedown", () => {
            isDragging = true;
            startCell = cell;
            cells.forEach(c => c.classList.remove("selected"));
            cell.classList.add("selected");
            cell.style.backgroundColor = "#a5a1e4";
            cell.style.color = "white";
        });

        cell.addEventListener("mouseenter", () => {
            if (isDragging && startCell) {
                let startIndex = cells.indexOf(startCell);
                let currentIndex = cells.indexOf(cell);

                cells.forEach(c => {
                    c.classList.remove("selected");
                    c.style.backgroundColor = "";
                    c.style.color = "";
                });

                let [from, to] = [startIndex, currentIndex].sort((a, b) => a - b);
                for (let i = from; i <= to; i++) {
                    cells[i].classList.add("selected");
                    cells[i].style.backgroundColor = "#a5a1e4";
                    cells[i].style.color = "white";
                }
            }
        });
    });

    document.addEventListener("mouseup", () => {
        if (isDragging && startCell) {
            isDragging = false;
            const selected = document.querySelectorAll(".time-cell.selected");
            if (selected.length > 0) {
                const start = selected[0].dataset.time;
                const end = selected[selected.length - 1].dataset.time;
                document.getElementById("dragStart").value = start;
                document.getElementById("dragEnd").value = end;
            }
        }
    });
}

/**
 * 시간 그리드에서 시간 범위 선택
 * @param {string} startTime - 시작 시간 (HH:MM)
 * @param {string} endTime - 종료 시간 (HH:MM)
 */
function selectTimeRange(startTime, endTime) {
    const startIndex = cells.findIndex(c => c.dataset.time === startTime);
    const endIndex = cells.findIndex(c => c.dataset.time === endTime);

    if (startIndex >= 0 && endIndex >= 0) {
        cells.forEach(c => {
            c.classList.remove("selected");
            c.style.backgroundColor = "";
            c.style.color = "";
        });

        const [from, to] = [startIndex, endIndex].sort((a, b) => a - b);
        for (let i = from; i <= to; i++) {
            cells[i].classList.add("selected");
            cells[i].style.backgroundColor = "#a5a1e4";
            cells[i].style.color = "white";
        }

        document.getElementById("dragStart").value = startTime;
        document.getElementById("dragEnd").value = endTime;
    }
}

/**
 * 스케줄 편집 모달 닫기
 */
function closeEditScheduleModal() {
    document.getElementById('editScheduleModal').classList.add('hidden');
    editingScheduleId = null;
    editingScheduleDate = null;
}

/**
 * 이벤트 확정 모달 열기
 * @param {string} startTime - 시작 시간
 * @param {string} endTime - 종료 시간
 */
function showConfirmEventModal(startTime, endTime) {
    // 이벤트 생성자만 확정할 수 있는지 확인
    if (!currentEvent || userId !== currentEvent.ownerId) {
        Swal.fire({
            title: '권한 오류',
            text: '이벤트 확정 권한이 없습니다.',
            icon: 'warning',
            confirmButtonColor: '#7c6dfa'
        });
        return;
    }

    // 이벤트 상태가 미확정(unchecked)인지 확인
    if (currentEvent.status === 'checked') {
        Swal.fire({
            title: '상태 오류',
            text: '이미 확정된 이벤트입니다.',
            icon: 'warning',
            confirmButtonColor: '#7c6dfa'
        });
        return;
    }

    if (currentEvent.status === 'completed') {
        Swal.fire({
            title: '상태 오류',
            text: '완료된 이벤트입니다.',
            icon: 'warning',
            confirmButtonColor: '#7c6dfa'
        });
        return;
    }

    if (currentEvent.status !== 'expired') {
        Swal.fire({
            title: '상태 오류',
            text: '만료된 이벤트입니다.',
            icon: 'warning',
            confirmButtonColor: '#7c6dfa'
        });
        return;
    }

    // 시간 정보 저장
    document.getElementById('confirmEventStartTime').value = startTime;
    document.getElementById('confirmEventEndTime').value = endTime;

    // 시간 정보 표시
    const startDate = new Date(startTime);
    const endDate = new Date(endTime);

    const formatDate = (date) => {
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            weekday: 'short'
        });
    };

    const formatTime = (date) => {
        return date.toLocaleTimeString('ko-KR', {
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    document.getElementById('confirmEventTime').innerHTML = `
        <div>${formatDate(startDate)}</div>
        <div class="text-lg font-bold mt-1">${formatTime(startDate)} ~ ${formatTime(endDate)}</div>
    `;

    // 모달 표시
    document.getElementById('confirmEventModal').classList.remove('hidden');
}

/**
 * 이벤트 확정 모달 닫기
 */
function closeConfirmEventModal() {
    document.getElementById('confirmEventModal').classList.add('hidden');
}

// 문서가 로드될 때 이벤트 핸들러 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 이벤트 편집 모달 탐색
    document.getElementById("editPrevMonth").addEventListener("click", () => {
        editCurrentMonth--;
        if (editCurrentMonth < 1) {
            editCurrentMonth = 12;
            editCurrentYear--;
        }
        generateEditCalendar(editCurrentYear, editCurrentMonth);
    });

    document.getElementById("editNextMonth").addEventListener("click", () => {
        editCurrentMonth++;
        if (editCurrentMonth > 12) {
            editCurrentMonth = 1;
            editCurrentYear++;
        }
        generateEditCalendar(editCurrentYear, editCurrentMonth);
    });

    // 저장 버튼
    document.getElementById("saveEventBtn").addEventListener("click", saveEditEvent);
    document.getElementById("saveScheduleBtn").addEventListener("click", saveEditSchedule);
});

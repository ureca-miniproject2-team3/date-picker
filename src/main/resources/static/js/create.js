const userId = sessionStorage.getItem("userId");
if (!userId) {
    Swal.fire({
        title: '로그인 필요',
        text: '로그인이 필요합니다.',
        icon: 'warning',
        confirmButtonColor: '#7c6dfa'
    }).then(() => {
        window.location.href = "/login.html";
    });
}

const selectedDates = new Set();
let currentYear = 2025;
let currentMonth = 5;

const today = new Date();
const todayYear = today.getFullYear();
const todayMonth = today.getMonth() + 1;
const todayDate = today.getDate();

const generateCalendar = (year, month) => {
    const calendar = document.getElementById("calendar");
    calendar.innerHTML = "";
    const firstDay = new Date(year, month - 1, 1);
    const lastDay = new Date(year, month, 0);
    const startWeekday = firstDay.getDay();
    const totalDays = lastDay.getDate();

    for (let i = 0; i < startWeekday; i++) calendar.appendChild(document.createElement("div"));

    for (let day = 1; day <= totalDays; day++) {
        const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
        const btn = document.createElement("button");
        btn.textContent = day;
        btn.dataset.date = dateStr;
        btn.className = "date-btn py-2 rounded-full hover:bg-[#e5dbff] cursor-pointer btn-transition";

        const isPast = year < todayYear || (year === todayYear && month < todayMonth) || (year === todayYear && month === todayMonth && day < todayDate);
        if (isPast) btn.classList.add("date-disabled");
        else {
            if (selectedDates.has(dateStr)) btn.classList.add("selected");
            btn.addEventListener("click", () => {
                if (selectedDates.has(dateStr)) {
                    selectedDates.delete(dateStr);
                    btn.classList.remove("selected");
                } else {
                    selectedDates.add(dateStr);
                    btn.classList.add("selected");
                }
                updateCreateButton();
            });
        }
        calendar.appendChild(btn);
    }

    document.getElementById("calendar-title").textContent = `${year}.${String(month).padStart(2, '0')}`;
};

const updateCreateButton = () => {
    const btn = document.getElementById("createBtn");
    const title = document.getElementById("title").value.trim();
    if (selectedDates.size > 0 && title !== "") {
        btn.disabled = false;
        btn.classList.remove("cursor-not-allowed", "text-gray-400", "bg-[#e6dfd6]");
        btn.classList.add("bg-[#7c6dfa]", "text-white", "hover:bg-[#6b5de6]");
    } else {
        btn.disabled = true;
        btn.classList.add("cursor-not-allowed", "text-gray-400", "bg-[#e6dfd6]");
        btn.classList.remove("bg-[#7c6dfa]", "text-white", "hover:bg-[#6b5de6]");
    }
};

document.getElementById("title").addEventListener("input", updateCreateButton);
document.getElementById("prevMonth").addEventListener("click", () => {
    currentMonth--; if (currentMonth < 1) { currentMonth = 12; currentYear--; }
    generateCalendar(currentYear, currentMonth);
});
document.getElementById("nextMonth").addEventListener("click", () => {
    currentMonth++; if (currentMonth > 12) { currentMonth = 1; currentYear++; }
    generateCalendar(currentYear, currentMonth);
});

async function getCsrfToken() {
    const res = await fetch("/api/auth/csrf-token", { credentials: "same-origin" });
    const data = await res.json();
    return data.token;
}

document.getElementById("createBtn").addEventListener("click", async () => {
    const title = document.getElementById("title").value.trim();
    const eventDates = Array.from(selectedDates);
    const ownerId = Number(userId);
    const _csrf = await getCsrfToken();

    if (title === "" || eventDates.length === 0) {
        Swal.fire({
            title: '입력 오류',
            text: '제목과 날짜를 모두 입력해주세요.',
            icon: 'warning',
            confirmButtonColor: '#7c6dfa'
        });
        return;
    }

    const formData = new URLSearchParams();
    formData.append("ownerId", ownerId);
    formData.append("title", title);
    eventDates.forEach(date => formData.append("eventDates", date));
    formData.append("_csrf", _csrf);

    try {
        const response = await fetch("/api/events", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: formData
        });

        const result = await response.json();

        if (result.result === "success") {
            Swal.fire({
                title: '성공',
                text: '이벤트가 성공적으로 생성되었습니다.',
                icon: 'success',
                confirmButtonColor: '#7c6dfa'
            }).then(() => {
                window.location.href = "/";
            });
        } else {
            Swal.fire({
                title: '실패',
                text: '이벤트 생성 실패: ' + JSON.stringify(result),
                icon: 'error',
                confirmButtonColor: '#7c6dfa'
            });
        }
    } catch (error) {
        console.error("오류 발생:", error);
        Swal.fire({
            title: '오류',
            text: '서버 오류',
            icon: 'error',
            confirmButtonColor: '#7c6dfa'
        });
    }
});

generateCalendar(currentYear, currentMonth); 
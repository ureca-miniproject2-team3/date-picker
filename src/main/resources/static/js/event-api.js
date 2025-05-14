// event-api.js - event.html을 위한 API 상호작용

// 전역 변수
let currentEvent = null; // 현재 이벤트 데이터 저장
const userMap = {}; // 사용자 ID를 이름에 매핑

/**
 * 보안 요청을 위한 CSRF 토큰 가져오기
 * @returns {Promise<string>} CSRF 토큰
 */
async function getCsrfToken() {
    const res = await fetch('/api/auth/csrf-token', {credentials: 'same-origin'});
    const data = await res.json();
    return data.token;
}

/**
 * 이벤트 데이터 가져오기 및 렌더링
 */
async function renderEvent() {
    try {
        // 이벤트 정보 가져오기
        const eventRes = await fetch(`/api/events/${eventId}`);
        const eventResult = await eventRes.json();
        const event = eventResult?.eventDto;
        currentEvent = event; // 이벤트 데이터를 전역적으로 저장

        if (!event) {
            document.getElementById("eventContainer").innerHTML = `
                <div class="text-red-500 font-semibold">이벤트 정보를 불러오지 못했습니다.</div>`;
            return;
        }

        // 스케줄 정보 가져오기
        const scheduleRes = await fetch(`/api/event/${eventId}/schedules`);
        const scheduleResult = await scheduleRes.json();
        const schedules = scheduleResult?.scheduleDtoList || [];

        // 겹치는 시간대 가져오기
        const overlapRes = await fetch(`/api/schedules/overlap/${eventId}`);
        const overlapResult = await overlapRes.json();
        const timeSlots = overlapResult?.timeSlots || [];
        const maxCount = overlapResult?.maxCount || 0;

        // 참가자 수(내림차순)와 날짜/시간(오름차순)으로 timeSlots 정렬
        timeSlots.sort((a, b) => {
            // 먼저 참가자 수로 정렬(내림차순)
            if (b.userIds.length !== a.userIds.length) {
                return b.userIds.length - a.userIds.length;
            }
            // 그 다음 날짜/시간으로 정렬(오름차순)
            return new Date(a.start) - new Date(b.start);
        });

        // 매핑을 위한 사용자 정보 가져오기
        try {
            const usersRes = await fetch(`/api/users`);
            const usersResult = await usersRes.json();
            const users = usersResult?.userDtoList || [];

            // 사용자 ID를 이름에 매핑하는 맵 생성
            users.forEach(user => {
                userMap[user.id] = user.name || `사용자 ${user.id}`;
            });
        } catch (error) {
            console.error('사용자 가져오기 오류:', error);
            // 오류 발생 시 기본값 설정
            event.userIds.forEach(id => {
                userMap[id] = `사용자 ${id}`;
            });
        }

        // HTML 콘텐츠 렌더링
        renderEventHTML(event, schedules, timeSlots, maxCount);

        // 시간대 차트 렌더링 제거 (요구사항에 따라)
    } catch (error) {
        console.error('이벤트 렌더링 오류:', error);
        document.getElementById("eventContainer").innerHTML = `
            <div class="text-red-500 font-semibold">이벤트 정보를 불러오는 중 오류가 발생했습니다.</div>`;
    }
}

/**
 * 스케줄 삭제
 * @param {string} scheduleId - 삭제할 스케줄의 ID
 */
async function deleteSchedule(scheduleId) {
    const result = await Swal.fire({
        title: '스케줄 삭제',
        text: '이 스케줄을 삭제하시겠습니까?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#7c6dfa',
        cancelButtonColor: '#d33',
        confirmButtonText: '삭제',
        cancelButtonText: '취소'
    });

    if (!result.isConfirmed) return;

    try {
        const csrf = await getCsrfToken();

        const res = await fetch(`/api/schedules/${scheduleId}?userId=${userId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-XSRF-TOKEN': csrf
            }
        });

        const result = await res.json();
        if (result.result === 'success') {
            Swal.fire({
                title: '삭제 완료',
                text: '스케줄이 삭제되었습니다.',
                icon: 'success',
                confirmButtonColor: '#7c6dfa'
            });
            closeEditScheduleModal();
            await renderEvent();
        } else {
            Swal.fire({
                title: '삭제 실패',
                text: '스케줄 삭제에 실패했습니다.',
                icon: 'error',
                confirmButtonColor: '#7c6dfa'
            });
        }
    } catch (error) {
        console.error('스케줄 삭제 오류:', error);
        Swal.fire({
            title: '오류 발생',
            text: '스케줄 삭제 중 오류가 발생했습니다.',
            icon: 'error',
            confirmButtonColor: '#7c6dfa'
        });
    }
}

/**
 * 이벤트 삭제
 * @param {string} eventId - 삭제할 이벤트의 ID
 */
async function deleteEvent(eventId) {
    const result = await Swal.fire({
        title: '이벤트 삭제',
        text: '이 이벤트를 삭제하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#7c6dfa',
        cancelButtonColor: '#d33',
        confirmButtonText: '삭제',
        cancelButtonText: '취소'
    });

    if (!result.isConfirmed) return;

    try {
        const csrf = await getCsrfToken();
        const formData = new URLSearchParams();
        formData.append("userId", userId);
        formData.append("_csrf", csrf);

        const res = await fetch(`/api/events/${eventId}?userId=${userId}`, {
            method: "DELETE",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: formData
        });

        const result = await res.json();
        if (result.result === "success") {
            Swal.fire({
                title: '삭제 완료',
                text: '이벤트가 삭제되었습니다.',
                icon: 'success',
                confirmButtonColor: '#7c6dfa'
            }).then(() => {
                location.href = "/";
            });
        } else {
            Swal.fire({
                title: '삭제 실패',
                text: "삭제 실패: " + JSON.stringify(result),
                icon: 'error',
                confirmButtonColor: '#7c6dfa'
            });
        }
    } catch (error) {
        console.error('이벤트 삭제 오류:', error);
        Swal.fire({
            title: '오류 발생',
            text: '이벤트 삭제 중 오류가 발생했습니다.',
            icon: 'error',
            confirmButtonColor: '#7c6dfa'
        });
    }
}

/**
 * 수정된 이벤트 저장
 */
async function saveEditEvent() {
    const newTitle = document.getElementById('editEventTitle').value.trim();
    if (!newTitle) {
        Swal.fire({
            title: '입력 오류',
            text: '제목을 입력해주세요.',
            icon: 'warning',
            confirmButtonColor: '#7c6dfa'
        });
        return;
    }

    // 모든 날짜 (기존 + 새로 선택된)
    const allDates = [...existingDates, ...selectedNewDates];

    try {
        const csrf = await getCsrfToken();
        const formData = new URLSearchParams();
        formData.append("userId", userId);
        formData.append("eventId", editEventId);
        formData.append("title", newTitle);
        allDates.forEach(d => formData.append("eventDates", d));
        formData.append("_csrf", csrf);

        const res = await fetch("/api/events", {
            method: "PUT",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: formData
        });

        const result = await res.json();
        if (result.result === "success") {
            Swal.fire({
                title: '수정 완료',
                text: '이벤트가 수정되었습니다.',
                icon: 'success',
                confirmButtonColor: '#7c6dfa'
            });
            closeEditEventModal();
            await renderEvent();
        } else {
            Swal.fire({
                title: '수정 실패',
                text: "수정 실패: " + JSON.stringify(result),
                icon: 'error',
                confirmButtonColor: '#7c6dfa'
            });
        }
    } catch (error) {
        console.error('이벤트 수정 오류:', error);
        Swal.fire({
            title: '오류 발생',
            text: '이벤트 수정 중 오류가 발생했습니다.',
            icon: 'error',
            confirmButtonColor: '#7c6dfa'
        });
    }
}

/**
 * 이메일로 사용자 검색
 * @param {Event} e - 입력 이벤트
 */
async function searchUsers(e) {
    const email = e.target.value.trim();
    if (email.length < 3) {
        document.getElementById('searchResults').innerHTML = '';
        return;
    }

    try {
        const res = await fetch(`/api/users/search?email=${encodeURIComponent(email)}`);
        const result = await res.json();
        const user = result.userDto;

        const resultsContainer = document.getElementById('searchResults');
        if (!user) {
            resultsContainer.innerHTML = '<div class="text-gray-500 p-2">검색 결과가 없습니다.</div>';
            return;
        }

        resultsContainer.innerHTML = `
            <div class="p-3 border-b hover:bg-gray-50 flex justify-between items-center">
                <div>
                    <div class="font-medium">${user.email}</div>
                    <div class="text-sm text-gray-500">이름: ${user.name}</div>
                </div>
                <button onclick="inviteUser(${user.id})"
                        class="bg-[#7c6dfa] text-white px-3 py-1 rounded-lg text-sm">
                    초대
                </button>
            </div>
        `;
    } catch (error) {
        console.error('사용자 검색 오류:', error);
        document.getElementById('searchResults').innerHTML =
            '<div class="text-red-500 p-2">사용자 검색 중 오류가 발생했습니다.</div>';
    }
}

/**
 * 이벤트에 사용자 초대
 * @param {number} invitedId - 초대할 사용자의 ID
 */
async function inviteUser(invitedId) {
    try {
        if (invitedId === undefined || invitedId === null) {
            Swal.fire({
                title: '초대 오류',
                text: '초대할 사용자 ID가 유효하지 않습니다.',
                icon: 'error',
                confirmButtonColor: '#7c6dfa'
            });
            return;
        }

        // 사용자가 이미 참여 중인지 확인
        if (currentEvent && currentEvent.userIds && currentEvent.userIds.includes(invitedId)) {
            Swal.fire({
                title: '초대 불가',
                text: '이미 참여 중인 사용자입니다.',
                icon: 'warning',
                confirmButtonColor: '#7c6dfa'
            });
            return;
        }

        const csrf = await getCsrfToken();
        const formData = new URLSearchParams();
        formData.append("inviterId", userId);
        formData.append("eventId", eventId);
        formData.append("invitedIds", invitedId);
        formData.append("_csrf", csrf);

        const res = await fetch("/api/events/invite", {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: formData
        });

        const result = await res.json();
        if (result.result === "success") {
            Swal.fire({
                title: '초대 완료',
                text: '사용자가 초대되었습니다.',
                icon: 'success',
                confirmButtonColor: '#7c6dfa'
            });
            closeInviteModal();
            await renderEvent();
        } else {
            Swal.fire({
                title: '초대 실패',
                text: "초대 실패: " + JSON.stringify(result),
                icon: 'error',
                confirmButtonColor: '#7c6dfa'
            });
        }
    } catch (error) {
        console.error('사용자 초대 오류:', error);
        Swal.fire({
            title: '오류 발생',
            text: '사용자 초대 중 오류가 발생했습니다.',
            icon: 'error',
            confirmButtonColor: '#7c6dfa'
        });
    }
}

/**
 * 수정된 스케줄 저장
 */
async function saveEditSchedule() {
    const scheduleId = editingScheduleId;
    const originalDate = document.getElementById("originalDate").value;
    const start = document.getElementById("dragStart").value;
    const end = document.getElementById("dragEnd").value;

    if (!start || !end) {
        Swal.fire({
            title: '입력 오류',
            text: '시간을 선택해주세요.',
            icon: 'warning',
            confirmButtonColor: '#7c6dfa'
        });
        return;
    }

    // 시작 시간이 종료 시간보다 늦은지 확인
    if (start >= end) {
        Swal.fire({
            title: '입력 오류',
            text: '종료 시간은 시작 시간보다 늦어야 합니다.',
            icon: 'warning',
            confirmButtonColor: '#7c6dfa'
        });
        return;
    }

    try {
        const csrf = await getCsrfToken();

        // 스케줄 데이터 준비
        const scheduleData = new URLSearchParams({
            userId: userId,
            eventId: Number(eventId),
            startTime: `${originalDate}T${start}:00`,
            endTime: `${originalDate}T${end}:00`,
            _csrf: csrf
        });

        // 서버로 전송
        const res = await fetch(`/api/schedules/${scheduleId}`, {
            method: "PUT",
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: scheduleData
        });

        const result = await res.json();

        if (result.result === "success") {
            Swal.fire({
                title: '수정 완료',
                text: '스케줄이 수정되었습니다.',
                icon: 'success',
                confirmButtonColor: '#7c6dfa'
            });
            closeEditScheduleModal();
            await renderEvent();
        } else if (result.result === "forbidden") {
            Swal.fire({
                title: '권한 오류',
                text: '스케줄을 생성한 사용자만 수정할 수 있습니다.',
                icon: 'warning',
                confirmButtonColor: '#7c6dfa'
            });
        } else {
            Swal.fire({
                title: '수정 실패',
                text: "실패: " + JSON.stringify(result),
                icon: 'error',
                confirmButtonColor: '#7c6dfa'
            });
        }
    } catch (err) {
        console.error(err);
        Swal.fire({
            title: '오류 발생',
            text: '서버 오류가 발생했습니다.',
            icon: 'error',
            confirmButtonColor: '#7c6dfa'
        });
    }
}

/**
 * 이벤트 확정
 * 선택된 시간으로 이벤트를 확정합니다.
 */
async function confirmEvent() {
    try {
        // 이벤트 생성자만 확정할 수 있는지 확인
        if (!currentEvent || userId != currentEvent.ownerId) {
            Swal.fire({
                title: '권한 오류',
                text: '이벤트 생성자만 이벤트를 확정할 수 있습니다.',
                icon: 'warning',
                confirmButtonColor: '#7c6dfa'
            });
            return;
        }

        const csrf = await getCsrfToken();

        // 확정할 시간 데이터 가져오기
        const startTime = document.getElementById('confirmEventStartTime').value;
        const endTime = document.getElementById('confirmEventEndTime').value;

        if (!startTime || !endTime) {
            Swal.fire({
                title: '데이터 오류',
                text: '확정할 시간 정보가 없습니다.',
                icon: 'error',
                confirmButtonColor: '#7c6dfa'
            });
            return;
        }

        // 이벤트 확정 데이터 준비
        const formData = new URLSearchParams({
            userId: userId,
            startTime: startTime,
            endTime: endTime,
            _csrf: csrf
        });

        // 서버로 전송
        const res = await fetch('/api/events/check', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: formData
        });

        const result = await res.json();

        if (result.result === "success") {
            Swal.fire({
                title: '확정 완료',
                text: '이벤트가 확정되었습니다.',
                icon: 'success',
                confirmButtonColor: '#7c6dfa'
            });
            closeConfirmEventModal();
            await renderEvent();
        } else {
            Swal.fire({
                title: '확정 실패',
                text: "실패: " + JSON.stringify(result),
                icon: 'error',
                confirmButtonColor: '#7c6dfa'
            });
        }
    } catch (err) {
        console.error('이벤트 확정 오류:', err);
        Swal.fire({
            title: '오류 발생',
            text: '이벤트 확정 중 오류가 발생했습니다.',
            icon: 'error',
            confirmButtonColor: '#7c6dfa'
        });
    }
}

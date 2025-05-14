// event-ui.js - event.html을 위한 UI 렌더링 및 모달 관리

/**
 * 이벤트 HTML 콘텐츠 렌더링
 * @param {Object} event - 이벤트 데이터
 * @param {Array} schedules - 스케줄 데이터
 * @param {Array} timeSlots - 시간대 데이터
 * @param {number} maxCount - 최대 참가자 수
 */
function renderEventHTML(event, schedules, timeSlots, maxCount) {
    const container = document.getElementById("eventContainer");
    container.innerHTML = `
        <div class="mb-4">
            <a href="/index.html" class="text-[#7c6dfa] hover:text-[#6a5cd6] flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                </svg>
            </a>
        </div>
        <div class="flex justify-between items-center border-b pb-5">
            <div class="flex items-center">
                <h1 class="text-3xl font-bold">${event.title}</h1>
                ${event.status ? `
                <span class="ml-3 px-3 py-1 text-sm rounded-full ${
                    event.status === 'checked' ? 'bg-green-100 text-green-800' : 
                    event.status === 'unchecked' ? 'bg-yellow-100 text-yellow-800' : 
                    event.status === 'completed' ? 'bg-blue-100 text-blue-800' : 
                    event.status === 'expired' ? 'bg-gray-100 text-gray-800' : ''
                }">
                    ${
                        event.status === 'checked' ? '확정' : 
                        event.status === 'unchecked' ? '미확정' : 
                        event.status === 'completed' ? '완료' : 
                        event.status === 'expired' ? '만료' : ''
                    }
                </span>
                ` : ''}
            </div>
            ${userId === event.ownerId ? `
            <div class="flex space-x-2">
                ${event.status === 'unchecked' ? `
                <button onclick="editEvent('${event.eventId}', '${event.title}', ${event.eventId})"
                        data-dates='${JSON.stringify(event.eventDates)}'
                        class="bg-yellow-400 hover:bg-yellow-300 text-white px-4 py-2 rounded-full text-sm flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                    <span class="leading-normal">이벤트 수정</span>
                </button>
                ` : `
                <button disabled
                        class="bg-gray-400 cursor-not-allowed text-white px-4 py-2 rounded-full text-sm flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                    <span class="leading-normal">이벤트 수정</span>
                </button>
                `}
                <button onclick="deleteEvent('${event.eventId}')"
                        class="bg-red-400 hover:bg-red-300 text-white px-4 py-2 rounded-full text-sm flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                    <span class="leading-normal">이벤트 삭제</span>
                </button>
                ${event.status === 'unchecked' ? `
                <button onclick="showInviteModal()" class="bg-[#7c6dfa] hover:bg-[#6a5cd6] text-white px-4 py-2 rounded-full text-sm flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                    </svg>
                    사용자 초대
                </button>
                ` : `
                <button disabled class="bg-gray-400 cursor-not-allowed text-white px-4 py-2 rounded-full text-sm flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                    </svg>
                    사용자 초대
                </button>
                `}
            </div>
            ` : ''}
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
                <h2 class="text-lg font-semibold mb-3 flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1 text-[#7c6dfa]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                    </svg>
                    참여자 (${event.userIds.length}명)
                </h2>
                <div class="flex flex-wrap gap-2">
                    ${event.userIds.map(id => {
                        const userName = userMap[id] || `사용자 ${id}`;
                        const initial = userName.charAt(0);
                        return `
                        <div class="bg-[#f3f0ea] px-3 py-1.5 rounded-xl text-sm flex items-center">
                            <span class="w-6 h-6 rounded-full bg-[#7c6dfa] text-white flex items-center justify-center text-xs mr-2">
                                ${initial}
                            </span>
                            ${userName}
                        </div>
                        `;
                    }).join('')}
                </div>
            </div>

            <div>
                <h2 class="text-lg font-semibold mb-3 flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1 text-[#7c6dfa]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                    이벤트 날짜
                </h2>
                <div class="flex flex-wrap gap-2">
                    ${event.eventDates.map(d => {
                        const date = new Date(d);
                        const options = {weekday: 'short', month: 'short', day: 'numeric'};
                        const formattedDate = date.toLocaleDateString('ko-KR', options);
                        return `
                            <div class="bg-[#fcf8f3] px-3 py-1.5 rounded-full text-sm flex items-center">
                                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 text-[#7c6dfa]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                </svg>
                                ${formattedDate}
                            </div>
                        `;
                    }).join('')}
                </div>
            </div>
        </div>

        <!-- 가장 많이 겹치는 시간대 -->
        <div class="bg-white border border-gray-100 rounded-xl shadow-sm p-5 mt-4">
            <h2 class="text-lg font-semibold mb-4 flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1 text-[#7c6dfa]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                </svg>
                가장 많이 가능한 시간
                <span class="text-[#7c6dfa] font-bold ml-1">(${maxCount}명)</span>
            </h2>

            ${timeSlots.length > 0
                ? `<div class="space-y-2">
                    ${timeSlots.map((slot, index) => {
                        const startTime = new Date(slot.start);
                        const endTime = new Date(slot.end);

                        const formatTimeOnly = (date) => {
                            return date.toLocaleTimeString('ko-KR', {
                                hour: '2-digit',
                                minute: '2-digit'
                            });
                        };

                        const formatDateWithDay = (date) => {
                            return date.toLocaleDateString('ko-KR', {
                                month: 'long',
                                day: 'numeric',
                                weekday: 'short'
                            });
                        };

                        const percentage = (slot.userIds.length / maxCount) * 100;
                        const isBestTime = slot.userIds.length === maxCount;

                        return `
                            <div class="time-slot ${isBestTime ? 'border-2 border-[#7c6dfa] shadow-md' : 'border border-gray-100'} bg-white rounded-lg overflow-hidden ${isBestTime && userId === event.ownerId && event.status === 'unchecked' ? 'cursor-pointer hover:bg-gray-50' : ''}" 
                                 style="height: ${isBestTime ? '76px' : '60px'}"
                                 ${isBestTime && userId === event.ownerId && event.status === 'unchecked' ? `onclick="showConfirmEventModal('${slot.start}', '${slot.end}')"` : ''}>
                                <div class="time-slot-bg" style="width: ${percentage}%"></div>
                                <div class="time-slot-content">
                                    <div>
                                        <div class="${isBestTime ? 'font-bold text-[#7c6dfa] text-base leading-normal' : 'font-semibold leading-normal'}">
                                            ${formatDateWithDay(startTime)} ${formatTimeOnly(startTime)} ~ ${formatTimeOnly(endTime)}
                                            ${isBestTime ? '<span class="ml-2 text-xs bg-[#7c6dfa] text-white px-2 py-0.5 rounded-full font-bold">BEST</span>' : ''}
                                            ${isBestTime && userId === event.ownerId && event.status === 'unchecked' ? '<span class="ml-2 text-xs bg-green-500 text-white px-2 py-0.5 rounded-full font-bold">확정 가능</span>' : ''}
                                        </div>
                                        <div class="text-xs text-gray-500 mt-1.5 flex flex-wrap leading-relaxed">
                                            ${slot.userIds.map(uid => `
                                                <span class="user-badge">${userMap[uid] || `사용자 ${uid}`}</span>
                                            `).join('')}
                                        </div>
                                    </div>
                                    <div class="user-count text-lg font-bold ${isBestTime ? 'text-[#7c6dfa]' : 'text-gray-600'}">
                                        ${slot.userIds.length}명
                                    </div>
                                </div>
                            </div>`;
                    }).join('')}
                </div>
`
                : '<div class="text-gray-500 py-8 text-center">등록된 스케줄이 없습니다.</div>'
            }
        </div>

        <!-- 스케줄 타임 그리드 뷰 -->
        <div class="bg-white border border-gray-100 rounded-xl shadow-sm p-5 mt-4">
            <h2 class="text-lg font-semibold mb-4 flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1 text-[#7c6dfa]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
                등록된 스케줄
            </h2>

            ${schedules.length > 0
                ? `<div class="overflow-x-auto">
                    ${(() => {
                        // 날짜별로 스케줄 그룹화
                        const schedulesByDate = {};
                        schedules.forEach(schedule => {
                            const startTime = new Date(schedule.startTime);
                            const dateStr = startTime.toLocaleDateString('ko-KR', {
                                year: 'numeric',
                                month: 'long',
                                day: 'numeric'
                            });

                            if (!schedulesByDate[dateStr]) {
                                schedulesByDate[dateStr] = [];
                            }
                            schedulesByDate[dateStr].push(schedule);
                        });

                        // 날짜 정렬
                        const sortedDates = Object.keys(schedulesByDate).sort((a, b) => {
                            return new Date(a) - new Date(b);
                        });

                        // 스케줄 시간 범위 계산
                        let earliestHour = 24;
                        let latestHour = 0;

                        // 모든 스케줄을 순회하며 가장 이른 시간과 가장 늦은 시간 찾기
                        schedules.forEach(schedule => {
                            const startTime = new Date(schedule.startTime);
                            const endTime = new Date(schedule.endTime);

                            // 시간만 추출 (시간 + 분/60)
                            const startHourDecimal = startTime.getHours() + (startTime.getMinutes() / 60);
                            const endHourDecimal = endTime.getHours() + (endTime.getMinutes() / 60);

                            // 가장 이른 시간과 가장 늦은 시간 업데이트
                            earliestHour = Math.min(earliestHour, startHourDecimal);
                            latestHour = Math.max(latestHour, endHourDecimal);
                        });

                        // 시간 범위에 여유 추가 (가장 이른 시간 - 3시간 ~ 가장 늦은 시간 + 3시간)
                        let startHour = Math.max(0, Math.floor(earliestHour - 3));
                        let endHour = Math.min(23, Math.ceil(latestHour + 3));

                        // 스케줄이 없는 경우 기본값 설정 (9시 ~ 18시)
                        if (schedules.length === 0) {
                            startHour = 9;
                            endHour = 18;
                        }

                        const timeSlots = [];
                        for (let hour = startHour; hour <= endHour; hour++) {
                            timeSlots.push(`${String(hour).padStart(2, '0')}:00`);
                            timeSlots.push(`${String(hour).padStart(2, '0')}:30`);
                        }

                        // 마지막 시간 추가 (endHour + 1):00
                        if (endHour < 24) {
                            timeSlots.push(`${String(endHour + 1).padStart(2, '0')}:00`);
                        } else {
                            timeSlots.push(`24:00`);
                        }

                        // 각 날짜별 시간대별 스케줄 카운트
                        const scheduleCountByDateAndTime = {};
                        sortedDates.forEach(dateStr => {
                            scheduleCountByDateAndTime[dateStr] = {};
                            timeSlots.forEach(timeSlot => {
                                scheduleCountByDateAndTime[dateStr][timeSlot] = [];
                            });
                        });

                        // 스케줄을 시간대별로 분류
                        schedules.forEach(schedule => {
                            const startTime = new Date(schedule.startTime);
                            const endTime = new Date(schedule.endTime);
                            const dateStr = startTime.toLocaleDateString('ko-KR', {
                                year: 'numeric',
                                month: 'long',
                                day: 'numeric'
                            });

                            // 날짜가 scheduleCountByDateAndTime에 없는 경우 건너뛰기 (다른 날짜의 스케줄)
                            if (!scheduleCountByDateAndTime[dateStr]) {
                                return;
                            }

                            // 시작 시간과 종료 시간 사이의 모든 시간대에 스케줄 추가
                            timeSlots.forEach(timeSlot => {
                                const [hour, minute] = timeSlot.split(':').map(Number);
                                const slotTime = new Date(startTime);
                                slotTime.setHours(hour, minute, 0, 0);

                                // 슬롯 시간의 종료 시간 계산 (30분 또는 1시간 후)
                                const slotEndTime = new Date(slotTime);
                                if (minute === 30) {
                                    slotEndTime.setHours(hour + 1, 0, 0, 0);
                                } else {
                                    slotEndTime.setHours(hour, 30, 0, 0);
                                }

                                // 스케줄과 시간 슬롯이 겹치는지 확인
                                // 1. 슬롯 시작 시간이 스케줄 시작과 종료 사이에 있는 경우
                                // 2. 슬롯 종료 시간이 스케줄 시작과 종료 사이에 있는 경우
                                // 3. 스케줄이 슬롯 전체를 포함하는 경우
                                // 4. 스케줄이 슬롯에 일부라도 포함되는 경우
                                if ((slotTime >= startTime && slotTime < endTime) || 
                                    (slotEndTime > startTime && slotEndTime <= endTime) ||
                                    (startTime <= slotTime && endTime >= slotEndTime) ||
                                    (startTime >= slotTime && startTime < slotEndTime) ||
                                    (endTime > slotTime && endTime <= slotEndTime)) {
                                    scheduleCountByDateAndTime[dateStr][timeSlot].push(schedule);
                                }
                            });
                        });

                        // 색상 강도 계산 함수
                        const getColorIntensity = (count, max) => {
                            if (count === 0) return 'bg-gray-50';
                            if (count === 1) return 'bg-[#7c6dfa40]';
                            if (count === 2) return 'bg-[#7c6dfa60]';
                            if (count === 3) return 'bg-[#7c6dfa70]';
                            if (count >= 4) return 'bg-[#7c6dfaa0]';

                            // 예상치 못한 경우를 위한 대비책
                            const intensity = Math.min(Math.floor((count / Math.max(max, 5)) * 5) + 1, 5);
                            return `bg-[#7c6dfa${intensity * 20}]`;
                        };

                        // 최대 겹침 수 계산
                        let maxOverlap = 0;
                        Object.keys(scheduleCountByDateAndTime).forEach(dateStr => {
                            Object.keys(scheduleCountByDateAndTime[dateStr]).forEach(timeSlot => {
                                maxOverlap = Math.max(maxOverlap, scheduleCountByDateAndTime[dateStr][timeSlot].length);
                            });
                        });

                        return `
                            <div class="mb-4 text-sm text-gray-500 flex items-center">
                                <div class="flex items-center space-x-1">
                                    <div class="w-4 h-4 bg-gray-50 border border-gray-200"></div>
                                    <div class="w-4 h-4 bg-[#7c6dfa20]"></div>
                                    <div class="w-4 h-4 bg-[#7c6dfa40]"></div>
                                    <div class="w-4 h-4 bg-[#7c6dfa60]"></div>
                                    <div class="w-4 h-4 bg-[#7c6dfa80]"></div>
                                    <div class="w-4 h-4 bg-[#7c6dfaa0]"></div>
                                </div>
                            </div>

                            <div class="border rounded-lg overflow-hidden">
                                <div class="overflow-x-auto" style="max-width: 100%;">
                                    <div class="grid" style="grid-template-columns: 80px repeat(${sortedDates.length}, minmax(100px, 1fr)); min-width: ${sortedDates.length > 3 ? '800px' : '100%'}; display: grid;">
                                    <!-- 헤더 행 (날짜) -->
                                    <div class="bg-gray-100 p-2 border-b border-r font-medium text-center">시간</div>
                                    ${sortedDates.map(dateStr => {
                                        // Parse the date string properly
                                        const dateParts = dateStr.match(/(\d{4})년\s+(\d{1,2})월\s+(\d{1,2})일/);
                                        let date;

                                        if (dateParts) {
                                            // If we can extract parts from the Korean format
                                            const year = parseInt(dateParts[1]);
                                            const month = parseInt(dateParts[2]);
                                            const day = parseInt(dateParts[3]);
                                            date = new Date(year, month - 1, day);
                                        } else {
                                            // Fallback to direct parsing
                                            date = new Date(dateStr);
                                        }

                                        // Format the date properly
                                        const dayOfWeek = date.toLocaleDateString('ko-KR', { weekday: 'short' });
                                        const month = date.getMonth() + 1;
                                        const day = date.getDate();

                                        // Check if date is valid
                                        if (isNaN(date.getTime())) {
                                            console.error("Invalid date:", dateStr);
                                            return `
                                                <div class="bg-gray-100 p-2 border-b border-r font-medium text-center">
                                                    <div>날짜 오류</div>
                                                    <div>(확인 필요)</div>
                                                </div>
                                            `;
                                        }

                                        return `
                                            <div class="bg-gray-100 p-2 border-b border-r font-medium text-center">
                                                <div>${month}월 ${day}일</div>
                                                <div>(${dayOfWeek})</div>
                                            </div>
                                        `;
                                    }).join('')}

                                    <!-- 시간대 행 -->
                                    ${timeSlots.map(timeSlot => {
                                        return `
                                            <div class="border-b border-r p-2 text-center text-sm text-gray-500">${timeSlot}</div>
                                            ${sortedDates.map(dateStr => {
                                                const schedules = scheduleCountByDateAndTime[dateStr][timeSlot];
                                                const count = schedules.length;
                                                const colorClass = getColorIntensity(count, maxOverlap);

                                                // 툴팁 내용 생성
                                                const tooltipContent = count > 0 
                                                    ? schedules.map(s => {
                                                        const userName = userMap[s.userId] || `사용자 ${s.userId}`;
                                                        const startTime = new Date(s.startTime).toLocaleTimeString('ko-KR', {
                                                            hour: '2-digit',
                                                            minute: '2-digit'
                                                        });
                                                        const endTime = new Date(s.endTime).toLocaleTimeString('ko-KR', {
                                                            hour: '2-digit',
                                                            minute: '2-digit'
                                                        });
                                                        return `${userName}:   ${startTime}~${endTime}`;
                                                    }).join('\\n')
                                                    : '';

                                                // 툴팁 및 title 속성을 위한 텍스트 생성
                                                const tooltipText = count > 0 
                                                    ? `${count}명 참여 가능\n` + schedules.map(s => {
                                                        const userName = userMap[s.userId] || `사용자 ${s.userId}`;
                                                        const startTime = new Date(s.startTime).toLocaleTimeString('ko-KR', {
                                                            hour: '2-digit',
                                                            minute: '2-digit'
                                                        });
                                                        const endTime = new Date(s.endTime).toLocaleTimeString('ko-KR', {
                                                            hour: '2-digit',
                                                            minute: '2-digit'
                                                        });
                                                        return `${userName}:   ${startTime}~${endTime}`;
                                                    }).join('\n')
                                                    : '';

                                            return `
                                                <div class="border-b border-r p-2 ${colorClass} relative group" 
                                                     ${count > 0 ? `title="${tooltipText.replace(/"/g, '&quot;')}"` : ''}>
                                                    ${count > 0 ? `
                                                        <div class="absolute hidden group-hover:block bg-white border border-gray-200 shadow-lg rounded-lg text-sm z-10 tooltip-container" 
                                                             style="min-width: 160px; width: max-content; max-width: 200px; bottom: calc(100% + 1px); max-height: 150px; overflow-y: auto; line-height: 1.1; font-size: 12px; padding: 0;"
                                                             onmouseover="adjustTooltipPosition(this)">
                                                            <div class="px-2 py-0.5 font-semibold text-[#7c6dfa] rounded-t-lg border-b border-gray-100" style="margin: 0; padding-top: 5px; padding-bottom: 5px;">
                                                                ${count}명 참여 가능
                                                            </div>
                                                            <div class="px-2 pt-0 pb-1" style="margin-top: 5px;">
                                                            ${schedules.map(s => {
                                                                const userName = userMap[s.userId] || `사용자 ${s.userId}`;
                                                                const startTime = new Date(s.startTime).toLocaleTimeString('ko-KR', {
                                                                    hour: '2-digit',
                                                                    minute: '2-digit'
                                                                });
                                                                const endTime = new Date(s.endTime).toLocaleTimeString('ko-KR', {
                                                                    hour: '2-digit',
                                                                    minute: '2-digit'
                                                                });
                                                                const isCurrentUser = s.userId === userId;
                                                                return `<div class="flex justify-between items-center" style="width: 100%; line-height: 1.1; margin: 0; padding: 1px 0;">
                                                                    <div class="flex items-center min-w-0 mr-3">
                                                                        <span class="font-medium text-gray-800" style="display: inline-block; word-break: keep-all; white-space: nowrap;">${userName}</span>
                                                                    </div>
                                                                    <span class="text-gray-500 flex-shrink-0 text-right whitespace-nowrap ml-1">${startTime}~${endTime}</span>
                                                                </div>`;
                                                            }).join('')}
                                                            </div>
                                                        </div>
                                                    ` : ''}
                                                </div>
                                            `;
                                        }).join('')}
                                    `;
                                }).join('')}
                                </div>
                            </div>

                            <!-- 기존 리스트 뷰 (접을 수 있게) -->
                            <div class="mt-2">
                                <button id="toggleListViewBtn" class="text-[#7c6dfa] hover:underline text-base flex items-center">
                                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                                    </svg>
                                    <span class="text-base" style="font-weight: bold">스케줄 수정하기</span>
                                </button>
                                <div id="scheduleListView" class="hidden mt-2 space-y-6 max-h-[300px] overflow-y-auto">
                                    ${sortedDates.map(dateStr => {
                                        const dateSchedules = schedulesByDate[dateStr];

                                        return `
                                            <div class="border rounded-lg overflow-hidden">
                                                <div class="bg-[#f0efff] px-4 py-3 font-medium text-[#7c6dfa]">
                                                    ${dateStr}
                                                </div>
                                                <div class="divide-y max-h-64 overflow-y-auto">
                                                    ${dateSchedules.map(schedule => {
                                                        const startTime = new Date(schedule.startTime);
                                                        const endTime = new Date(schedule.endTime);

                                                        const formatTimeOnly = (date) => {
                                                            return date.toLocaleTimeString('ko-KR', {
                                                                hour: '2-digit',
                                                                minute: '2-digit'
                                                            });
                                                        };

                                                        const isCurrentUser = schedule.userId === userId;

                                                        return `
                                                            <div class="px-4 py-3 grid grid-cols-[auto_1fr_auto] items-center gap-2 ${isCurrentUser ? 'bg-[#fafafa]' : ''} ${isCurrentUser ? 'cursor-pointer hover:bg-gray-100' : ''}" 
                                                                ${isCurrentUser ? `onclick="editSchedule('${schedule.scheduleId}', '${schedule.startTime}', '${schedule.endTime}', '${schedule.userId}')"` : ''}>
                                                                <div class="w-10 text-center text-sm text-gray-500">
                                                                    ${formatTimeOnly(startTime)}
                                                                </div>
                                                                <div class="flex items-center">
                                                                    <span class="w-7 h-7 rounded-full ${isCurrentUser ? 'bg-[#7c6dfa]' : 'bg-gray-200'} text-${isCurrentUser ? 'white' : 'gray-700'} flex items-center justify-center text-xs mr-2 flex-shrink-0">
                                                                        ${(userMap[schedule.userId] || `사용자 ${schedule.userId}`).charAt(0)}
                                                                    </span>
                                                                    <div style="min-width: 60px;">
                                                                        <span class="font-medium truncate">${userMap[schedule.userId] || `사용자 ${schedule.userId}`}</span>
                                                                        ${isCurrentUser ? '<span class="ml-2 text-[10px] bg-[#7c6dfa] text-white px-1 py-0.5 rounded-full flex-shrink-0">나</span>' : ''}
                                                                    </div>
                                                                </div>
                                                                <div class="text-sm text-gray-500 text-right whitespace-nowrap ml-2">
                                                                    ${formatTimeOnly(startTime)} ~ ${formatTimeOnly(endTime)}
                                                                    ${isCurrentUser ? '<span class="ml-2 text-xs text-[#7c6dfa]"><svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 inline" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" /></svg></span>' : ''}
                                                                </div>
                                                            </div>
                                                        `;
                                                    }).join('')}
                                                </div>
                                            </div>
                                        `;
                                    }).join('')}
                                </div>
                            </div>

                            <!-- 스크립트 태그 제거 - 이벤트 리스너는 HTML이 DOM에 추가된 후 별도로 설정 -->
                        `;
                    })()}
                </div>`
                : '<div class="text-gray-500 py-8 text-center">등록된 스케줄이 없습니다.</div>'
            }
        </div>

        <div class="mt-8">
            <div class="fixed bottom-6 left-1/2 transform -translate-x-1/2 z-10">
                ${event.status === 'unchecked' ? `
                <button onclick="location.href='/schedule.html?eventId=${event.eventId}'"
                        class="bg-[#7c6dfa] hover:bg-[#6a5cd6] text-white px-5 py-2.5 rounded-full flex items-center shadow-md transition duration-200 ease-in-out hover:shadow-lg">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    <span class="leading-normal">스케줄 생성</span>
                </button>
                ` : `
                <button disabled
                        class="bg-gray-400 cursor-not-allowed text-white px-5 py-2.5 rounded-full flex items-center shadow-md">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    <span class="leading-normal">스케줄 생성</span>
                </button>
                `}
            </div>
        </div>`;

    setTimeout(() => {
        const toggleListViewBtn = document.getElementById('toggleListViewBtn');
        if (toggleListViewBtn) {
            toggleListViewBtn.addEventListener('click', function() {
                const listView = document.getElementById('scheduleListView');
                const isHidden = listView.classList.contains('hidden');

                listView.classList.toggle('hidden');

                // 버튼 텍스트 변경
                this.innerHTML = isHidden
                    ? '<svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7" /></svg> <span class="text-base" style="font-weight: bold">숨기기</span>'
                    : '<svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" /></svg> <span class="text-base" style="font-weight: bold">스케줄 수정하기</span>';
            });
        }
    }, 0);
}

// 툴팁 위치 조정 함수
function adjustTooltipPosition(tooltip) {
    // 툴팁과 뷰포트의 위치 정보 가져오기
    const tooltipRect = tooltip.getBoundingClientRect();
    const viewportWidth = window.innerWidth;
    const parentRect = tooltip.parentElement.getBoundingClientRect();

    // 기본 위치 설정 (중앙 위)
    tooltip.style.left = '50%';
    tooltip.style.transform = 'translateX(-50%)';
    tooltip.style.right = 'auto'; // 기본값으로 right 속성 초기화

    // 왼쪽 가장자리에 가까운 경우
    if (tooltipRect.left < 10) {
        tooltip.style.left = '0';
        tooltip.style.transform = 'translateX(0)';
    }

    // 오른쪽 가장자리에 가까운 경우
    if (tooltipRect.right > viewportWidth - 10) {
        // 가장 오른쪽 셀인 경우 특별 처리
        if (parentRect.right > viewportWidth - 100) {
            tooltip.style.left = 'auto';
            tooltip.style.right = '0';
            tooltip.style.transform = 'translateX(0)';

            // 부모 요소의 오른쪽 가장자리가 뷰포트 오른쪽에 매우 가까운 경우
            // 툴팁을 왼쪽으로 더 이동시켜 잘리지 않도록 함
            const rightOffset = Math.min(viewportWidth - parentRect.right, 0);
            tooltip.style.right = `${Math.abs(rightOffset) + 20}px`;
        } else {
            // 일반적인 오른쪽 가장자리 처리
            tooltip.style.left = 'auto';
            tooltip.style.right = '0';
            tooltip.style.transform = 'translateX(0)';
        }
    }
}

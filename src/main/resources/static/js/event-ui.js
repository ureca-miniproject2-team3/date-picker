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
            <h1 class="text-3xl font-bold">${event.title}</h1>
            ${userId == event.ownerId ? `
            <button onclick="showInviteModal()" class="bg-[#7c6dfa] hover:bg-[#6a5cd6] text-white px-4 py-2 rounded-full text-sm flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                </svg>
                사용자 초대
            </button>
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
                <span class="text-[#7c6dfa] font-bold ml-1">(최대 ${maxCount}명)</span>
            </h2>

            ${timeSlots.length > 0
                ? `<div class="bg-[#f8f8f8] p-4 rounded-xl mb-4">
                    <canvas id="timeSlotChart" class="w-full h-48 mb-4"></canvas>
                </div>
                <div class="space-y-2">
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
                            <div class="time-slot ${isBestTime ? 'border-2 border-[#7c6dfa] shadow-md' : 'border border-gray-100'} bg-white rounded-lg overflow-hidden ${isBestTime ? 'transform hover:scale-102' : ''}" style="height: ${isBestTime ? '76px' : '60px'}">
                                <div class="time-slot-bg" style="background-color: ${isBestTime ? '#e0dbff' : '#e8e4ff'}; width: ${percentage}%"></div>
                                <div class="time-slot-content">
                                    <div>
                                        <div class="font-medium ${isBestTime ? 'text-[#7c6dfa] text-base leading-normal' : 'leading-normal'}">
                                            ${formatDateWithDay(startTime)} ${formatTimeOnly(startTime)} ~ ${formatTimeOnly(endTime)}
                                            ${isBestTime ? '<span class="ml-2 text-xs bg-[#7c6dfa] text-white px-2 py-0.5 rounded-full font-bold">BEST</span>' : ''}
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
                </div>`
                : '<div class="text-gray-500 py-8 text-center">등록된 스케줄이 없습니다.</div>'
            }
        </div>

        <!-- 스케줄 캘린더 뷰 -->
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

                        return `
                            <div class="space-y-6 max-h-[300px] overflow-y-auto">
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

                                                    const isCurrentUser = schedule.userId == userId;

                                                    return `
                                                        <div class="px-4 py-3 flex items-center justify-between ${isCurrentUser ? 'bg-[#fafafa]' : ''} ${isCurrentUser ? 'cursor-pointer hover:bg-gray-100' : ''}" 
                                                            ${isCurrentUser ? `onclick="editSchedule('${schedule.scheduleId}', '${schedule.startTime}', '${schedule.endTime}', '${schedule.userId}')"` : ''}>
                                                            <div class="flex items-center">
                                                                <div class="w-10 text-center text-sm text-gray-500">
                                                                    ${formatTimeOnly(startTime)}
                                                                </div>
                                                                <div class="ml-4 flex items-center">
                                                                    <span class="w-6 h-6 rounded-full ${isCurrentUser ? 'bg-[#7c6dfa]' : 'bg-gray-200'} text-${isCurrentUser ? 'white' : 'gray-700'} flex items-center justify-center text-xs mr-2">
                                                                        ${(userMap[schedule.userId] || `사용자 ${schedule.userId}`).charAt(0)}
                                                                    </span>
                                                                    <span class="font-medium">${userMap[schedule.userId] || `사용자 ${schedule.userId}`}</span>
                                                                    ${isCurrentUser ? '<span class="ml-2 text-xs bg-[#7c6dfa] text-white px-2 py-0.5 rounded-full">나</span>' : ''}
                                                                </div>
                                                            </div>
                                                            <div class="text-sm text-gray-500">
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
                        `;
                    })()}
                </div>`
                : '<div class="text-gray-500 py-8 text-center">등록된 스케줄이 없습니다.</div>'
            }
        </div>

        <div class="flex justify-between items-center mt-8">
            <div class="space-x-4 flex">
                ${userId == event.ownerId ? `
                <button onclick="editEvent('${event.eventId}', '${event.title}', ${event.eventId})"
                        data-dates='${JSON.stringify(event.eventDates)}'
                        class="bg-yellow-400 hover:bg-yellow-300 text-white px-5 py-2.5 rounded-full flex items-center shadow-sm transition duration-200 ease-in-out hover:shadow">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                    <span class="leading-normal">이벤트 수정</span>
                </button>
                <button onclick="deleteEvent('${event.eventId}')"
                        class="bg-red-400 hover:bg-red-300 text-white px-5 py-2.5 rounded-full flex items-center shadow-sm transition duration-200 ease-in-out hover:shadow">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                    <span class="leading-normal">이벤트 삭제</span>
                </button>
                ` : ''}
            </div>
            <button onclick="location.href='/schedule.html?eventId=${event.eventId}'"
                    class="bg-[#7c6dfa] hover:bg-[#6a5cd6] text-white px-5 py-2.5 rounded-full flex items-center shadow-sm transition duration-200 ease-in-out hover:shadow">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <span class="leading-normal">스케줄 생성</span>
            </button>
        </div>`;
}

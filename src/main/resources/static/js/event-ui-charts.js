// event-ui-charts.js - event.html을 위한 차트 렌더링

/**
 * 시간대 차트 렌더링
 * @param {Array} timeSlots - 시간대 데이터
 * @param {number} maxCount - 최대 참가자 수
 */
function renderTimeSlotChart(timeSlots, maxCount) {
    const ctx = document.getElementById('timeSlotChart').getContext('2d');

    const labels = timeSlots.map(slot => {
        const start = new Date(slot.start);
        const dateStr = start.toLocaleDateString('ko-KR', {
            month: 'short',
            day: 'numeric'
        });
        return `${dateStr} ${start.getHours().toString().padStart(2, '0')}:${start.getMinutes().toString().padStart(2, '0')}`;
    });

    const data = timeSlots.map(slot => slot.userIds.length);

    // 최대 참가자가 있는 데이터 포인트 강조
    const backgroundColors = data.map(value =>
        value === maxCount ? '#7c6dfa' : '#b6adfa'
    );

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: '참여 가능 인원',
                data: data,
                backgroundColor: backgroundColors,
                borderColor: backgroundColors.map(color => color === '#7c6dfa' ? '#6a5adb' : '#9e95e8'),
                borderWidth: 1,
                borderRadius: 6
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            const slotIndex = context.dataIndex;
                            const slot = timeSlots[slotIndex];
                            return `${slot.userIds.length}명 참여 가능`;
                        },
                        afterLabel: function (context) {
                            const slotIndex = context.dataIndex;
                            const slot = timeSlots[slotIndex];
                            return slot.userIds.map(id => userMap[id] || `사용자 ${id}`);
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: maxCount + 1,
                    ticks: {
                        stepSize: 1,
                        font: {
                            size: 12
                        }
                    },
                    title: {
                        display: true,
                        text: '참여 가능 인원 수',
                        font: {
                            size: 14
                        }
                    }
                },
                x: {
                    grid: {
                        display: false
                    },
                    title: {
                        display: true,
                        text: '날짜 및 시간',
                        font: {
                            size: 14
                        }
                    }
                }
            }
        }
    });
}

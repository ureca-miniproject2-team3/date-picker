// event-init.js - event.html을 위한 초기화 코드

// 전역 변수
const eventId = new URLSearchParams(location.search).get("id");
const userId = Number(sessionStorage.getItem("userId"));

// 로그인 및 이벤트 ID 확인
document.addEventListener('DOMContentLoaded', async function () {
    if (!userId) {
        Swal.fire({
            title: '로그인 필요',
            text: '로그인이 필요합니다.',
            icon: 'warning',
            confirmButtonColor: '#7c6dfa'
        }).then(() => {
            location.href = "/login.html";
        });
    } else if (!eventId) {
        Swal.fire({
            title: '오류',
            text: '이벤트 ID가 필요합니다.',
            icon: 'error',
            confirmButtonColor: '#7c6dfa'
        }).then(() => {
            location.href = "/";
        });
    } else {
        // 공통코드 초기화 먼저 수행
        await initEventStatusCodes();
        
        // 이벤트 페이지 초기화
        await renderEvent();
    }
});
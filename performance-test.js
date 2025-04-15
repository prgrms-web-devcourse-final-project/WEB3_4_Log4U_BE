import http from 'k6/http';
import { check, sleep, group } from 'k6';

export const options = {
    stages: [
        { duration: '1m', target: 50 },   // 1분 동안 VU 50명 유지
        { duration: '1m', target: 100 },  // 1분 동안 VU 100명 유지
        { duration: '1m', target: 150 },  // 1분 동안 VU 150명 유지
        { duration: '1m', target: 200 },  // 1분 동안 VU 200명 유지
        { duration: '1m', target: 300 },  // 1분 동안 VU 300명 유지
        { duration: '1m', target: 500 },  // 1분 동안 VU 500명 유지
        { duration: '1m', target: 0 },    // 점진적 종료
    ],
};

const BASE_URL = 'http://spring:8080';

export default function () {
    // 강북구 고정 좌표
    const bounds = {
        south: 37.6335,
        north: 37.6535,
        west: 127.0011,
        east: 127.0211,
    };

    const url = `${BASE_URL}/maps/diaries/marker?south=${bounds.south}&north=${bounds.north}&west=${bounds.west}&east=${bounds.east}`;

    group('Get Diary Markers (Gangbuk-gu fixed bounds)', () => {
        const res = http.get(url, {
            headers: {
                'Content-Type': 'application/json',
            },
        });

        check(res, {
            'status is 200': (r) => r.status === 200,
        });

        sleep(1); // 사용자당 요청 간격
    });
}

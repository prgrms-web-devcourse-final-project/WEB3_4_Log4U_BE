import http from 'k6/http';
import { check, sleep, group } from 'k6';

export const options = {
    stages: [
        { duration: '1m', target: 50 },   // 1분 동안 VU 50명 유지
        { duration: '1m', target: 100 },  // 1분 동안 VU 100명 유지
        { duration: '1m', target: 300 },  // 1분 동안 VU 200명 유지
        { duration: '1m', target: 500 },  // 1분 동안 VU 300명 유지
        { duration: '1m', target: 700 },  // 1분 동안 VU 500명 유지
        { duration: '1m', target: 1000 },  // 1분 동안 VU 500명 유지
        { duration: '1m', target: 0 },    // 점진적 종료
    ],
};

const BASE_URL = 'http://spring:8080';

export default function () {
    // 강북구 고정 좌표
    const bounds = {
        south: 33.0,
        north: 39.5,
        west: 124.01,
        east: 131.0,
        zoom: 1
    };

    const url = `${BASE_URL}/maps/diaries/cluster?south=${bounds.south}&north=${bounds.north}&west=${bounds.west}&east=${bounds.east}&zoom=${bounds.zoom}`;

    group('Get Diary Clusters (Gangbuk-gu fixed bounds)', () => {
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

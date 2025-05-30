import http from 'k6/http';
import { check, group, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '2m', target: 100 },  // stay at 52 VUs
        { duration: '1m', target: 200 },  // ramp-up to 517 VUs (peak time)
        { duration: '2m', target: 300 },  // stay at 517 VUs
        { duration: '1m', target: 500 },   // ramp-down to 52 VUs
        { duration: '2m', target: 700 },   // ramp-down to 52 VUs
        { duration: '2m', target: 900 },   // ramp-down to 52 VUs
        { duration: '1m', target: 0 },    // ramp-down to 0
    ],
};

const BASE_URL = 'http://spring:8080';

export default function () {
    // 1. 클러스터형 조회 API 먼저 호출
    clusterRequest();

    // 2. 그 다음 마커형 개별 다이어리 조회 API 호출
    markerRequest();

    // (optional) sleep 추가
    sleep(1);
}

function clusterRequest() {
    //서울특별시 전체를 커버할 수 있는 남, 북, 동, 서 좌표 범위
    const bounds = {
        south: 37.4133,
        north: 37.7014,
        west: 126.7341,
        east: 127.2693,
        zoom: 12
    };

    const url = `${BASE_URL}/maps/diaries/cluster?south=${bounds.south}&north=${bounds.north}&west=${bounds.west}&east=${bounds.east}&zoom=${bounds.zoom}`;

    const res = http.get(url, {
        headers: {
            'Content-Type': 'application/json',
        },
    });

    check(res, {
        'Cluster API 응답 성공': (r) => r.status === 200,
    });

    console.log(`클러스터 조회 응답 시간: ${res.timings.duration} ms`);
}

function markerRequest() {
    // 강동구 중심 좌표를 기준으로 소범위 설정
    const bounds = {
        south: 37.5459,
        north: 37.5559,
        west: 127.1644,
        east: 127.1744,
    };

    const url = `${BASE_URL}/maps/diaries/marker?south=${bounds.south}&north=${bounds.north}&west=${bounds.west}&east=${bounds.east}`;

    const res = http.get(url, {
        headers: {
            'Content-Type': 'application/json',
        },
    });

    check(res, {
        'Marker API 응답 성공': (r) => r.status === 200,
    });

    console.log(`마커 조회 응답 시간: ${res.timings.duration} ms`);
}

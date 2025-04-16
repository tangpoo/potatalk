import http from 'k6/http';
import {check, sleep} from 'k6';
import ws from 'k6/ws';

export const options = {
  stages: [
    { duration: '10s', target: 10 },
    { duration: '30s', target: 50 },
    { duration: '60s', target: 100 },
    { duration: '30s', target: 0 },
  ],
  // thresholds: {
  //   http_req_duration: ['p(95)<500'],
  //   ws_session_duration: ['avg>1s'],
  // },
};

const roomId = 11;

export default function () {
  const memberId = 1;
  const joinUrl = `http://localhost:8081/api/v1/chatroom/${roomId}?memberId=${memberId}`;
  const socketUrl = 'ws://localhost:8082/ws/chat';
  const subDestination = `/sub/chat/room/${roomId}`;

  // Step 1: 채팅방 입장 요청
  const res = http.post(joinUrl, null, {
    headers: {'Content-Type': 'application/json'},
  });

  check(res, {
    'entered successfully': (r) => r.status === 200,
  });

  // Step 2: WebSocket 연결 및 SUBSCRIBE (입장 이벤트 자동 발생)
  ws.connect(socketUrl, null, function (socket) {
    socket.on('open', () => console.log('WebSocket connection opened'));
    socket.on('close', () => console.log('WebSocket connection closed'));
    socket.on('error', (e) => console.error('WebSocket error:', e));

    socket.send(
        'CONNECT\r\naccept-version:1.2\r\nheart-beat:10000,10000\r\n\r\n\u0000');

    socket.send(
        `SUBSCRIBE\r\nid:sub-0\r\ndestination:${subDestination}\r\n\r\n\u0000`);

    // 연결 유지 시간 확보
    sleep(3);
    socket.close();
  });
}
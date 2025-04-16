import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '10s', target: 10 },
    { duration: '10s', target: 50 },
    { duration: '10s', target: 0 },
  ],
};

export default function () {
  const roomId = 10;
  const url = `http://localhost:8081/api/v1/chatroom/${roomId}`;

  const params = {
    headers: { 'Content-Type': 'application/json' },
    params: {
      memberId: 1,
      secretKey: null
    },
  };

  const res = http.post(url, null, params);

  check(res, {
    'entered successfully': (r) => r.status === 200,
  });
}
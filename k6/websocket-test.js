import ws from 'k6/ws';
import { check } from 'k6';

export const options = {
  stages: [
    { duration: '10s', target: 30 },
    { duration: '15s', target: 50 },
    { duration: '10s', target: 0 },
  ]
}

const roomId = 'roomId-1234';
const subDestination = `/sub/chat/room/${roomId}`;
const pubDestination = `/pub/chat/message`;

export default function () {
  const url = 'ws://localhost:8082/ws/chat'; // 엔드포인트에 맞게 수정

  const res = ws.connect(url, null, function (socket) {
    // 1. STOMP CONNECT 프레임 전송
    socket.send('CONNECT\naccept-version:1.2\nheart-beat:10000,10000\n\n\u0000');

    socket.on('message', function (message) {
      // 2. CONNECTED 응답 처리
      if (message.startsWith('CONNECTED')) {
        console.log('STOMP connected');

        // 3. SUBSCRIBE: 채팅방 구독
        socket.send(
            `SUBSCRIBE\nid:sub-0\ndestination:${subDestination}\n\n\u0000`
        );

        // 4. SEND: 채팅 메시지 발행
        const payload = JSON.stringify({
          roomId: roomId,
          sender: 'k6-bot',
          message: 'Hello from k6!',
        });

        socket.send(
            `SEND\ndestination:${pubDestination}\ncontent-type:application/json\n\n${payload}\u0000`
        );
      }

      if (message.startsWith('MESSAGE')) {
        check(message, {
          'received MESSAGE frame': (msg) => msg.includes('Hello from k6!'),
        });
        socket.close();
      }
    });

    socket.on('open', () => console.log('WebSocket connection opened'));
    socket.on('close', () => console.log('WebSocket connection closed'));
    socket.on('error', (e) => console.error('WebSocket error:', e));

    socket.close();
    return true; // 연결 종료 후 iteration 마무리
  });

  check(res, { 'WebSocket handshake successful': (r) => r && r.status === 101 });
}

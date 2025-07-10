## Potatalk는 실시간 채팅 서비스 입니다.

## Architecture

![image](https://github.com/user-attachments/assets/ce42bc96-d5c6-43ff-aae0-b91c9ce84470)

## User Flow SnapShot

![image](https://github.com/user-attachments/assets/9412b2ae-b3d4-48dd-bad0-aa072f0f620f)


## 주요 기술

- 언어 : Java 17
- 프레임워크 : Spring Boot, Spring WebFlux, Spring WebSocket
- 빌드 도구 : Gradle
- 배포 및 운영 :
    - 서버 : AWS EC2, Docker
    - CI/CD : GitHub Actions
    - 데이터베이스 : MySQL, MongoDB
    - 캐시 : Redis (AWS Elastic Cache)
    - 메시징 시스템 : Kafka, Redis (Pub/Sub)
- 모니터링 : Prometheus + Grafana, CloudWatch, Brave, Zipkin
- 테스트 및 품질 : JaCoCo

## ERD

![Pasted image 20240827154537](https://github.com/user-attachments/assets/03d89f6a-7e47-4dc8-b586-354ba581fd46)


---

## 프로젝트 관련 업로드

[TimeLine](https://tangpoo.tistory.com/195)

[주제 선정](https://tangpoo.tistory.com/193)

[설계 과정](https://tangpoo.tistory.com/194)

[Spring WebSocket 실시간 채팅 구현](https://tangpoo.tistory.com/196)

[API-Gateway 인증/인가 적용](https://tangpoo.tistory.com/197)

[Kafka vs RabbitMQ 성능 비교](https://tangpoo.tistory.com/201)

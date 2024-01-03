## 💰계획적인 소비로 내 지갑을 지켜주는 Safe Wallet 서비스

> 사용자들이 개인 재무를 관리하고 지출을 추적하는 데 도움을 주는 애플리케이션입니다.
> <br/>이 앱은 사용자들이 예산을 설정하고 지출을 모니터링하며 재무 목표를 달성하는 데 도움이 됩니다.

[요구사항 문서](https://bow-hair-db3.notion.site/90cba97a58a843e4a2563a226db3d5b5#ae43733f8f8e4862b417d746623dce89)

<br/>

### 사용 기술 

<img src="https://github.com/jkde7721/safe-wallet-service/assets/65665065/f7c72d69-f8bc-4fb7-b5e7-31232d5a67e0" width="90%"/>

<br/>

### DB 설계

<img src="https://github.com/jkde7721/safe-wallet-service/assets/65665065/262d36be-1466-425e-a7ee-63248fd787f5" width="90%"/>

### CI/CD 파이프라인

<img src="https://github.com/jkde7721/safe-wallet-service/assets/65665065/e5d9eb9d-ea2d-4403-8fe6-e8d2483df039" width="90%"/>

<br/>

### [API 명세](http://ec2-3-35-128-28.ap-northeast-2.compute.amazonaws.com:8080/docs/index.html)

<br/>

### 구현 시 고려사항 

| 고려사항                         | 정리 블로그                                                                          |
|------------------------------|---------------------------------------------------------------------------------|
| 날짜 데이터 요청 및 응답에 대한 역직렬화, 직렬화 | [날짜 데이터를 요청 및 응답으로 주고 받는 여러 가지 방법](https://daeun21dev.tistory.com/38)           |
| Facade 디자인 패턴 적용             | [Controller - Service 계층 리팩토링 with Facade 패턴](https://daeun21dev.tistory.com/40) |
 | DB 연관관계가 있는 데이터 삭제 방법        | 작성 예정                                                                           | 
| CI/CD 파이프라인 구축               | 작성 예정                                                                           |
| Passay 비밀번호 검증기 | 작성 예정                                                                           |

<br/>

### 프로젝트 관리 Jira 칸반 보드

<img src="https://github.com/jkde7721/safe-wallet-service/assets/65665065/140741b6-2a11-4f5f-8d1d-ba3a7ee6bb66" width="90%"/>

<br/>

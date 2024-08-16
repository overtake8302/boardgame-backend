<!-- <div align="center"> -->

# 세네트 - 보드게임 커뮤니티
## 프로젝트 주제
+ 다양한 보드게임을 즐기고, 관련 정보를 공유할 수 있는 온라인 보드게임 커뮤니티를 구축하여 사용자들이 다양한 보드게임을 즐길 수 있는 플랫폼을 제공하는 것.  

---

## 팀 소개
- 엘리스 Cloud 트랙 3기 3차 프로젝트 10팀  
- 개발기간 : 2024.07.22 ~ 2024.18.16
- 노션 팀페이지 : https://www.notion.so/elice-track/10-cab424f1cfbb4e1b9393d64640f2c286?pvs=4
- 서버 도메인 : http://kdt-cloud-3-team10-final.elicecoding.com/
---

# 프로젝트 소개
## 목적
+ 사용자 간의 소통과 교류를 촉진하는 커뮤니티 형성
+ 보드게임의 규칙과 전략을 배우고 공유할 수 있는 공간 제공

## 목표
+ 보드게임 관련 정보를 제공하여 커뮤니티 활성화  
+ 사용자 경험을 향상시키기 위한 기능 개발  

---

# Tech Stack
## Communications
[![Communications](https://skillicons.dev/icons?i=gitlab,discord,notion)](https://skillicons.dev)  
## Server
[![Server](https://skillicons.dev/icons?i=java,spring)](https://skillicons.dev)   
## Database
[![Database](https://skillicons.dev/icons?i=mysql)](https://skillicons.dev)  
## Infrastructure
[![Infrastructure](https://skillicons.dev/icons?i=gcp,aws)](https://skillicons.dev)  

---

# Entity Relationship Diagram

### 보드게임 관련 테이블
<img src="/uploads/f1729b65450a9a1a9928967b951d59c1/ERD-boardgame.png" width="50%">

### 게시판 관련 테이블
<img src="/uploads/dfb58707880f30188d4da7725bd26c66/ERD-post.png" width="50%">

### 유저 관련 테이블
<img src="/uploads/08b4def289b1d5edc5860dbb7d70e455/ERD-user.png" width="50%">

---

# InfraStructure
+ 서버 배포: Google Cloud Platform
+ 이미지 저장 : AWS S3

# 패키지 구조
```
├─auth
│  ├─controller
│  ├─dto
│  ├─entity
│  ├─jwt
│  ├─repository
│  └─service
├─category
│  ├─controller
│  ├─dto
│  ├─entity
│  ├─mapper
│  ├─repository
│  └─service
├─common
│  ├─annotation
│  ├─config
│  ├─dto
│  ├─entity
│  ├─enums
│  └─exceptions
├─game
│  ├─annotation
│  ├─controller
│  ├─dto
│  ├─entity
│  ├─mapper
│  ├─repository
│  └─service
├─post
│  ├─controller
│  ├─dto
│  ├─entity
│  ├─repository
│  └─service
├─report
│  ├─controller
│  ├─dto
│  ├─entity
│  ├─repository
│  └─service
└─social
    ├─controller
    ├─dto
    ├─entity
    ├─exception
    ├─repository
    └─service
```


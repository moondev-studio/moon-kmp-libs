# [moon-kmp-libs 프로젝트 전용]

## 프로젝트 개요
MoonDeveloper OSS KMP 라이브러리 모노레포.
Splitly, BetOnMe 등 MoonDeveloper 앱에서 공통으로 사용하는 인터페이스와 유틸리티.

## 모듈 목록
- build-logic/convention: Convention Plugins (moon.kmp.library, moon.compose.library)
- moon-analytics-kmp: Analytics/Crash 인터페이스 (Phase A)

## 빌드 규칙
- desktopTest만 사용 (allTests 금지)
- 컴파일 5분 + 실행 5분 타임아웃
- gradle --stop 선행

## 버전 정책
- splitly와 Kotlin/Compose 버전 동기화
- libs.versions.toml로 중앙 관리

## 의존성 원칙 (절대 규칙)
- 이 레포의 모든 모듈은 Firebase, Google Play, Apple SDK에 의존하면 안 됨
- 허용: kotlinx-coroutines, kotlinx-datetime, compose-multiplatform
- OSS 모듈 간 의존은 허용 (예: moon-ui-kmp -> moon-sync-kmp)

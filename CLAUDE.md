# MoonDeveloper — 공통 지침

> CMP 기반 1인 글로벌 앱 스튜디오. 이 레포는 모든 앱 프로젝트가 상속하는 공통 규칙을 관리한다.

## 앱 포트폴리오
| 앱 | 상태 | 비고 |
|-----|------|------|
| Splitly | Phase 12 (사업자등록 대기) | 정산+가계부, 1094 테스트 |
| BetOnMe | 기획 전 | 습관 내기앱, Splitly 모듈 65% 재사용 |
| ureen | 홀딩 | 로또 추천앱, v1.0.0 출시 완료 |

## 공통 기술 스택
KMP (Kotlin 2.3.0) + Compose Multiplatform + Material 3 + Koin 4.1.1 + Room KMP / Firebase (GitLive) + GitHub Actions

## .claude/ 구조
| 디렉토리 | 용도 | 로드 방식 |
|----------|------|----------|
| rules/ | Git, 테스트, 코드스타일, 성능 규칙 | 모든 세션 자동 로드 |
| agents/ | 에이전트 정의 + 팀 프로토콜 | 팀 작업 시 로드 |
| infra/ | 하드웨어, 시크릿, CI/CD 요약 | 필요 시 참조 |
| strategy/ | 비전, 포트폴리오 요약 | 필요 시 참조 |

> rules/는 모든 앱 프로젝트에서 자동 상속된다. 앱별 CLAUDE.md에서 "공통 규칙은 master-claude-code-configs 참조"로 연결.

## 원본 문서
상세 전략은 `docs/` 참조 (8개 문서). `.claude/`는 세션용 요약본이며, `docs/`가 source of truth.

## Kotlin/KMP 공용 규칙
`kotlin/CLAUDE_COMMON.md` — 모든 KMP 프로젝트의 마스터 규칙
`kotlin/CONVENTIONS.md` — 코딩 컨벤션
`kotlin/GLOBAL_SKILLS.md` — 12개 공용 스킬

## 모듈화 규칙
- 3-Tier: OSS 라이브러리(moon-*) → 앱 라이브러리({app}-*) → 앱
- OSS 모듈은 앱 의존성 제로 (Firebase, Play 등 금지)
- Convention Plugin으로 빌드 설정 통일
- 상세: .claude/agents/modularization-agent.md 참조

---

# [moon-kmp-libs 프로젝트 전용]

## 현재 상태
- 7개 OSS 모듈 (analytics, auth, billing, sync, ui, i18n, ocr)
- Hardening Phase 완료 (Step 0~9)
- 테스트: 130 @Test (desktopTest 기준)
- Maven Central: publishToMavenLocal 검증 완료
- Dokka API 문서 생성 설정 완료

## 모듈 목록
| 모듈 | 설명 | 테스트 | 의존성 |
|------|------|--------|--------|
| moon-analytics-kmp | Analytics/Crash/Screen/Action/Perf/Conversion tracking | 24 | 없음 (순수 인터페이스) |
| moon-auth-kmp | Auth abstraction (Google/Apple/Email) | 39 | kotlinx-coroutines, kotlinx-datetime |
| moon-billing-kmp | IAP abstraction (Play/StoreKit) | 32 | kotlinx-coroutines, kotlinx-datetime |
| moon-sync-kmp | Offline-first sync engine | 11 | kotlinx-coroutines, kotlinx-datetime |
| moon-ui-kmp | Adaptive Compose components | 9 | compose-multiplatform |
| moon-i18n-kmp | Hybrid i18n (bundle + on-demand) | 7 | kotlinx-coroutines |
| moon-ocr-kmp | OCR & receipt parsing interfaces | 8 | 없음 (순수 인터페이스) |

## 기술 스택
- Kotlin 2.3.0, CMP 1.10.0, AGP 8.11.2, Gradle 8.14.4

## Convention Plugins (build-logic/)
- `moon.kmp.library`: KMP 공통 설정 (android+ios+desktop, JVM17)
- `moon.compose.library`: Compose Multiplatform 추가 설정
- `MoonPublishPlugin`: Maven Central 배포

## 빌드 규칙
- desktopTest만 사용 (allTests 금지)
- 컴파일 5분, 실행 5분 타임아웃
- gradle --stop 선행 필수

## 버전 정책
- splitly와 Kotlin/Compose 버전 동기화
- libs.versions.toml로 중앙 관리

## 참조 프로젝트
- splitly (includeBuild + dependencySubstitution)

## 의존성 원칙 (절대 규칙)
- 이 레포의 모든 모듈은 Firebase, Google Play, Apple SDK에 의존하면 안 됨
- 허용: kotlinx-coroutines, kotlinx-datetime, compose-multiplatform
- OSS 모듈 간 의존은 허용 (예: moon-ui-kmp -> moon-sync-kmp)

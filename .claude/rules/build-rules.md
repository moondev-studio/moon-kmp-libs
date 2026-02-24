# 빌드 규칙

## 테스트
- desktopTest만 실행 (allTests 금지)
- 컴파일 5분 + 실행 5분 타임아웃
- 작업 전 gradle --stop 필수

## 모듈 추가
- Convention Plugin 사용 필수 (moon.kmp.library 또는 moon.compose.library)
- settings.gradle.kts에 include 추가
- 패키지: com.moondeveloper.{모듈명에서 moon-과 -kmp 제거}
  예: moon-analytics-kmp -> com.moondeveloper.analytics

## 커밋
- 모듈 단위 커밋
- 커밋 메시지: feat/fix/docs: 설명

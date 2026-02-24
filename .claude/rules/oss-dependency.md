# OSS 의존성 규칙

## 절대 금지 의존성
이 레포의 어떤 모듈에서도 아래 라이브러리를 import하면 안 됩니다:
- dev.gitlive.firebase.*
- com.google.firebase.*
- com.android.billingclient.*
- com.google.android.gms.*
- StoreKit, GameKit 등 Apple 전용 프레임워크

## 허용 의존성
- org.jetbrains.kotlinx:kotlinx-coroutines-*
- org.jetbrains.kotlinx:kotlinx-datetime
- org.jetbrains.compose (Compose Multiplatform)
- kotlin-test, kotlinx-coroutines-test (테스트)
- 이 레포 내 다른 moon-* 모듈

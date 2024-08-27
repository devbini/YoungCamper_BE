# 🙏🏻 Young-Camper 백엔드 README
- **Deployment URL** : http://youngcamp.co.kr/
- **Language** : Java 17
- **Library & Framework** : Spring Boot 3.3.2, Gradle
- **Database** : PostgreSQL
- **Deploy**: AWS (EC2, S3, RDS), Nginx
- **CI/CD** : Jenkins, Docker

## 📌 Commit Convention (커밋 메시지 규칙)
> 📌 **한 커밋에는 한가지 이슈만** 담을 것  
> 📌 가능한 **본문**을 포함할 것.

|TYPE|Subject|Des.|
|:-----:|:---|:---|
|feat|새로운 기능을 추가하는 경우|브랜치 첫 커밋이나 기능 추가시|
|fix|코드를 수정하는 경우|버그 및 코드 수정의 모든 과정|
|docs|문서만을 수정하는 경우||
|refactor|버그 수정 or 기능 추가가 없는 코드 변경|리팩토링하는 과정|
|test|테스트 추가 과정|누락된 테스트 추가 또는 기존 테스트 수정|

### CLI에서 커밋 메시지 여러 줄로 작성하는 방법 (2번째 줄 부터 본문)
✅ 쌍따옴표(")를 닫지 말고 개행(엔터)하며 작성하면 됩니다.

<br/>

## 👤 팀원 구성
| **김찬빈** | **김세은** | **임주혁** |
| :------: |  :------: | :------: |
| [<img src="https://avatars.githubusercontent.com/devbini" height=150 width=150> <br/> @devbini](https://github.com/devbini) | [<img src="https://avatars.githubusercontent.com/seeun0210" height=150 width=150> <br/> @seeun0210](https://github.com/seeun0210) | [<img src="https://avatars.githubusercontent.com/Juhyeok0202" height=150 width=150> <br/> @Juhyeok0202](https://github.com/Juhyeok0202) |

## 🚩 브랜치 전략
- Git-Flow 전략을 기반으로 합니다.
- main, release, develop 브랜치를 메인으로 가져갑니다.
- feature 브랜치를 보조 브랜치로 가져갑니다.
    - **main** 브랜치는 배포 단계에서 사용됩니다.
    - **release** 브랜치는 배포 직전 QA 단계에서 사용됩니다.
    - **develop** 브랜치는 개발 단계의 Git-flow에서 main 역할을 수행합니다.
    - **feature** 브랜치는 기능 단위로 사용하고 merge 후 각 브랜치를 삭제합니다.
- PR 종료는 최소 1명 이상의 리뷰어가 승인 한 경우에만 가능하도록 합니다.
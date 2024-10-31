## Spring cloud config

Spring Cloud Config는 분산 시스템에서 외부화된 구성에 대한 서버 측 및 클라이언트 측 지원을 제공한다.
Config Server를 사용하면 개발에서 프로덕션까지 환경 구성을 외부에서 관리한다.
덕분에 애플리케이션이 마이그레이션마다 빠르게 필요한 모든 환경 변수 파악이 가능하다.

> 스토리지로 보통 Git을 사용하는데 환경에 따라 [S3](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/#spring-cloud-config-serving-plain-text-aws-s3) 등의 다른 스토리지를 사용할 수 있다.
> (문서에서는 동작 방식이 다르다고 한다.)

### Spring cloud server

#### 특징 요약

- search-paths 에 포함되는 파일을 전부 읽는다.
- git 설정한 경우 git update 마다 정보를 갱신한다.
- `application name`과 `profile` 기준으로 파일을 조회한다.
- `profile`이 없는 파일은 전체 프로파일에 적용된다.

조회 방식은 아래가 전부다.

```http request
### REST API
GET /{application}/{profile}[/{label}]

### 기본 branch(label)에서 직접 yml 조회
GET /{application}-{profile}.yml

### 기본 branch(label)에서 직접 properties 조회
GET /{application}-{profile}.properties

### branch(label) 지정해서 yml 조회
GET /{label}/{application}-{profile}.yml

### branch(label) 지정해서 properties 조회
GET /{label}/{application}-{profile}.properties
```

응답 데이터는 다음처럼 보함된다.

```json
{
  "name": "hcc",
  "profiles": [
    "prod"
  ],
  "label": null,
  "version": "e11772c1439a6e8183ece24c2e18e49a9b8275f9",
  "state": null,
  "propertySources": [
    {
      "name": "https://github.com/this-is-spear/hello-spring-config/hello-config-file/hcc/prod/hcc-prod.yml",
      "source": {
        "application.name": "hello-config-client",
        "application.message": "Hello, Prod Config Client!"
      }
    }
  ]
}
```

변경되면 다음 로그가 출력된다.
간단하게 바라보는 저장소 파일을 읽어 파일을 관리하고 있다.

```log
MultipleJGitEnvironmentRepository : Fetched for remote main and found 1 updates
NativeEnvironmentRepository  : Adding property source: Config resource 'file [/var/folders/rt/54tz9m_x72781wptbr0nhfc80000gn/T/config-repo-10948513823158205996/hello-config-file/hcc/prod/hcc-prod.yml]' via location 'file:/var/folders/rt/54tz9m_x72781wptbr0nhfc80000gn/T/config-repo-10948513823158205996/hello-config-file/hcc/prod/'
NativeEnvironmentRepository  : Adding property source: Config resource 'file [/var/folders/rt/54tz9m_x72781wptbr0nhfc80000gn/T/config-repo-10948513823158205996/hello-config-file/hcc/prod/hcc-prod.yml]' via location 'file:/var/folders/rt/54tz9m_x72781wptbr0nhfc80000gn/T/config-repo-10948513823158205996/hello-config-file/hcc/prod/'
```

### Spring cloud config

#### 특징 요약

- spring.config.import 를 사용해서 외부 설정을 가져올 수 있다.
- spring actuator 기능인 refresh 로 컨텍스트를 다시 로드할 수 있다. 
- `@ConfigurationProperties`또는 `@Value` 설정된 인스턴스는 refresh 하려면 빈으로 등록해야 하니 주의한다.

```log
ConfigServerConfigDataLoader   : Located environment: name=hcc, profiles=[test], label=null, version=05e04901ec3ba7c25f6bf810742a5568bcfcd1e8, state=null
RefreshEndpoint       : Refreshed keys : [config.client.version, refresh-client.message]
```

#### 갱신과 관련해서

`@ConfigurationProperties`또는 `@Value`로 지정된 인스턴스를 갱신하려면 `@RefreshScope` 를 사용해야 한다.
그러나 인스턴스를 삭제하고 다시 생성해야 하므로 정상적으로 동작하지 않는다.
그래서 `@ConfigurationProperties`로 지정된 인스턴스를 빈으로 등록하고 교체해야 한다.

- https://gist.github.com/dsyer/a43fe5f74427b371519af68c5c4904c7
- https://www.baeldung.com/spring-reloading-properties

> Spring Actuator로 refresh 사용하려면 spring cloud common 라이브러라기 꼭 필요하니 spring cloud start 도 함께 받자.

## Spring cloud aws - Secrets Manager and Parameter Store Integration

`Secrets Manager`와 `Parameter Store`는 AWS에서 제공하는 서비스로 보안 정보를 저장하고 관리할 수 있다.
`Secrets Manager`와 `Parameter Store` 는 정보를 암호화해 관리한다.
그러나 `Secrets Manager`는 키를 로테이션해 보안에 더 유리하다.

물론 비용차이도 존재한다. `Secrets Manager`는 조회마다 비용이 추가된다.

`Parameter Store` 동작먼저 살펴보겠다.

- refresh strategy 정책으로 파라미터를 변경할 수 있다.
  - spring cloud starter, spring actuator 라이브러라기 필요하다.
  - refresh, restart_context 가 존재한다.
  - 프로퍼티 수정 감지가 생각보다 빠르지 않다. 찾아보니 aws.parameterstore.reload.period 설정하면 되더라.
- refresh
  - refresh 는 @RefreshScope 사용한 필드만 적용된다. 앞서 이야기 했듯이 @RefreshScope 를 적용하려면 빈으로 등록되어야 한다.
- restart_context
  - restart_context 는 컨텍스트 자체를 로드해서 빈 없이 프로퍼티 주입을 수정한다. 컨텍스트 로드되는 도중 요청이 어떻게 될지는 모르겠다.

`Parameter Store` 설정 방법은 간단하다. 

spring.config.import 를 사용해서 `aws-parameterstore:/spring/config/` 값을 주입하면 `/spring/config/` 내에 있는 환경 변수를 모두 찾아온다.

<img width="1787" alt="스크린샷 2024-10-31 오전 9 04 41" src="https://github.com/user-attachments/assets/1dc5b39c-3a33-4804-996b-0cfcb0d27553">

yml 파일처럼 관리하지 못하는게 단점처럼 보인다.
환경 변수마다 파편화가 안되게 설정하려면 어떤 방식이 좋을까?



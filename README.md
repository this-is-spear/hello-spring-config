## Spring cloud server

### 특징 요약

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

## Spring cloud config

### 특징 요약

- spring.config.import 를 사용해서 외부 설정을 가져올 수 있다.
- spring actuator 기능인 refresh 로 컨텍스트를 다시 로드할 수 있다. 
- `@ConfigurationProperties`또는 `@Value` 설정된 인스턴스는 refresh 하려면 빈으로 등록해야 하니 주의한다.

```log
ConfigServerConfigDataLoader   : Located environment: name=hcc, profiles=[test], label=null, version=05e04901ec3ba7c25f6bf810742a5568bcfcd1e8, state=null
RefreshEndpoint       : Refreshed keys : [config.client.version, refresh-client.message]
```

### 갱신과 관련해서

`@ConfigurationProperties`또는 `@Value`로 지정된 인스턴스를 갱신하려면 `@RefreshScope` 를 사용해야 한다.
그러나 인스턴스를 삭제하고 다시 생성해야 하므로 정상적으로 동작하지 않는다.
그래서 `@ConfigurationProperties`로 지정된 인스턴스를 빈으로 등록하고 교체해야 한다.

- https://gist.github.com/dsyer/a43fe5f74427b371519af68c5c4904c7
- https://www.baeldung.com/spring-reloading-properties



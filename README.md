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

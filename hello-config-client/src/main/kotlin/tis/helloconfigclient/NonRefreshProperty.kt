package tis.helloconfigclient

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("client")
class NonRefreshProperty(
    val message: String,
)
package tis.helloawsconfigclient

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.stereotype.Component

@Component
@RefreshScope
@ConfigurationProperties("refresh-client")
class RefreshProperty {
    lateinit var message: String
}
package tis.helloconfigclient

import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController(
    private val refreshProperty: RefreshProperty,
    private val nonRefreshProperty: NonRefreshProperty,
    private val environment: Environment,
) {
    @GetMapping("/hello")
    fun hello(): String {
        environment.getProperty("client.message")?.let {
            println("client.message: $it")
        }
        return nonRefreshProperty.message
    }

    @GetMapping("/refresh-hello")
    fun refreshHello(): String {
        environment.getProperty("refresh-client.message")?.let {
            println("client.message: $it")
        }
        return refreshProperty.message
    }
}

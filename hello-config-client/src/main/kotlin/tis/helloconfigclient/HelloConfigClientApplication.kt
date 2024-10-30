package tis.helloconfigclient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HelloConfigClientApplication

fun main(args: Array<String>) {
    runApplication<HelloConfigClientApplication>(*args)
}

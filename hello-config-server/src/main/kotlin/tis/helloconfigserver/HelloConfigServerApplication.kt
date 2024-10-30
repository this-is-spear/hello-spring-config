package tis.helloconfigserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HelloConfigServerApplication

fun main(args: Array<String>) {
    runApplication<HelloConfigServerApplication>(*args)
}

package tis.helloawssecretmanagerconfig

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HelloAwsSecretManagerConfigApplication

fun main(args: Array<String>) {
    runApplication<HelloAwsSecretManagerConfigApplication>(*args)
}

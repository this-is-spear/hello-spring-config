package tis.helloconfigclient

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<HelloConfigClientApplication>().with(TestcontainersConfiguration::class).run(*args)
}

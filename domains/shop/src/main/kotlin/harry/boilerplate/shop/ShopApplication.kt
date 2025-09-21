package harry.boilerplate.shop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("harry.boilerplate.shop", "harry.boilerplate.common")
@EntityScan("harry.boilerplate.shop", "harry.boilerplate.common")
class ShopApplication

fun main(args: Array<String>) {
    System.setProperty("spring.profiles.active", "shop")
    runApplication<ShopApplication>(*args)
}

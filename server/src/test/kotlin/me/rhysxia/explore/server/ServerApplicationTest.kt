package me.rhysxia.explore.server

import kotlinx.coroutines.runBlocking
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.po.UserStatus
import me.rhysxia.explore.server.repository.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Instant

@SpringBootTest
@ActiveProfiles("test")
class ServerApplicationTest {

  @Autowired
  private lateinit var userRepository: UserRepository

  @Test
  fun contextLoads() {
    runBlocking {
      userRepository.save(
        UserPo(
          null, "aa", "aa", "aa", "aa", "aa", UserStatus.ACTIVATED, "aa", Instant.now(), Instant.now(),
          Instant.now()
        )
      )
      val user = userRepository.findByUsername("aa")
      println(user)
    }
  }
}
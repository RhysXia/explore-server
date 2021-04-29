package me.rhysxia.blog.server.controller;

import me.rhysxia.blog.server.po.UserPo;
import me.rhysxia.blog.server.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController("/")
public class IndexController {

    private TestService testService;

    public IndexController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/")
    public Flux<UserPo> index() {
        return testService.findAll();
    }
}

package me.rhysxia.blog.server.service;

import me.rhysxia.blog.server.po.UserPo;
import reactor.core.publisher.Flux;

public interface TestService {

    Flux<UserPo> findAll();
}

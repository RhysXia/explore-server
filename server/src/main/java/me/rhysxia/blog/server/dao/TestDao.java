package me.rhysxia.blog.server.dao;

import me.rhysxia.blog.server.po.UserPo;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TestDao extends R2dbcRepository<UserPo, Long> {
    Flux<UserPo> findAll();
}

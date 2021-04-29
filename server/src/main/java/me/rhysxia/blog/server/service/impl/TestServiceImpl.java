package me.rhysxia.blog.server.service.impl;

import me.rhysxia.blog.server.dao.TestDao;
import me.rhysxia.blog.server.po.UserPo;
import me.rhysxia.blog.server.service.TestService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class TestServiceImpl implements TestService {

    private final TestDao testDao;

    public TestServiceImpl(TestDao testDao) {
        this.testDao = testDao;
    }

    @Override
    public Flux<UserPo> findAll() {
        return testDao.findAll();
    }
}

package com.team8504.csgo2web.dao.impl;

import com.team8504.csgo2web.dao.CommentDao;
import com.team8504.csgo2web.entity.Background;
import com.team8504.csgo2web.entity.Comment;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CommentDaoImplTest {
    @Resource
    CommentDao commentDao;


    @Test
    void getCommentList() {
        List<Comment> list = commentDao.getCommentList();
        for (Comment comment: list) {
            System.out.println(comment);
        }
    }
}
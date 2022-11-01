package com.yejin.exam.wbook.domain.mybook.repository;

import com.yejin.exam.wbook.domain.mybook.entity.MyBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyBookRepository extends JpaRepository<MyBook, Long> {
    void deleteByProductIdAndOwnerId(long productId, long ownerId);
}
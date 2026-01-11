package com.zarvekule.blog.repository;

import com.zarvekule.blog.entity.BlogCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogCollectionRepository extends JpaRepository<BlogCollection, Long> {
    List<BlogCollection> findAllByOwner_Id(Long userId);

    List<BlogCollection> findAllByOwner_IdAndIsPublicTrue(Long userId);
}
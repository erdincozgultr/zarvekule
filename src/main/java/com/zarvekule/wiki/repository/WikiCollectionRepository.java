package com.zarvekule.wiki.repository;

import com.zarvekule.wiki.entity.WikiCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WikiCollectionRepository extends JpaRepository<WikiCollection, Long> {
    List<WikiCollection> findAllByOwner_Id(Long userId);

    List<WikiCollection> findAllByOwner_IdAndIsPublicTrue(Long userId);
}
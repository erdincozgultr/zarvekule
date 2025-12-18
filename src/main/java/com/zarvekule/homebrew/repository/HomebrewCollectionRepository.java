package com.zarvekule.homebrew.repository;

import com.zarvekule.homebrew.entity.HomebrewCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomebrewCollectionRepository extends JpaRepository<HomebrewCollection, Long> {
    List<HomebrewCollection> findAllByOwner_Id(Long userId);

    List<HomebrewCollection> findAllByOwner_IdAndIsPublicTrue(Long userId);
}
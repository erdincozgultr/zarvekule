package com.zarvekule.blog.repository;

import com.zarvekule.blog.entity.BlogComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogCommentRepository extends JpaRepository<BlogComment, Long> {

    // Public görüntüleme için sadece onaylılar
    Page<BlogComment> findByBlog_IdAndIsApprovedTrue(Long blogId, Pageable pageable);

    // Admin paneli için onaysızları getirme (Moderasyon kuyruğu)
    Page<BlogComment> findByIsApprovedFalse(Pageable pageable);
}
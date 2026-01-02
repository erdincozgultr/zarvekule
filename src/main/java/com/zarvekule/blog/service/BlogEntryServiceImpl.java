package com.zarvekule.blog.service;

import com.zarvekule.exceptions.ApiException;
import com.zarvekule.blog.dto.BlogEntryRequest;
import com.zarvekule.blog.dto.BlogEntryResponse;
import com.zarvekule.blog.dto.BlogEntrySummary;
import com.zarvekule.blog.entity.BlogEntry;
import com.zarvekule.blog.enums.BlogCategory; // Import eklemeyi unutma
import com.zarvekule.blog.enums.BlogStatus;
import com.zarvekule.blog.mapper.BlogEntryMapper;
import com.zarvekule.blog.repository.BlogEntryRepository;
import com.zarvekule.gamification.enums.ActionType;
import com.zarvekule.gamification.service.GamificationService;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.UserRepository;
import com.zarvekule.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogEntryServiceImpl implements BlogEntryService {

    private final BlogEntryRepository blogRepository;
    private final UserRepository userRepository;
    private final BlogEntryMapper blogMapper;
    private final GamificationService gamificationService;

    // Ortalama okuma hızı (kelime/dakika)
    private static final int WORDS_PER_MINUTE = 200;

    @Override
    @Transactional
    public BlogEntryResponse create(String authenticatedUsername, BlogEntryRequest request) {
        User author = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new ApiException("Yazar bulunamadı.", HttpStatus.NOT_FOUND));

        String baseTitle = request.getTitle();
        String slug = SlugUtils.createSlug(request.getCustomSlug(), baseTitle);
        slug = this.ensureUniqueSlug(slug);

        BlogEntry blogEntry = new BlogEntry();
        blogEntry.setTitle(baseTitle);
        blogEntry.setContent(request.getContent());
        blogEntry.setThumbnailUrl(request.getThumbnailUrl());
        blogEntry.setAuthor(author);
        blogEntry.setTags(request.getTags());
        blogEntry.setSlug(slug);

        // --- YENİ ALANLAR ---
        blogEntry.setCategory(request.getCategory() != null ? request.getCategory() : BlogCategory.OTHER); // Varsayılan kategori
        blogEntry.setReadingTime(calculateReadingTime(request.getContent()));
        blogEntry.setSeoTitle(request.getSeoTitle());
        blogEntry.setSeoDescription(request.getSeoDescription());
        // --------------------

        BlogStatus status = request.getStatus() != null ?
                request.getStatus() : BlogStatus.DRAFT;
        blogEntry.setStatus(status);
        blogEntry.setCreatedAt(LocalDateTime.now());

        // Eğer direkt PUBLISHED ise publishedAt set et
        if (status == BlogStatus.PUBLISHED) {
            blogEntry.setPublishedAt(LocalDateTime.now());
        }

        BlogEntry saved = blogRepository.save(blogEntry);

        // ✅ YENİ: Eğer PUBLISHED ise XP ver
        if (status == BlogStatus.PUBLISHED) {
            gamificationService.processAction(author, ActionType.CREATE_BLOG);
        }

        return blogMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public BlogEntryResponse update(String authenticatedUsername, Long id, BlogEntryRequest request) {
        BlogEntry blogEntry = blogRepository.findById(id)
                .orElseThrow(() -> new ApiException("Blog yazısı bulunamadı. ID: " + id, HttpStatus.NOT_FOUND));

        if (!Objects.equals(blogEntry.getAuthor().getUsername(), authenticatedUsername)) {
            throw new ApiException("Bu blog yazısını güncelleme yetkiniz yok.", HttpStatus.FORBIDDEN);
        }

        // Eski status'u sakla
        BlogStatus oldStatus = blogEntry.getStatus();

        blogEntry.setTitle(request.getTitle());
        blogEntry.setContent(request.getContent());
        blogEntry.setThumbnailUrl(request.getThumbnailUrl());
        blogEntry.setTags(request.getTags());

        if (request.getCategory() != null) {
            blogEntry.setCategory(request.getCategory());
        }
        blogEntry.setReadingTime(calculateReadingTime(request.getContent()));
        blogEntry.setSeoTitle(request.getSeoTitle());
        blogEntry.setSeoDescription(request.getSeoDescription());
        blogEntry.setUpdatedAt(LocalDateTime.now());

        BlogStatus newStatus = request.getStatus();
        if (newStatus != null) {
            blogEntry.setStatus(newStatus);

            // ✅ YENİ: İlk kez PUBLISHED oluyorsa XP ver
            if (newStatus == BlogStatus.PUBLISHED && oldStatus != BlogStatus.PUBLISHED) {
                blogEntry.setPublishedAt(LocalDateTime.now());
                gamificationService.processAction(blogEntry.getAuthor(), ActionType.CREATE_BLOG);
            }
        }

        if (request.getCustomSlug() != null || !Objects.equals(blogEntry.getTitle(), request.getTitle())) {
            String newSlug = SlugUtils.createSlug(request.getCustomSlug(), request.getTitle());
            if (!Objects.equals(newSlug, blogEntry.getSlug())) {
                blogEntry.setSlug(this.ensureUniqueSlug(newSlug));
            }
        }

        BlogEntry saved = blogRepository.save(blogEntry);
        return blogMapper.toResponseDto(saved);
    }


    // Diğer metodlar aynı kalabilir...

    // --- Yardımcı Metod: Okuma Süresi Hesaplama ---
    private int calculateReadingTime(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }
        // HTML etiketlerini temizle (basitçe) ve kelimeleri say
        String cleanContent = content.replaceAll("\\<.*?\\>", "");
        String[] words = cleanContent.trim().split("\\s+");
        int wordCount = words.length;

        int readingTime = wordCount / WORDS_PER_MINUTE;
        return readingTime < 1 ? 1 : readingTime; // En az 1 dakika dön
    }

    // ... searchBlogs, delete, getBySlug, vb. buranın altına aynen gelecek.
    // (Önceki adımda eklediğimiz searchBlogs'u silmemeye dikkat et)

    @Override
    @Transactional
    public void delete(String authenticatedUsername, Long id) {
        // (Önceki kodun aynısı)
        BlogEntry blogEntry = blogRepository.findById(id)
                .orElseThrow(() -> new ApiException("Blog yazısı bulunamadı. ID: " + id, HttpStatus.NOT_FOUND));
        User currentUser = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new ApiException("Geçerli kullanıcı bulunamadı.", HttpStatus.UNAUTHORIZED));
        boolean isAuthor = Objects.equals(blogEntry.getAuthor().getUsername(), authenticatedUsername);
        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
        if (!isAuthor && !isAdmin) throw new ApiException("Bu blog yazısını silme yetkiniz yok.", HttpStatus.FORBIDDEN);
        blogRepository.delete(blogEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogEntrySummary> getPublishedBlogs(Pageable pageable) {
        return blogRepository.findByStatusOrderByPublishedAtDesc(BlogStatus.PUBLISHED, pageable)
                .map(blogMapper::toSummaryDto);
    }

    @Override
    @Transactional
    public BlogEntryResponse getBySlug(String slug) {
        BlogEntry blogEntry = blogRepository.findBySlugAndStatus(slug, BlogStatus.PUBLISHED)
                .orElseThrow(() -> new ApiException("Yayınlanmış blog yazısı bulunamadı.", HttpStatus.NOT_FOUND));
        this.increaseViewCount(blogEntry.getId());
        return blogMapper.toResponseDto(blogEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogEntrySummary> getMyBlogs(String authenticatedUsername, Pageable pageable) {
        return blogRepository.findByAuthor_UsernameOrderByCreatedAtDesc(authenticatedUsername, pageable)
                .map(blogMapper::toSummaryDto);
    }

    @Override
    @Transactional
    public void increaseViewCount(Long id) {
        blogRepository.findById(id).ifPresent(blog -> {
            blog.setViewCount(blog.getViewCount() + 1);
            blogRepository.save(blog);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogEntrySummary> searchBlogs(String query) {
        return blogRepository.searchPublic(query).stream()
                .map(blogMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    private String ensureUniqueSlug(String slug) {
        String finalSlug = slug;
        int count = 1;
        while (blogRepository.findBySlugAndStatus(finalSlug, BlogStatus.PUBLISHED).isPresent()) {
            finalSlug = slug + "-" + count++;
        }
        return finalSlug;
    }

    @Override
    @Transactional
    public BlogEntryResponse updateStatus(String authenticatedUsername, Long id, BlogStatus newStatus) {
        // Blog'u bul
        BlogEntry blogEntry = blogRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        "Blog yazısı bulunamadı. ID: " + id,
                        HttpStatus.NOT_FOUND
                ));

        // Current user'ı bul
        User currentUser = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new ApiException(
                        "Geçerli kullanıcı bulunamadı.",
                        HttpStatus.UNAUTHORIZED
                ));

        // Yetki kontrolü - Sadece kendi blogu veya admin
        boolean isAuthor = Objects.equals(
                blogEntry.getAuthor().getUsername(),
                authenticatedUsername
        );
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        if (!isAuthor && !isAdmin) {
            throw new ApiException(
                    "Bu blog yazısının durumunu değiştirme yetkiniz yok.",
                    HttpStatus.FORBIDDEN
            );
        }

        // Sadece status güncelle (moderasyon ATLA!)
        blogEntry.setStatus(newStatus);

        // Kaydet
        BlogEntry updatedBlog = blogRepository.save(blogEntry);

        // Response
        return blogMapper.toResponseDto(updatedBlog);
    }
}
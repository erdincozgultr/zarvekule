package com.zarvekule.search.service;

import com.zarvekule.blog.entity.BlogEntry;
import com.zarvekule.blog.repository.BlogEntryRepository;
import com.zarvekule.homebrew.entity.HomebrewEntry;
import com.zarvekule.homebrew.repository.HomebrewEntryRepository;
import com.zarvekule.search.dto.GlobalSearchResponse;
import com.zarvekule.search.enums.SearchResultType;
import com.zarvekule.wiki.entity.WikiEntry;
import com.zarvekule.wiki.repository.WikiEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GlobalSearchServiceImpl implements GlobalSearchService {

    private final WikiEntryRepository wikiRepository;
    private final BlogEntryRepository blogRepository;
    private final HomebrewEntryRepository homebrewRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GlobalSearchResponse> search(String query) {
        List<GlobalSearchResponse> results = new ArrayList<>();

        if (query == null || query.trim().length() < 3) {
            return results;
        }

        String searchTerm = query.trim();

        List<WikiEntry> wikiEntries = wikiRepository.searchPublic(searchTerm);
        for (WikiEntry w : wikiEntries) {
            GlobalSearchResponse dto = new GlobalSearchResponse();
            dto.setTitle(w.getTitle());
            dto.setSlug(w.getSlug());
            dto.setType(SearchResultType.WIKI);
            dto.setUrl("/wiki/" + w.getSlug());
            results.add(dto);
        }

        List<BlogEntry> blogEntries = blogRepository.searchPublic(searchTerm);
        for (BlogEntry b : blogEntries) {
            GlobalSearchResponse dto = new GlobalSearchResponse();
            dto.setTitle(b.getTitle());
            dto.setDescription(getDescriptionSnippet(b.getContent()));
            dto.setSlug(b.getSlug());
            dto.setType(SearchResultType.BLOG);
            dto.setUrl("/blog/" + b.getSlug());
            results.add(dto);
        }

        List<HomebrewEntry> homebrewEntries = homebrewRepository.searchPublic(searchTerm);
        for (HomebrewEntry h : homebrewEntries) {
            GlobalSearchResponse dto = new GlobalSearchResponse();
            dto.setTitle(h.getName());
            dto.setDescription(h.getExcerpt() != null ? h.getExcerpt() : getDescriptionSnippet(h.getDescription()));
            dto.setSlug(h.getSlug());
            dto.setType(SearchResultType.HOMEBREW);
            dto.setUrl("/homebrew/" + h.getSlug());
            results.add(dto);
        }

        return results;
    }

    private String getDescriptionSnippet(String content) {
        if (content == null) return "";
        return content.length() > 100 ? content.substring(0, 100) + "..." : content;
    }
}
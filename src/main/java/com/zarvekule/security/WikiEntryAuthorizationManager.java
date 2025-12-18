package com.zarvekule.security;

import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.UserRepository;
import com.zarvekule.wiki.entity.WikiEntry;
import com.zarvekule.wiki.repository.WikiEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("wikiEntryAuthorization")
@RequiredArgsConstructor
public class WikiEntryAuthorizationManager {

    private final WikiEntryRepository wikiEntryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public boolean isAuthor(Long entryId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return false;
        }

        String username = authentication.getName();

        WikiEntry entry = wikiEntryRepository.findById(entryId).orElse(null);
        if (entry == null) return false;

        if (entry.getAuthor() == null) return false;

        User currentUser = userRepository.findByUsername(username).orElse(null);
        if (currentUser == null) return false;

        return entry.getAuthor().getId().equals(currentUser.getId());
    }
}
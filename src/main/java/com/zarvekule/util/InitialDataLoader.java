package com.zarvekule.util;

import com.zarvekule.gamification.entity.Badge;
import com.zarvekule.gamification.enums.BadgeCategory;
import com.zarvekule.gamification.repository.BadgeRepository;
import com.zarvekule.user.entity.Role;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.enums.ERole;
import com.zarvekule.user.repository.RoleRepository;
import com.zarvekule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;

import static com.zarvekule.gamification.enums.BadgeCategory.*;

@Component
@RequiredArgsConstructor
public class InitialDataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository; // Gamification paketi için
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createRolesIfNotFound();

        createAdminUserIfNotFound();

        createInitialBadges();
    }

    private void createRolesIfNotFound() {
        for (ERole roleName : ERole.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role(roleName);
                roleRepository.save(role);
                System.out.println("Sistem Rolü Eklendi: " + roleName);
            }
        }
    }

    private void createAdminUserIfNotFound() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@zarvekule.com");
            admin.setPassword(passwordEncoder.encode("admin123"));

            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

            admin.setRoles(new HashSet<>());
            admin.getRoles().add(adminRole);
            admin.getRoles().add(userRole);

            admin.setBanned(false);

            userRepository.save(admin);
            System.out.println("Süper Admin Kullanıcısı Oluşturuldu: admin / admin123");
        }
    }

    private void createInitialBadges() {

        // ==================== 1. İLK ADIMLAR ====================
        createBadgeIfNotFound(
                "Hoş Geldin!",
                "PROFILE_COMPLETE",
                "Profilini tamamladın",
                "🎭",
                BadgeCategory.FIRST_STEPS
        );

        createBadgeIfNotFound(
                "İlk Yorum",
                "FIRST_COMMENT",
                "İlk yorumunu yaptın",
                "💬",
                BadgeCategory.FIRST_STEPS
        );

        createBadgeIfNotFound(
                "İlk Eser",
                "FIRST_HOMEBREW",
                "İlk homebrew'unu paylaştın",
                "📜",
                BadgeCategory.FIRST_STEPS
        );

        createBadgeIfNotFound(
                "İlk Yazı",
                "FIRST_BLOG",
                "İlk blog yazını yazdın",
                "✍️",
                BadgeCategory.FIRST_STEPS
        );

        // ==================== 2. İÇERİK ÜRETİCİSİ ====================

        // Homebrew rozetleri
        createBadgeIfNotFound(
                "Çırak Yaratıcı",
                "BREWER_1",
                "1 homebrew paylaştın",
                "⚗️",
                BadgeCategory.CONTENT_CREATOR
        );

        createBadgeIfNotFound(
                "Usta Yaratıcı",
                "BREWER_10",
                "10 homebrew paylaştın (Bronz)",
                "🥉",
                BadgeCategory.CONTENT_CREATOR
        );

        createBadgeIfNotFound(
                "Efsane Yaratıcı",
                "BREWER_50",
                "50 homebrew paylaştın (Gümüş)",
                "🥈",
                BadgeCategory.CONTENT_CREATOR
        );

        createBadgeIfNotFound(
                "Tanrısal Yaratıcı",
                "BREWER_100",
                "100 homebrew paylaştın (Altın)",
                "🥇",
                BadgeCategory.CONTENT_CREATOR
        );

        // Blog rozetleri
        createBadgeIfNotFound(
                "Yazar",
                "BLOGGER_5",
                "5 blog yazısı yazdın",
                "📝",
                BadgeCategory.CONTENT_CREATOR
        );

        createBadgeIfNotFound(
                "Kronikçi",
                "BLOGGER_20",
                "20 blog yazısı yazdın",
                "📚",
                BadgeCategory.CONTENT_CREATOR
        );

        createBadgeIfNotFound(
                "Hikaye Ustası",
                "BLOGGER_50",
                "50 blog yazısı yazdın",
                "🖋️",
                BadgeCategory.CONTENT_CREATOR
        );

        // ==================== 3. TOPLULUK ====================

        // Yorum rozetleri
        createBadgeIfNotFound(
                "Konuşkan",
                "COMMENT_10",
                "10 yorum yaptın",
                "💭",
                BadgeCategory.COMMUNITY
        );

        createBadgeIfNotFound(
                "Hatip",
                "COMMENT_50",
                "50 yorum yaptın (Bronz)",
                "🗣️",
                BadgeCategory.COMMUNITY
        );

        createBadgeIfNotFound(
                "Ozan",
                "COMMENT_200",
                "200 yorum yaptın (Gümüş)",
                "🎤",
                BadgeCategory.COMMUNITY
        );

        createBadgeIfNotFound(
                "İmparatorluk Ozanı",
                "COMMENT_500",
                "500 yorum yaptın (Altın)",
                "👑",
                BadgeCategory.COMMUNITY
        );

        // Beğeni rozetleri
        createBadgeIfNotFound(
                "Sevilen",
                "MID_LIKED",
                "100 beğeni aldın",
                "❤️",
                BadgeCategory.COMMUNITY
        );

        createBadgeIfNotFound(
                "Popüler",
                "MOST_LIKED",
                "500 beğeni aldın",
                "⭐",
                BadgeCategory.COMMUNITY
        );

        createBadgeIfNotFound(
                "Efsane",
                "SUPER_LIKED",
                "1000 beğeni aldın",
                "🌟",
                BadgeCategory.COMMUNITY
        );

        // ==================== 4. TAVERNA (XP, GUILD) ====================

        // XP rozetleri
        createBadgeIfNotFound(
                "Çaylak Maceracı",
                "XP_1000",
                "1,000 XP kazandın",
                "🗡️",
                BadgeCategory.TAVERNA
        );

        createBadgeIfNotFound(
                "Deneyimli Gezgin",
                "XP_5000",
                "5,000 XP kazandın",
                "⚔️",
                BadgeCategory.TAVERNA
        );

        createBadgeIfNotFound(
                "Kahraman",
                "XP_10000",
                "10,000 XP kazandın",
                "🛡️",
                BadgeCategory.TAVERNA
        );

        createBadgeIfNotFound(
                "Efsane Savaşçı",
                "XP_50000",
                "50,000 XP kazandın",
                "👑",
                BadgeCategory.TAVERNA
        );

        // Guild rozetleri
        createBadgeIfNotFound(
                "Lonca Üyesi",
                "GUILD_JOIN",
                "Bir loncaya katıldın",
                "🏰",
                BadgeCategory.TAVERNA
        );

        createBadgeIfNotFound(
                "Lonca Kurucusu",
                "GUILD_FOUNDER",
                "Bir lonca kurdun",
                "🏛️",
                BadgeCategory.TAVERNA
        );

        createBadgeIfNotFound(
                "Lonca Lideri",
                "GUILD_LEADER_ACTIVE",
                "Aktif lonca liderisin",
                "👑",
                BadgeCategory.TAVERNA
        );

        // ==================== 5. PARTY FINDER ====================

        createBadgeIfNotFound(
                "Dungeon Master",
                "DM_CREATE",
                "İlk kampanyanı oluşturdun",
                "🎲",
                BadgeCategory.PARTY_FINDER
        );

        createBadgeIfNotFound(
                "Oyuncu",
                "PLAYER_JOIN",
                "Bir kampanyaya katıldın",
                "🎭",
                BadgeCategory.PARTY_FINDER
        );

        createBadgeIfNotFound(
                "Deneyimli DM",
                "DM_5_CAMPAIGNS",
                "5 kampanya oluşturdun",
                "🎯",
                BadgeCategory.PARTY_FINDER
        );

        createBadgeIfNotFound(
                "Veteran Oyuncu",
                "PLAYER_10_CAMPAIGNS",
                "10 kampanyaya katıldın",
                "🏅",
                BadgeCategory.PARTY_FINDER
        );

        // ==================== 6. ÖZEL ROZETLER ====================

        createBadgeIfNotFound(
                "Öncü",
                "EARLY_ADOPTER",
                "Sitedeki ilk 100 kullanıcıdan birisin",
                "🚀",
                BadgeCategory.SPECIAL
        );

        createBadgeIfNotFound(
                "Beta Savaşçısı",
                "BETA_TESTER",
                "Beta döneminde katıldın",
                "🧪",
                BadgeCategory.SPECIAL
        );

        // Sadakat rozetleri
        createBadgeIfNotFound(
                "Sadık Gezgin",
                "MEMBER_3M",
                "3 aydır bizimlesin",
                "📅",
                BadgeCategory.SPECIAL
        );

        createBadgeIfNotFound(
                "Eski Dost",
                "MEMBER_1Y",
                "1 yıldır bizimlesin",
                "🎂",
                BadgeCategory.SPECIAL
        );

        createBadgeIfNotFound(
                "Efsane Üye",
                "MEMBER_2Y",
                "2 yıldır bizimlesin",
                "🏆",
                BadgeCategory.SPECIAL
        );

        // Donor rozetleri
        createBadgeIfNotFound(
                "Destekçi",
                "DONOR_TIER_1",
                "Tier 1 bağışçısı",
                "💎",
                BadgeCategory.SPECIAL
        );

        createBadgeIfNotFound(
                "Hami",
                "DONOR_TIER_2",
                "Tier 2 bağışçısı",
                "💍",
                BadgeCategory.SPECIAL
        );

        createBadgeIfNotFound(
                "Büyük Hami",
                "DONOR_TIER_3",
                "Tier 3 bağışçısı",
                "👑",
                BadgeCategory.SPECIAL
        );

    }

    private void createBadgeIfNotFound(String name, String code, String description,
                                       String emoji, BadgeCategory category) {
        if (badgeRepository.findByConditionCode(code).isEmpty()) {
            Badge badge = new Badge();
            badge.setName(name);
            badge.setConditionCode(code);
            badge.setDescription(description);
            badge.setIconUrl(emoji); // Emoji kullanıyoruz (iconUrl yerine)
            badge.setCategory(category);
            badgeRepository.save(badge);
        }
    }
}
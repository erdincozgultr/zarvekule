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

        createBadges();
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

    private void createBadges() {
        // --- 1. EŞSİZLER ---
        createBadgeIfNotFound("İlk Ses", "FIRST_COMMENT", "Sessizliği bozdun! İlk yorumunu yaptın.", "icon_first_comment.png", FIRST_STEPS);
        createBadgeIfNotFound("Yaratılış Kıvılcımı", "FIRST_HOMEBREW", "İlk Homebrew içeriğini oluşturdun.", "icon_first_homebrew.png", FIRST_STEPS);
        createBadgeIfNotFound("Hikaye Başlıyor", "FIRST_BLOG", "İlk Blog yazını paylaştın.", "icon_first_blog.png", FIRST_STEPS);
        createBadgeIfNotFound("Halk Efsanesi", "MID_LIKED", "Bir içeriğin 100'den fazla beğeni aldı.", "icon_mid_liked.png", FIRST_STEPS);
        createBadgeIfNotFound("Çağlar Kahramanı", "MOST_LIKED", "Bir içeriğin 200'den fazla beğeni aldı.", "icon_most_liked.png", FIRST_STEPS);

        // --- 2. İÇERİK (Kademeli) ---
        createBadgeIfNotFound("Çırak Tasarımcı", "BREWER_1", "1 Homebrew içeriği oluşturdun. (Bakır)", "icon_brewer_bronze.png", CREATOR);
        createBadgeIfNotFound("Zanaatkar", "BREWER_10", "10 Homebrew içeriği oluşturdun. (Gümüş)", "icon_brewer_silver.png", CREATOR);
        createBadgeIfNotFound("Usta", "BREWER_50", "50 Homebrew içeriği oluşturdun. (Altın)", "icon_brewer_gold.png", CREATOR);
        createBadgeIfNotFound("Evren Mimarı", "BREWER_100", "100 Homebrew içeriği oluşturdun. (Altın)", "icon_brewer_platin.png", CREATOR);

        // --- 3. SOSYAL (Kademeli) ---
        createBadgeIfNotFound("Taverna Sakini", "COMMENT_10", "10 Yorum yaptın. (Bakır)", "icon_comment_bronze.png", COMMUNITY);
        createBadgeIfNotFound("Söz Ustası", "COMMENT_50", "50 Yorum yaptın. (Gümüş)", "icon_comment_silver.png", COMMUNITY);
        createBadgeIfNotFound("Bilge Ozan", "COMMENT_200", "200 Yorum yaptın. (Altın)", "icon_comment_gold.png", COMMUNITY);
        createBadgeIfNotFound("İmparatorluk Ozanı", "COMMENT_500", "500 Yorum yaptın. (Altın)", "icon_comment_platin.png", COMMUNITY);

        // --- 4. SADAKAT ---
        createBadgeIfNotFound("Misafir", "MEMBER_3M", "3 aydır bizimlesin.", "icon_member_bronze.png", LOYALTY);
        createBadgeIfNotFound("Sakin", "MEMBER_1Y", "1 yıldır bizimlesin.", "icon_member_silver.png", LOYALTY);
        createBadgeIfNotFound("Vatandaş", "MEMBER_2Y", "2 yıldır bizimlesin.", "icon_member_silver.png", LOYALTY);

        // --- 5. DONOR ---
        createBadgeIfNotFound("Gezgin", "DONOR_TIER_1", "Tier 1 site bağışçısı", "icon_member_bronze.png", SUPPORTER);
        createBadgeIfNotFound("Baron", "DONOR_TIER_2", "Tier 2 site bağışçısı", "icon_member_silver.png", SUPPORTER);
        createBadgeIfNotFound("Lord", "DONOR_TIER_3", "Tier 3 site bağışçısı", "icon_member_gold.png", SUPPORTER);
        createBadgeIfNotFound("Hami", "DONOR_TIER_4", "Tier 4 site bağışçısı", "icon_member_platin.png", SUPPORTER);
    }

    private void createBadgeIfNotFound(String name, String code, String description,
                                       String icon, BadgeCategory category) {
        if (badgeRepository.findByConditionCode(code).isEmpty()) {
            Badge badge = new Badge();
            badge.setName(name);
            badge.setConditionCode(code);
            badge.setDescription(description);
            badge.setIconUrl("/uploads/badges/" + icon);
            badge.setCategory(category);
            badgeRepository.save(badge);
            System.out.println("Rozet oluşturuldu: " + name);
        }
    }
}
package com.zarvekule.gamification.service;

import com.zarvekule.exceptions.ApiException;
import com.zarvekule.gamification.dto.*;
import com.zarvekule.gamification.entity.Guild;
import com.zarvekule.gamification.repository.GuildRepository;
import com.zarvekule.notification.enums.NotificationType;
import com.zarvekule.notification.service.NotificationService;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.mapper.UserMapper;
import com.zarvekule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuildServiceImpl implements GuildService {

    private final GuildRepository guildRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public List<GuildDto> getAllGuilds(String currentUsername) {
        List<Guild> guilds = guildRepository.findAll();

        // Kullanıcının guild'ini bul (varsa)
        Long userGuildId = null;
        if (currentUsername != null) {
            User user = userRepository.findByUsername(currentUsername).orElse(null);
            if (user != null) {
                Guild userGuild = guildRepository.findByMemberId(user.getId()).orElse(null);
                if (userGuild != null) {
                    userGuildId = userGuild.getId();
                }
            }
        }

        final Long finalUserGuildId = userGuildId;
        return guilds.stream()
                .map(guild -> toDto(guild, finalUserGuildId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GuildDetailDto getGuildById(Long id, String currentUsername) {
        Guild guild = guildRepository.findByIdWithMembers(id)
                .orElseThrow(() -> new ApiException("Lonca bulunamadı", HttpStatus.NOT_FOUND));

        boolean isMember = false;
        boolean isLeader = false;

        if (currentUsername != null) {
            User currentUser = userRepository.findByUsername(currentUsername).orElse(null);
            if (currentUser != null) {
                isMember = guild.getMembers().stream()
                        .anyMatch(m -> m.getId().equals(currentUser.getId()));
                isLeader = guild.getLeader() != null &&
                        guild.getLeader().getId().equals(currentUser.getId());
            }
        }

        return toDetailDto(guild, isMember, isLeader);
    }

    @Override
    @Transactional(readOnly = true)
    public GuildDto getMyGuild(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));

        Guild guild = guildRepository.findByMemberId(user.getId())
                .orElseThrow(() -> new ApiException("Herhangi bir loncada değilsiniz", HttpStatus.NOT_FOUND));

        return toDto(guild, guild.getId());
    }

    @Override
    @Transactional
    public GuildDto createGuild(String username, GuildCreateRequest request) {
        User leader = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));

        // Kullanıcı zaten bir guild'de mi?
        if (guildRepository.findByMemberId(leader.getId()).isPresent()) {
            throw new ApiException("Zaten bir loncadasınız. Önce ayrılmanız gerekiyor.", HttpStatus.BAD_REQUEST);
        }

        // İsim benzersiz mi?
        if (guildRepository.existsByName(request.name())) {
            throw new ApiException("Bu lonca adı zaten kullanılıyor", HttpStatus.BAD_REQUEST);
        }

        Guild guild = new Guild();
        guild.setName(request.name());
        guild.setDescription(request.description());
        guild.setLeader(leader);
        guild.getMembers().add(leader);
        guild.setLevel(1);
        guild.setXp(0);

        Guild saved = guildRepository.save(guild);

        log.info("Yeni lonca oluşturuldu: {} by {}", guild.getName(), username);

        return toDto(saved, saved.getId());
    }

    @Override
    @Transactional
    public GuildDto updateGuild(String username, Long guildId, GuildUpdateRequest request) {
        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(() -> new ApiException("Lonca bulunamadı", HttpStatus.NOT_FOUND));

        // Leader kontrolü
        if (guild.getLeader() == null || !guild.getLeader().getUsername().equals(username)) {
            throw new ApiException("Sadece lonca lideri güncelleyebilir", HttpStatus.FORBIDDEN);
        }

        if (request.description() != null) {
            guild.setDescription(request.description());
        }

        Guild updated = guildRepository.save(guild);

        return toDto(updated, guildId);
    }

    @Override
    @Transactional
    public void deleteGuild(String username, Long guildId) {
        Guild guild = guildRepository.findByIdWithMembers(guildId)
                .orElseThrow(() -> new ApiException("Lonca bulunamadı", HttpStatus.NOT_FOUND));

        // Leader kontrolü
        if (guild.getLeader() == null || !guild.getLeader().getUsername().equals(username)) {
            throw new ApiException("Sadece lonca lideri silebilir", HttpStatus.FORBIDDEN);
        }

        // Tüm üyelere bildirim gönder
        guild.getMembers().forEach(member -> {
            if (!member.getUsername().equals(username)) {
                notificationService.createNotification(
                        member,
                        "Lonca Dağıldı",
                        guild.getName() + " loncası lideri tarafından dağıtıldı.",
                        NotificationType.SYSTEM,
                        "/taverna/loncalar"
                );
            }
        });

        guildRepository.delete(guild);

        log.info("Lonca silindi: {} by {}", guild.getName(), username);
    }

    @Override
    @Transactional
    public void joinGuild(String username, Long guildId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));

        Guild guild = guildRepository.findByIdWithMembers(guildId)
                .orElseThrow(() -> new ApiException("Lonca bulunamadı", HttpStatus.NOT_FOUND));

        // Kullanıcı zaten bir guild'de mi?
        if (guildRepository.findByMemberId(user.getId()).isPresent()) {
            throw new ApiException("Zaten bir loncadasınız", HttpStatus.BAD_REQUEST);
        }

        // Kullanıcıyı guild'e ekle
        guild.getMembers().add(user);
        guildRepository.save(guild);

        // Lidere bildirim
        if (guild.getLeader() != null) {
            notificationService.createNotification(
                    guild.getLeader(),
                    "Yeni Üye!",
                    user.getDisplayName() + " loncanıza katıldı.",
                    NotificationType.GUILD,
                    "/taverna/loncalar/" + guildId
            );
        }

        log.info("{} kullanıcısı {} loncasına katıldı", username, guild.getName());
    }

    @Override
    @Transactional
    public void leaveGuild(String username, Long guildId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));

        Guild guild = guildRepository.findByIdWithMembers(guildId)
                .orElseThrow(() -> new ApiException("Lonca bulunamadı", HttpStatus.NOT_FOUND));

        // Kullanıcı bu guild'in üyesi mi?
        if (guild.getMembers().stream().noneMatch(m -> m.getId().equals(user.getId()))) {
            throw new ApiException("Bu loncada değilsiniz", HttpStatus.BAD_REQUEST);
        }

        // Leader ayrılamaz (önce liderliği devretmeli veya guild'i silmeli)
        if (guild.getLeader() != null && guild.getLeader().getId().equals(user.getId())) {
            throw new ApiException("Lider loncadan ayrılamaz. Önce liderliği devredin veya loncayı silin.", HttpStatus.BAD_REQUEST);
        }

        guild.getMembers().removeIf(m -> m.getId().equals(user.getId()));
        guildRepository.save(guild);

        // Lidere bildirim
        if (guild.getLeader() != null) {
            notificationService.createNotification(
                    guild.getLeader(),
                    "Üye Ayrıldı",
                    user.getDisplayName() + " loncanızdan ayrıldı.",
                    NotificationType.GUILD,
                    "/taverna/loncalar/" + guildId
            );
        }

        log.info("{} kullanıcısı {} loncasından ayrıldı", username, guild.getName());
    }

    @Override
    @Transactional
    public void kickMember(String leaderUsername, Long guildId, Long memberId) {
        Guild guild = guildRepository.findByIdWithMembers(guildId)
                .orElseThrow(() -> new ApiException("Lonca bulunamadı", HttpStatus.NOT_FOUND));

        // Leader kontrolü
        if (guild.getLeader() == null || !guild.getLeader().getUsername().equals(leaderUsername)) {
            throw new ApiException("Sadece lonca lideri üye atabilir", HttpStatus.FORBIDDEN);
        }

        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));

        // Lider kendini atamaz
        if (member.getId().equals(guild.getLeader().getId())) {
            throw new ApiException("Lider kendini atamaz", HttpStatus.BAD_REQUEST);
        }

        guild.getMembers().removeIf(m -> m.getId().equals(memberId));
        guildRepository.save(guild);

        // Atılan üyeye bildirim
        notificationService.createNotification(
                member,
                "Loncadan Atıldınız",
                guild.getName() + " loncasından atıldınız.",
                NotificationType.SYSTEM,
                "/taverna/loncalar"
        );

        log.info("{} kullanıcısı {} loncasından {} tarafından atıldı",
                member.getUsername(), guild.getName(), leaderUsername);
    }

    // ==================== HELPER METHODS ====================

    private GuildDto toDto(Guild guild, Long userGuildId) {
        GuildDto dto = new GuildDto();
        dto.setId(guild.getId());
        dto.setName(guild.getName());
        dto.setDescription(guild.getDescription());
        dto.setLevel(guild.getLevel());
        dto.setXp(guild.getXp());
        dto.setMemberCount(guild.getMembers() != null ? guild.getMembers().size() : 0);
        dto.setLeader(guild.getLeader() != null ? userMapper.toSummaryDto(guild.getLeader()) : null);
        dto.setMember(userGuildId != null && userGuildId.equals(guild.getId()));
        return dto;
    }

    private GuildDetailDto toDetailDto(Guild guild, boolean isMember, boolean isLeader) {
        GuildDetailDto dto = new GuildDetailDto();
        dto.setId(guild.getId());
        dto.setName(guild.getName());
        dto.setDescription(guild.getDescription());
        dto.setLevel(guild.getLevel());
        dto.setXp(guild.getXp());
        dto.setXpForNextLevel(1000L); // Her 1000 XP'de level atlıyor varsayımı
        dto.setMemberCount(guild.getMembers() != null ? guild.getMembers().size() : 0);

        // Leader
        if (guild.getLeader() != null) {
            GuildMemberDto leaderDto = toMemberDto(guild.getLeader(), true);
            dto.setLeader(leaderDto);
        }

        // Members
        if (guild.getMembers() != null) {
            List<GuildMemberDto> members = guild.getMembers().stream()
                    .map(user -> toMemberDto(user,
                            guild.getLeader() != null && guild.getLeader().getId().equals(user.getId())))
                    .collect(Collectors.toList());
            dto.setMembers(members);
        }

        dto.setMember(isMember);
        dto.setLeader(isLeader);

        return dto;
    }

    private GuildMemberDto toMemberDto(User user, boolean isLeader) {
        GuildMemberDto dto = new GuildMemberDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setDisplayName(user.getDisplayName() != null ? user.getDisplayName() : user.getUsername());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setTitle(user.getTitle() != null ? user.getTitle() : "Gezgin");
        dto.setLeader(isLeader);
        return dto;
    }
}
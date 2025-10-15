package vn.yenthan.taskmanager.notifications.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import vn.yenthan.taskmanager.notifications.dto.response.NotificationDto;
import vn.yenthan.taskmanager.notifications.entity.NotificationEntity;
import vn.yenthan.taskmanager.core.auth.entity.User;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "boardId", source = "board.id")
    @Mapping(target = "cardId", source = "card.id")
    @Mapping(target = "actorId", source = "actor.id")
    @Mapping(target = "actorName", source = "actor.fullName")
    @Mapping(target = "actorAvatar", source = "actor.profileImageUrl")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToString")
    @Mapping(target = "memberId", ignore = true) // Will be set in service if needed
    NotificationDto toNotificationDto(NotificationEntity notification);

    List<NotificationDto> toNotificationDtoList(List<NotificationEntity> notifications);

    @Named("instantToString")
    default String instantToString(Instant instant) {
        if (instant == null) return null;
        return instant.toString();
    }
}

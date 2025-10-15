package vn.yenthan.taskmanager.scrumboard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import vn.yenthan.taskmanager.scrumboard.dto.response.*;
import vn.yenthan.taskmanager.scrumboard.entity.*;
import vn.yenthan.taskmanager.core.auth.entity.User;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ScrumboardMapper {

    // Board mappings
    @Mapping(target = "list", source = "lists")
    BoardDto toBoardDto(BoardEntity board);

    List<BoardDto> toBoardDtoList(List<BoardEntity> boards);

    // List mappings
    @Mapping(target = "cards", source = "cards", qualifiedByName = "cardSetToList")
    CardListDto toCardListDto(ListEntity list);

    List<CardListDto> toCardListDtoList(List<ListEntity> lists);

    // Card mappings
    @Mapping(target = "desc", source = "description")
    @Mapping(target = "date", source = "date", qualifiedByName = "instantToString")
    @Mapping(target = "laneId", source = "list.id")
    @Mapping(target = "label", source = "labels", qualifiedByName = "cardLabelsToLabelDtos")
    @Mapping(target = "members", source = "members", qualifiedByName = "cardMembersToMemberDtos")
    @Mapping(target = "attachments", source = "attachments")
    @Mapping(target = "comments", source = "comments", qualifiedByName = "commentsToEmptyList")
    @Mapping(target = "checkedList", source = "checklists", qualifiedByName = "checklistsToCheckedListDtos")
    CardDto toCardDto(CardEntity card);

    List<CardDto> toCardDtoList(List<CardEntity> cards);

    // Member mappings
    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "name", source = "user.fullName")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "avatar", source = "user.profileImageUrl")
    @Mapping(target = "role", source = "boardRole.name")
    @Mapping(target = "joinedAt", source = "joinedAt", qualifiedByName = "instantToString")
    @Mapping(target = "lastActive", source = "updatedAt", qualifiedByName = "instantToString")
    @Mapping(target = "boards", source = "user", qualifiedByName = "getBoardCount")
    @Mapping(target = "tasks", source = "user", qualifiedByName = "getTaskCount")
    MemberDto toMemberDto(BoardMemberEntity boardMember);

    List<MemberDto> toMemberDtoList(List<BoardMemberEntity> boardMembers);

    // Label mappings
    @Mapping(target = "type", constant = "1")
    LabelDto toLabelDto(LabelEntity label);

    List<LabelDto> toLabelDtoList(List<LabelEntity> labels);

    // Attachment mappings
    @Mapping(target = "file.path", source = "filePath")
    @Mapping(target = "file.name", source = "fileName")
    @Mapping(target = "file.lastModified", source = "fileLastModified")
    @Mapping(target = "file.lastModifiedDate", source = "fileLastModifiedDate")
    AttachmentDto toAttachmentDto(AttachmentEntity attachment);

    List<AttachmentDto> toAttachmentDtoList(List<AttachmentEntity> attachments);

    // CheckedList mappings
    CheckedListDto toCheckedListDto(ChecklistItemEntity checklistItem);

    List<CheckedListDto> toCheckedListDtoList(List<ChecklistItemEntity> checklistItems);

    // Named methods for complex mappings
    @Named("instantToString")
    default String instantToString(Instant instant) {
        if (instant == null) return null;
        return instant.toString();
    }

    @Named("cardLabelsToLabelDtos")
    default List<LabelDto> cardLabelsToLabelDtos(List<CardLabelEntity> cardLabels) {
        if (cardLabels == null) return List.of();
        return cardLabels.stream()
                .map(CardLabelEntity::getLabel)
                .map(this::toLabelDto)
                .toList();
    }

    @Named("cardMembersToMemberDtos")
    default List<MemberDto> cardMembersToMemberDtos(List<CardMemberEntity> cardMembers) {
        if (cardMembers == null) return List.of();
        return cardMembers.stream()
                .map(cardMember -> {
                    MemberDto memberDto = new MemberDto();
                    memberDto.setId(cardMember.getUser().getId());
                    memberDto.setName(cardMember.getUser().getFullName());
                    memberDto.setEmail(cardMember.getUser().getEmail());
                    memberDto.setAvatar(cardMember.getUser().getProfileImageUrl());
                    return memberDto;
                })
                .toList();
    }

    @Named("commentsToEmptyList")
    default List<Object> commentsToEmptyList(List<CommentEntity> comments) {
        return List.of(); // Empty array as per requirements
    }

    @Named("checklistsToCheckedListDtos")
    default List<CheckedListDto> checklistsToCheckedListDtos(List<ChecklistEntity> checklists) {
        if (checklists == null) return List.of();
        return checklists.stream()
                .flatMap(checklist -> checklist.getItems().stream())
                .map(this::toCheckedListDto)
                .toList();
    }

    @Named("getBoardCount")
    default Integer getBoardCount(User user) {
        // This would need to be calculated in service layer
        return 0;
    }

    @Named("getTaskCount")
    default Integer getTaskCount(User user) {
        // This would need to be calculated in service layer using CardMemberRepository.countByUserId
        return 0;
    }

    @Named("cardSetToList")
    default List<CardDto> cardSetToList(Set<CardEntity> cards) {
        if (cards == null) return List.of();
        return cards.stream()
                .map(this::toCardDto)
                .collect(Collectors.toList());
    }
}

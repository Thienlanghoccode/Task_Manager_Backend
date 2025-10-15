package vn.yenthan.taskmanager.scrumboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yenthan.taskmanager.core.exception.payload.NotFoundException;
import vn.yenthan.taskmanager.scrumboard.dto.request.CreateCardRequest;
import vn.yenthan.taskmanager.scrumboard.dto.request.UpdateCardRequest;
import vn.yenthan.taskmanager.scrumboard.dto.request.UpdateCardCategoryRequest;
import vn.yenthan.taskmanager.scrumboard.dto.response.CardDto;
import vn.yenthan.taskmanager.scrumboard.entity.*;
import vn.yenthan.taskmanager.scrumboard.mapper.ScrumboardMapper;
import vn.yenthan.taskmanager.scrumboard.repository.*;
import vn.yenthan.taskmanager.core.auth.repository.UserRepository;
import vn.yenthan.taskmanager.core.auth.entity.User;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CardService {

    private final CardRepository cardRepository;
    private final ListRepository listRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final CardMemberRepository cardMemberRepository;
    private final CardLabelRepository cardLabelRepository;
    private final ScrumboardMapper scrumboardMapper;

    @Transactional(readOnly = true)
    public List<CardDto> getCardsByListId(Long listId) {
        log.info("Fetching cards for list with id: {}", listId);
        List<CardEntity> cards = cardRepository.findByListIdWithMembers(listId);
        return scrumboardMapper.toCardDtoList(cards);
    }

    @Transactional(readOnly = true)
    public CardDto getCardById(Long id) {
        log.info("Fetching card with id: {}", id);
        
        // Fetch card with all related data using separate queries to avoid MultipleBagFetchException
        CardEntity card = cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Card not found with id: " + id));
        
        // Load related data separately
        cardRepository.findByIdWithMembers(id).ifPresent(c -> {
            card.setMembers(c.getMembers());
        });
        
        cardRepository.findByIdWithLabels(id).ifPresent(c -> {
            card.setLabels(c.getLabels());
        });
        
        cardRepository.findByIdWithAttachments(id).ifPresent(c -> {
            card.setAttachments(c.getAttachments());
        });
        
        cardRepository.findByIdWithComments(id).ifPresent(c -> {
            card.setComments(c.getComments());
        });
        
        cardRepository.findByIdWithChecklists(id).ifPresent(c -> {
            card.setChecklists(c.getChecklists());
        });
        
        // Load checklist items separately to avoid MultipleBagFetchException
        List<ChecklistEntity> checklistsWithItems = cardRepository.findChecklistsWithItemsByCardId(id);
        card.getChecklists().forEach(checklist -> {
            checklistsWithItems.stream()
                    .filter(ch -> ch.getId().equals(checklist.getId()))
                    .findFirst()
                    .ifPresent(ch -> checklist.setItems(ch.getItems()));
        });
        
        return scrumboardMapper.toCardDto(card);
    }

    public CardDto createCard(CreateCardRequest request) {
        log.info("Creating new card with title: {}", request.getTitle());
        
        ListEntity list = listRepository.findById(request.getLaneId())
                .orElseThrow(() -> new NotFoundException("List not found with id: " + request.getLaneId()));

        CardEntity card = new CardEntity();
        card.setTitle(request.getTitle());
        card.setDescription(request.getDescription());
        card.setList(list);
        
        if (request.getDate() != null && !request.getDate().isEmpty()) {
            try {
                card.setDate(Instant.parse(request.getDate()));
            } catch (Exception e) {
                log.warn("Invalid date format: {}", request.getDate());
            }
        }
        
        CardEntity savedCard = cardRepository.save(card);
        
        // Add members if provided
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            addMembersToCard(savedCard.getId(), request.getMemberIds());
        }
        
        // Add labels if provided
        if (request.getLabelIds() != null && !request.getLabelIds().isEmpty()) {
            addLabelsToCard(savedCard.getId(), request.getLabelIds());
        }
        
        log.info("Card created successfully with id: {}", savedCard.getId());
        return scrumboardMapper.toCardDto(savedCard);
    }

    public CardDto updateCard(UpdateCardRequest request) {
        log.info("Updating card with id: {}", request.getId());
        
        CardEntity card = cardRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Card not found with id: " + request.getId()));

        card.setTitle(request.getTitle());
        card.setDescription(request.getDescription());
        
        if (request.getDate() != null && !request.getDate().isEmpty()) {
            try {
                card.setDate(Instant.parse(request.getDate()));
            } catch (Exception e) {
                log.warn("Invalid date format: {}", request.getDate());
            }
        }
        
        if (request.getLaneId() != null) {
            ListEntity list = listRepository.findById(request.getLaneId())
                    .orElseThrow(() -> new NotFoundException("List not found with id: " + request.getLaneId()));
            card.setList(list);
        }
        
        CardEntity updatedCard = cardRepository.save(card);
        
        // Update members if provided
        if (request.getMemberIds() != null) {
            updateCardMembers(updatedCard.getId(), request.getMemberIds());
        }
        
        // Update labels if provided
        if (request.getLabelIds() != null) {
            updateCardLabels(updatedCard.getId(), request.getLabelIds());
        }
        
        log.info("Card updated successfully with id: {}", updatedCard.getId());
        return scrumboardMapper.toCardDto(updatedCard);
    }

    public CardDto updateCardCategory(UpdateCardCategoryRequest request) {
        log.info("Moving card {} to list {}", request.getCardId(), request.getLaneId());
        
        CardEntity card = cardRepository.findById(request.getCardId())
                .orElseThrow(() -> new NotFoundException("Card not found with id: " + request.getCardId()));
        
        ListEntity newList = listRepository.findById(request.getLaneId())
                .orElseThrow(() -> new NotFoundException("List not found with id: " + request.getLaneId()));
        
        card.setList(newList);
        CardEntity updatedCard = cardRepository.save(card);
        
        log.info("Card moved successfully to list {}", request.getLaneId());
        return scrumboardMapper.toCardDto(updatedCard);
    }

    public void deleteCard(Long id) {
        log.info("Deleting card with id: {}", id);
        
        if (!cardRepository.existsById(id)) {
            throw new NotFoundException("Card not found with id: " + id);
        }
        
        cardRepository.deleteById(id);
        log.info("Card deleted successfully with id: {}", id);
    }

    private void addMembersToCard(Long cardId, List<Long> memberIds) {
        for (Long memberId : memberIds) {
            if (!cardMemberRepository.existsByCardIdAndUserId(cardId, memberId)) {
                User user = userRepository.findById(memberId)
                        .orElseThrow(() -> new NotFoundException("User not found with id: " + memberId));
                
                CardEntity card = cardRepository.findById(cardId)
                        .orElseThrow(() -> new NotFoundException("Card not found with id: " + cardId));
                
                CardMemberEntity cardMember = new CardMemberEntity();
                cardMember.setCard(card);
                cardMember.setUser(user);
                cardMemberRepository.save(cardMember);
            }
        }
    }

    private void addLabelsToCard(Long cardId, List<Long> labelIds) {
        for (Long labelId : labelIds) {
            if (!cardLabelRepository.existsByCardIdAndLabelId(cardId, labelId)) {
                LabelEntity label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new NotFoundException("Label not found with id: " + labelId));
                
                CardEntity card = cardRepository.findById(cardId)
                        .orElseThrow(() -> new NotFoundException("Card not found with id: " + cardId));
                
                CardLabelEntity cardLabel = new CardLabelEntity();
                cardLabel.setCard(card);
                cardLabel.setLabel(label);
                cardLabelRepository.save(cardLabel);
            }
        }
    }

    private void updateCardMembers(Long cardId, List<Long> memberIds) {
        // Remove existing members
        cardMemberRepository.deleteByCardId(cardId);
        
        // Add new members
        if (!memberIds.isEmpty()) {
            addMembersToCard(cardId, memberIds);
        }
    }

    private void updateCardLabels(Long cardId, List<Long> labelIds) {
        // Remove existing labels
        cardLabelRepository.deleteByCardId(cardId);
        
        // Add new labels
        if (!labelIds.isEmpty()) {
            addLabelsToCard(cardId, labelIds);
        }
    }
}

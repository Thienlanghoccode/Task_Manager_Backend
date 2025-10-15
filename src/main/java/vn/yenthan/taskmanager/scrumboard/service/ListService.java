package vn.yenthan.taskmanager.scrumboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yenthan.taskmanager.core.exception.payload.NotFoundException;
import vn.yenthan.taskmanager.scrumboard.dto.request.CreateListRequest;
import vn.yenthan.taskmanager.scrumboard.dto.request.UpdateListRequest;
import vn.yenthan.taskmanager.scrumboard.dto.response.CardListDto;
import vn.yenthan.taskmanager.scrumboard.entity.BoardEntity;
import vn.yenthan.taskmanager.scrumboard.entity.ListEntity;
import vn.yenthan.taskmanager.scrumboard.mapper.ScrumboardMapper;
import vn.yenthan.taskmanager.scrumboard.repository.BoardRepository;
import vn.yenthan.taskmanager.scrumboard.repository.ListRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ListService {

    private final ListRepository listRepository;
    private final BoardRepository boardRepository;
    private final ScrumboardMapper scrumboardMapper;

    @Transactional(readOnly = true)
    public List<CardListDto> getListsByBoardId(Long boardId) {
        log.info("Fetching lists for board with id: {}", boardId);
        List<ListEntity> lists = listRepository.findByBoardIdWithCards(boardId);
        return scrumboardMapper.toCardListDtoList(lists);
    }

    @Transactional(readOnly = true)
    public CardListDto getListById(Long id) {
        log.info("Fetching list with id: {}", id);
        ListEntity list = listRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new NotFoundException("List not found with id: " + id));
        return scrumboardMapper.toCardListDto(list);
    }

    public CardListDto createList(CreateListRequest request) {
        log.info("Creating new list with name: {} for board: {}", request.getName(), request.getBoardId());
        
        BoardEntity board = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new NotFoundException("Board not found with id: " + request.getBoardId()));

        if (listRepository.existsByNameAndBoardId(request.getName(), request.getBoardId())) {
            throw new IllegalArgumentException("List with name '" + request.getName() + "' already exists in this board");
        }

        ListEntity list = new ListEntity();
        list.setName(request.getName());
        list.setBoard(board);
        
        ListEntity savedList = listRepository.save(list);
        log.info("List created successfully with id: {}", savedList.getId());
        
        return scrumboardMapper.toCardListDto(savedList);
    }

    public CardListDto updateList(UpdateListRequest request) {
        log.info("Updating list with id: {}", request.getId());
        
        ListEntity list = listRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("List not found with id: " + request.getId()));

        if (!list.getName().equals(request.getName()) && 
            listRepository.existsByNameAndBoardId(request.getName(), list.getBoard().getId())) {
            throw new IllegalArgumentException("List with name '" + request.getName() + "' already exists in this board");
        }

        list.setName(request.getName());
        ListEntity updatedList = listRepository.save(list);
        log.info("List updated successfully with id: {}", updatedList.getId());
        
        return scrumboardMapper.toCardListDto(updatedList);
    }

    public void deleteList(Long id) {
        log.info("Deleting list with id: {}", id);
        
        if (!listRepository.existsById(id)) {
            throw new NotFoundException("List not found with id: " + id);
        }
        
        listRepository.deleteById(id);
        log.info("List deleted successfully with id: {}", id);
    }
}

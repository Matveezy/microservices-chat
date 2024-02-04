package com.lab2.chat.service;

import com.lab2.chat.dto.ChatRequestDto;
import com.lab2.chat.dto.ChatResponseDto;
import com.lab2.chat.dto.ChatUsersRequestDto;
import com.lab2.chat.dto.UserReadDto;
import com.lab2.chat.entity.Chat;
import com.lab2.chat.feign.UserServiceClient;
import com.lab2.chat.mapper.CreateChatMapper;
import com.lab2.chat.mapper.ReadChatMapper;
import com.lab2.chat.repository.ChatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRepository chatRepository;
    private final ReadChatMapper readChatMapper;
    private final CreateChatMapper createChatMapper;
    private final UserServiceClient userServiceClient;

    public List<ChatResponseDto> findAll() {
        return chatRepository.findAll().stream().map(readChatMapper::mapToDto).toList();
    }

    public List<ChatResponseDto> findUserPrivateChats(Long userId) {
        return chatRepository.findByIsPrivateAndUserIdsContaining(true, userId)
                .map(readChatMapper::mapToDto).stream().toList();
    }

    public ChatResponseDto findPrivateChat(Long loggedUserId, Long chatWithUserId) {
        ResponseEntity<?> loggedUser = userServiceClient.findUserById(loggedUserId);
        ResponseEntity<?> chatWithUser = userServiceClient.findUserById(chatWithUserId);
        if (chatWithUser == null)
            throw new ResponseStatusException(BAD_REQUEST, "User with id " + chatWithUserId + " doesn't exist");
        UserReadDto loggedUserReadDto = (UserReadDto) loggedUser.getBody();
        UserReadDto chatWithUserReadDto = (UserReadDto) chatWithUser.getBody();
        String privateChatName = getPrivateChatName(loggedUserReadDto.getUsername(), chatWithUserReadDto.getUsername());
        return chatRepository.findByNameAndIsPrivate(privateChatName, true)
                .map(readChatMapper::mapToDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<ChatResponseDto> findUserGroupChats(Long userId) {
        return chatRepository.findByIsPrivateAndUserIdsContaining(false, userId)
                .map(readChatMapper::mapToDto).stream().toList();
    }

    @Transactional
    public ChatResponseDto createGroupChat(ChatRequestDto chatRequestDto) {
        if (chatRepository.existsByName(chatRequestDto.getName())) {
            throw new ResponseStatusException(BAD_REQUEST, "A chat with this name already exists");
        }
        Chat chatToSave = createChatMapper.mapToEntity(chatRequestDto);
        chatToSave.setIsPrivate(false);
        return readChatMapper.mapToDto(chatRepository.save(chatToSave));
    }

    @Transactional
    public ChatResponseDto createPrivateChat(Long loggedUserId, Long chatWithUserId) {
        ResponseEntity<?> loggedUser = userServiceClient.findUserById(loggedUserId);
        ResponseEntity<?> chatWithUser = userServiceClient.findUserById(chatWithUserId);
        if (chatWithUser == null)
            throw new ResponseStatusException(BAD_REQUEST, "User with id " + chatWithUserId + " doesn't exist");
        UserReadDto loggedUserReadDto = (UserReadDto) loggedUser.getBody();
        UserReadDto chatWithUserReadDto = (UserReadDto) chatWithUser.getBody();
        String privateChatName = getPrivateChatName(loggedUserReadDto.getUsername(), chatWithUserReadDto.getUsername());
        if (chatRepository.existsByNameAndIsPrivate(privateChatName, true))
            throw new ResponseStatusException(BAD_REQUEST, "The private chat with this user already exists");
        Chat chatEntityToSave = Chat.builder()
                .name(privateChatName)
                .isPrivate(true)
                .userIds(List.of(loggedUserId, chatWithUserId)).build();
        return readChatMapper.mapToDto(chatRepository.save(chatEntityToSave));
    }

    @Transactional
    public ChatResponseDto update(Long id, ChatRequestDto chatRequestDto) {
        Chat chatToUpdate = chatRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        chatToUpdate.setName(chatRequestDto.getName());
        return readChatMapper.mapToDto(chatRepository.save(chatToUpdate));
    }

    @Transactional
    public ChatResponseDto updateParticipants(Long id, ChatUsersRequestDto chatUsersRequestDto) {
        Chat chatToUpdate = chatRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        int countOfParticipants = chatToUpdate.getUserIds().size();
        if (countOfParticipants < 2) throw new ResponseStatusException(BAD_REQUEST, "Chat must have at least 2 users");
        List<Long> usersIdsToAdd = chatUsersRequestDto.getUsersIds();
        chatToUpdate.getUserIds().addAll(usersIdsToAdd);
        List<Long> newUserList = chatToUpdate.getUserIds().stream().distinct().toList();
        chatToUpdate.setUserIds(newUserList);
        return readChatMapper.mapToDto(chatRepository.save(chatToUpdate));
    }

    @DeleteMapping
    public boolean delete(Long chatId) {
        Chat chatToDelete = chatRepository.findById(chatId).orElseThrow(EntityNotFoundException::new);
        chatRepository.delete(chatToDelete);
        return true;
    }

    private static String getPrivateChatName(String currentUsername, String anotherUsername) {
        return "PrivateChat_" + (currentUsername.hashCode() * anotherUsername.hashCode());
    }
}

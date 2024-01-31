package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.dto.*;
import org.example.domain.model.Chat;
import org.example.domain.model.User;
import org.example.repository.ChatRepository;
import org.example.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static com.google.common.collect.Lists.newArrayList;
import static org.example.domain.dto.PageableRequest.getPageable;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    private final UserRepository userRepository;

    private final UserService userService;

    public Page<ChatResponseDto> findAll(PageableRequest pageableRequest) {
        var pageable = getPageable(pageableRequest, "name");
        var chats = chatRepository.findAll(pageable);
        var chatsResponse = chats.stream().map(ChatResponseDto::new).toList();
        return new PageImpl<>(chatsResponse, pageable, chats.getTotalElements());
    }

    @Transactional
    public Page<ChatResponseDto> findMyPrivateChats(PageableRequest pageableRequest) {
        var currentUser = userService.getCurrentUser();
        var pageable = getPageable(pageableRequest, "name");
        var chats = chatRepository.findByPrivateBAndUsersContaining(true, currentUser, pageable);
        var chatsResponse = chats.stream().map(ChatResponseDto::new).toList();
        return new PageImpl<>(chatsResponse, pageable, chats.getTotalElements());
    }

    @Transactional
    public Page<ChatResponseDto> findMyGroupChats(PageableRequest pageableRequest) {
        var currentUser = userService.getCurrentUser();
        var pageable = getPageable(pageableRequest, "name");
        var chats = chatRepository.findByPrivateBAndUsersContaining(false, currentUser, pageable);
        var chatsResponse = chats.stream().map(ChatResponseDto::new).toList();
        return new PageImpl<>(chatsResponse, pageable, chats.getTotalElements());
    }

    @Transactional
    public ChatResponseDto findMyPrivateChat(Long userId) {
        var anotherUser = getUserByUserIdOrThrowException(userId);
        var currentUser = userService.getCurrentUser();
        throwExceptionIfAnotherUserIsCurrentUser(currentUser, anotherUser);
        var chatName = getPrivateChatName(currentUser, anotherUser);
        return new ChatResponseDto(chatRepository.findByNameAndPrivateB(chatName, true).orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND, "Chat with name \"" + chatName + "\" not found")));
    }

    private static void throwExceptionIfAnotherUserIsCurrentUser(User currentUser, User anotherUser) {
        if (currentUser.getId().equals(anotherUser.getId()))
            throw new ResponseStatusException(BAD_REQUEST, "You can't create a chat with yourself");
    }

    private User getUserByUserIdOrThrowException(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND, "User with id " + userId + " not found"));
    }

    private static String getPrivateChatName(User currentUser, User anotherUser) {
        return "PrivateChat_" + (currentUser.getUsername().hashCode() * anotherUser.getUsername().hashCode());
    }

    public ChatResponseDto createGroupChat(ChatRequestDto chat) {
        if (chatRepository.existsByName(chat.getName())) {
            throw new ResponseStatusException(BAD_REQUEST, "A chat with this name already exists");
        }
        var chatEntity = chat.toChatEntity();
        return new ChatResponseDto(chatRepository.save(chatEntity));
    }

    @Transactional
    public ChatResponseDto createPrivateChat(Long userId) {
        var anotherUser = getUserByUserIdOrThrowException(userId);
        var currentUser = userService.getCurrentUser();
        throwExceptionIfAnotherUserIsCurrentUser(currentUser, anotherUser);
        var chatName = getPrivateChatName(currentUser, anotherUser);
        if (chatRepository.existsByNameAndPrivateB(chatName, true))
            throw new ResponseStatusException(BAD_REQUEST, "The private chat with this user already exists");
        var chatEntity = new ChatRequestDto(chatName).toChatEntity(true, newArrayList(currentUser, anotherUser));
        return new ChatResponseDto(chatRepository.save(chatEntity));
    }

    public ChatResponseDto update(Long id, ChatRequestDto chat) {
        var originalChat = getChatOrThrowException(id);
        try {
            var chatEntity = chat.toChatEntity(id, originalChat.getPrivateB(), originalChat.getUsers());
            return new ChatResponseDto(chatRepository.save(chatEntity));
        } catch (Exception ex) {
            throw new ResponseStatusException(BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    private Chat getChatOrThrowException(Long chatId) {
        var chat = chatRepository.findById(chatId);
        if (chat.isEmpty()) throw new ResponseStatusException(NOT_FOUND, "Chat is not found");
        else return chat.get();
    }

    public ChatResponseDto updateParticipants(Long id, ChatUsersRequestDto chatUsersRequest) {
        var chat = getChatOrThrowException(id);
        var users = userRepository.findAllById(chatUsersRequest.getUsersIds());
        if (users.size() < 2) throw new ResponseStatusException(BAD_REQUEST, "Chat must have at least 2 users");
        try {
            chat.setUsers(users);
            chatRepository.save(chat);
            return new ChatResponseDto(chat);
        } catch (Exception ex) {
            throw new ResponseStatusException(BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    public ChatResponseDto delete(Long chatId) {
        var chat = getChatOrThrowException(chatId);
        var chatResponseDto = new ChatResponseDto(chat);
        chatRepository.deleteById(chatId);
        return chatResponseDto;
    }
}
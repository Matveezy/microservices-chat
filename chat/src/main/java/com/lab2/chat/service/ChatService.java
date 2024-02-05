package com.lab2.chat.service;

import com.lab2.chat.dto.ChatRequestDto;
import com.lab2.chat.dto.ChatResponseDto;
import com.lab2.chat.dto.ChatUsersRequestDto;
import com.lab2.chat.dto.UserReadDto;
import com.lab2.chat.entity.Chat;
import com.lab2.chat.entity.ChatParticipant;
import com.lab2.chat.exception.ChatNotExistException;
import com.lab2.chat.feign.UserServiceClient;
import com.lab2.chat.mapper.CreateChatMapper;
import com.lab2.chat.mapper.ReadChatMapper;
import com.lab2.chat.repository.ChatParticipantRepository;
import com.lab2.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ReadChatMapper readChatMapper;
    private final CreateChatMapper createChatMapper;
    private final UserServiceClient userServiceClient;
    private final ChatParticipantRepository chatParticipantRepository;

    public Flux<ChatResponseDto> findAll() {
        return chatRepository.findAll()
                .flatMap(chat -> {
                    Flux<ChatParticipant> participantsFlux = chatParticipantRepository
                            .findAllByChatParticipantKeyChatId(chat.getId());
                    return participantsFlux.collectList().map(participants ->
                    {
                        ChatResponseDto chatResponseDto = readChatMapper.mapToDto(chat);
                        if (!participants.isEmpty()) {
                            chatResponseDto.setUserIds(participants.stream().map(participant -> {
                                return participant.getUserId();
                            }).collect(Collectors.toList()));
                        }
                        return chatResponseDto;
                    });
                });

    }

    public Mono<ChatResponseDto> findById(Long id) {
        return chatRepository.findById(id)
                .switchIfEmpty(Mono.error(new ChatNotExistException("Chat with id + " + id + " doesn't exist!")))
                .flatMap(chat -> {
                    Flux<ChatParticipant> participantsFlux = chatParticipantRepository
                            .findAllByChatParticipantKeyChatId(chat.getId());
                    return participantsFlux.collectList().map(participants -> {
                        ChatResponseDto chatResponseDto = readChatMapper.mapToDto(chat);
                        if (!participants.isEmpty()) {
                            chatResponseDto.setUserIds(participants.stream().map(ChatParticipant::getUserId).collect(Collectors.toList()));
                        }
                        return chatResponseDto;
                    });
                });
    }

    public Flux<ChatResponseDto> findUserPrivateChats(Long userId) {
        return chatRepository.findByIsPrivateAndUserIds(true, userId)
                .flatMap(chat -> {
                    Flux<ChatParticipant> participantsFlux = chatParticipantRepository
                            .findAllByChatParticipantKeyChatId(chat.getId());
                    return participantsFlux.collectList().map(participants -> {
                        ChatResponseDto chatResponseDto = readChatMapper.mapToDto(chat);
                        if (!participants.isEmpty()) {
                            chatResponseDto.setUserIds(participants.stream().map(ChatParticipant::getUserId).collect(Collectors.toList()));
                        }
                        return chatResponseDto;
                    });
                });
    }

    public Mono<ChatResponseDto> findPrivateChat(Long loggedUserId, Long chatWithUserId) {
        Mono<ResponseEntity<UserReadDto>> loggedUserMono = Mono.fromCallable(() -> userServiceClient.findUserById(loggedUserId)).subscribeOn(Schedulers.boundedElastic());
        Mono<ResponseEntity<UserReadDto>> chatWithUserMono = Mono.fromCallable(() -> userServiceClient.findUserById(chatWithUserId)).subscribeOn(Schedulers.boundedElastic());
        return loggedUserMono
                .zipWith(chatWithUserMono)
                .flatMap(tuple -> {
                    if ((tuple.getT1().getBody() == null) || (tuple.getT2().getBody() == null)) {
                        return Mono.error(new ResponseStatusException(BAD_REQUEST, "User doesn't exist"));
                    }
                    UserReadDto loggedUserReadDto = tuple.getT1().getBody();
                    UserReadDto chatWithUserReadDto = tuple.getT2().getBody();
                    String privateChatName = getPrivateChatName(loggedUserReadDto.getUsername(), chatWithUserReadDto.getUsername());
                    return chatRepository.findByNameAndIsPrivate(privateChatName, true)
                            .flatMap(chat -> {
                                Flux<ChatParticipant> participantsFlux = chatParticipantRepository
                                        .findAllByChatParticipantKeyChatId(chat.getId());
                                return participantsFlux.collectList().map(participants -> {
                                    ChatResponseDto chatResponseDto = readChatMapper.mapToDto(chat);
                                    if (!participants.isEmpty()) {
                                        chatResponseDto.setUserIds(participants.stream().map(ChatParticipant::getUserId).collect(Collectors.toList()));
                                    }
                                    return chatResponseDto;
                                });
                            })
                            .switchIfEmpty(Mono.error(new ResponseStatusException(BAD_REQUEST, "Private chat not found")));
                });
    }

    public Flux<ChatResponseDto> findUserGroupChats(Long userId) {
        return chatRepository.findByIsPrivateAndUserIds(false, userId)
                .flatMap(chat -> {
                    Flux<ChatParticipant> participantsFlux = chatParticipantRepository
                            .findAllByChatParticipantKeyChatId(chat.getId());
                    return participantsFlux.collectList().map(participants -> {
                        ChatResponseDto chatResponseDto = readChatMapper.mapToDto(chat);
                        if (!participants.isEmpty()) {
                            chatResponseDto.setUserIds(participants.stream().map(ChatParticipant::getUserId).collect(Collectors.toList()));
                        }
                        return chatResponseDto;
                    });
                });
    }

    public Mono<ChatResponseDto> createGroupChat(ChatRequestDto chatRequestDto) {
        return Mono.defer(() -> chatRepository.existsByName(chatRequestDto.getName())
                .flatMap(exists -> {
                    if (exists)
                        return Mono.error(new ResponseStatusException(BAD_REQUEST, "A chat with this name already exists"));
                    Chat chatToSave = createChatMapper.mapToEntity(chatRequestDto);
                    chatToSave.setIsPrivate(false);
                    return chatRepository.save(chatToSave);
                })
                .map(readChatMapper::mapToDto));
    }

    public Mono<ChatResponseDto> createPrivateChat(Long loggedUserId, Long chatWithUserId) {
        Mono<ResponseEntity<UserReadDto>> loggedUserMono = Mono.fromCallable(() -> userServiceClient.findUserById(loggedUserId)).subscribeOn(Schedulers.boundedElastic());
        Mono<ResponseEntity<UserReadDto>> chatWithUserMono = Mono.fromCallable(() -> userServiceClient.findUserById(chatWithUserId)).subscribeOn(Schedulers.boundedElastic());
        return loggedUserMono
                .zipWith(chatWithUserMono)
                .flatMap(tuple -> {
                    if ((tuple.getT1().getBody() == null) || (tuple.getT2().getBody() == null)) {
                        return Mono.error(new ResponseStatusException(BAD_REQUEST, "User doesn't exist"));
                    }
                    UserReadDto loggedUserReadDto = tuple.getT1().getBody();
                    UserReadDto chatWithUserReadDto = tuple.getT2().getBody();
                    String privateChatName = getPrivateChatName(loggedUserReadDto.getUsername(), chatWithUserReadDto.getUsername());
                    return chatRepository.existsByNameAndIsPrivate(privateChatName, true)
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
                                }
                                Chat chat = Chat.builder()
                                        .isPrivate(true)
                                        .name(privateChatName)
                                        .build();
                                return chatRepository.save(chat);
                            })
                            .flatMap(savedChat -> {
                                return chatParticipantRepository.saveChatParticipant(loggedUserId, savedChat.getId())
                                        .zipWith(chatParticipantRepository.saveChatParticipant(chatWithUserId, savedChat.getId()))
                                        .flatMap(t -> Mono.just(readChatMapper.mapToDto(savedChat)));
                            });
                });
    }

    public Mono<ChatResponseDto> updateGroupChatName(Long chatId, ChatRequestDto chatRequestDto) {
        return chatRepository.findById(chatId)
                .switchIfEmpty(Mono.error(new ChatNotExistException("Chat with id + " + chatId + " doesn't exist!")))
                .flatMap(chatToUpdate -> {
                    if (chatToUpdate.getIsPrivate()) {
                        return Mono.error(new ResponseStatusException(BAD_REQUEST, "You can update names in group chats only"));
                    }
                    chatToUpdate.setName(chatRequestDto.getName());
                    return chatRepository.save(chatToUpdate);
                })
                .map(readChatMapper::mapToDto);

    }

    public Mono<ChatResponseDto> updateParticipants(Long chatId, ChatUsersRequestDto chatUsersRequestDto) {
        Mono<List<Long>> listMono = chatParticipantRepository.findAllByChatParticipantKeyChatId(chatId)
                .map(ChatParticipant::getUserId)
                .collectList();

        return chatRepository.findById(chatId)
                .switchIfEmpty(Mono.error(new ChatNotExistException("Chat with id " + chatId + " doesn't exist!")))
                .zipWith(listMono)
                .flatMap(tuple -> {
                    List<Long> usersIdsToAdd = chatUsersRequestDto.getUsersIds();
                    List<Long> userIds = tuple.getT2();
                    usersIdsToAdd.removeAll(userIds);
                    List<Long> updatedIds = usersIdsToAdd.stream().distinct().collect(Collectors.toList());
                    Chat chatToUpdate = tuple.getT1();

                    return Mono.from(saveChatParticipants(updatedIds, chatToUpdate.getId())
                            .zipWith(chatRepository.save(chatToUpdate))
                            .flatMap(tuple2 -> {
                                Chat chat = tuple2.getT2();
                                Flux<ChatParticipant> participantsFlux = chatParticipantRepository
                                        .findAllByChatParticipantKeyChatId(chat.getId());
                                return participantsFlux.collectList().map(participants -> {
                                    ChatResponseDto chatResponseDto = readChatMapper.mapToDto(chat);
                                    if (!participants.isEmpty()) {
                                        chatResponseDto.setUserIds(participants.stream()
                                                .map(ChatParticipant::getUserId)
                                                .collect(Collectors.toList()));
                                    }
                                    return chatResponseDto;
                                });
                            }));
                });
    }

    public Flux<ChatParticipant> saveChatParticipants(List<Long> userIds, Long chatId) {
        return Flux.fromIterable(userIds)
                .flatMap(userId -> chatParticipantRepository.saveChatParticipant(userId, chatId));
    }

    public Mono<Void> delete(Long chatId) {
        return chatRepository.findById(chatId)
                .switchIfEmpty(Mono.error(new ChatNotExistException("Chat with id + " + chatId + " doesn't exist!")))
                .flatMap(chatRepository::delete).then(chatParticipantRepository.deleteChatParticipants(chatId));
    }

    private static String getPrivateChatName(String currentUsername, String anotherUsername) {
        return "PrivateChat_" + (currentUsername.hashCode() * anotherUsername.hashCode());
    }
}

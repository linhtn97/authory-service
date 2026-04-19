package com.linhtn.authory_service.modules.user.service.impl;

import com.linhtn.authory_service.config.i18n.I18nHelper;
import com.linhtn.authory_service.modules.user.domain.dto.UserDto;
import com.linhtn.authory_service.modules.user.domain.entity.User;
import com.linhtn.authory_service.modules.user.domain.event.UserCreateEvent;
import com.linhtn.authory_service.modules.user.domain.event.UserDeleteEvent;
import com.linhtn.authory_service.modules.user.domain.event.UserUpdateEvent;
import com.linhtn.authory_service.modules.user.domain.mapper.UserMapper;
import com.linhtn.authory_service.modules.user.exception.error.UserError;
import com.linhtn.authory_service.modules.user.exception.unchecked.UserNotFoundException;
import com.linhtn.authory_service.modules.user.repository.UserRepository;
import com.linhtn.authory_service.modules.user.service.UserService;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;
import com.linhtn.shared_kernel.infrastructure.jpa.specification.GenericSpecification;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final I18nHelper i18nHelper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;

    public UserServiceImpl(I18nHelper i18nHelper, UserRepository userRepository, UserMapper userMapper, ApplicationEventPublisher eventPublisher) {
        this.i18nHelper = i18nHelper;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public PagingResponse<UserDto> getPage(PagingRequest request) {
        Specification<User> specification = new GenericSpecification<>(request);

        List<Sort.Order> orders = new ArrayList<>();
        request.getOrders().forEach(o -> {
            if (o.getSort().equals(FieldOrder.SortType.asc)) {
                orders.add(Sort.Order.asc(o.getField()));
            } else if (o.getSort().equals(FieldOrder.SortType.desc)) {
                orders.add(Sort.Order.desc(o.getField()));
            }
        });
        Sort sort = Sort.by(orders);
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize(), sort);

        var paging = userRepository.findAll(specification, pageable);

        return userMapper.toPage(paging);
    }

    @Override
    public UserDto get(UUID id) {
        return userMapper.toDto(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                UserError.USER_NOT_FOUND.name(),
                i18nHelper.getMessage(UserError.USER_NOT_FOUND.getMessage(), id)))
        );
    }

    @Override
    @Transactional
    public UserDto create(UserDto request) {
        var newUser = new User();
        userMapper.fillEntity(newUser, request);
        User savedUser = userRepository.save(newUser);
        eventPublisher.publishEvent(new UserCreateEvent(savedUser));
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto update(UUID id, UserDto request) {
        User existUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                UserError.USER_NOT_FOUND.name(),
                i18nHelper.getMessage(UserError.USER_NOT_FOUND.getMessage()))
        );
        userMapper.fillEntity(existUser, request);
        User savedUser = userRepository.save(existUser);
        eventPublisher.publishEvent(new UserUpdateEvent(savedUser));
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        User existUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                UserError.USER_NOT_FOUND.name(),
                i18nHelper.getMessage(UserError.USER_NOT_FOUND.getMessage()))
        );
        userRepository.delete(existUser);
        eventPublisher.publishEvent(new UserDeleteEvent(existUser));
    }

    @Override
    public UserDto getByUsername(String username) {
        User existUser = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(
                UserError.USER_NOT_FOUND.name(),
                i18nHelper.getMessage(UserError.USER_NOT_FOUND.getMessage(),  username))
        );
        return userMapper.toDto(existUser);
    }

    @Override
    public UserDto getByEmail(String email) {
        User existUser = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(
                UserError.USER_NOT_FOUND.name(),
                i18nHelper.getMessage(UserError.USER_NOT_FOUND.getMessage()))
        );
        return userMapper.toDto(existUser);
    }

    @Override
    public UserDto getByUsernameOrEmail(String um) {
        User existUser = userRepository.findByUsernameOrEmail(um, um).orElseThrow(() -> new UserNotFoundException(
                UserError.USER_NOT_FOUND.name(),
                i18nHelper.getMessage(UserError.USER_NOT_FOUND.getMessage()))
        );
        return userMapper.toDto(existUser);
    }

    @Override
    public boolean existsByUsernameOrEmail(String username, String email) {
        return userRepository.existsByUsernameOrEmail(username, email);
    }
}


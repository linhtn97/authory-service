package com.linhtn.authory_service.modules.user.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.user.domain.model.User;
import com.linhtn.authory_service.modules.user.domain.repository.UserRepository;
import com.linhtn.authory_service.modules.user.infrastructure.persistence.jpa.entity.UserEntity;
import com.linhtn.authory_service.modules.user.infrastructure.persistence.jpa.repository.UserJpaRepository;
import com.linhtn.authory_service.modules.user.infrastructure.persistence.mapper.UserEntityMapper;
import com.linhtn.data_jpa.specification.GenericSpecification;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public PagingResponse<User> getPage(PagingRequest pagingRequest) {
        Specification<UserEntity> specification = new GenericSpecification<>(pagingRequest);

        List<Sort.Order> orders = new ArrayList<>();
        if (pagingRequest.getOrders() != null) {
            pagingRequest
                    .getOrders()
                    .forEach(
                            o -> {
                                if (o.getSort() == FieldOrder.SortType.asc) {
                                    orders.add(Sort.Order.asc(o.getField()));
                                } else if (o.getSort() == FieldOrder.SortType.desc) {
                                    orders.add(Sort.Order.desc(o.getField()));
                                }
                            });
        }
        Sort sort = orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
        Pageable pageable =
                PageRequest.of(pagingRequest.getPage(), pagingRequest.getPageSize(), sort);

        Page<UserEntity> entityPage = userJpaRepository.findAll(specification, pageable);
        List<User> items =
                entityPage.getContent().stream().map(UserEntityMapper::toDomain).toList();

        return PagingResponse.of(
                items,
                pagingRequest.getPage(),
                pagingRequest.getPageSize(),
                entityPage.getTotalElements());
    }

    @Override
    public Optional<User> get(UUID id) {
        return userJpaRepository.findById(id).map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return userJpaRepository
                .findByUsernameOrEmail(username, email)
                .map(UserEntityMapper::toDomain);
    }

    @Override
    public boolean existsByUsernameOrEmail(String username, String email) {
        return userJpaRepository.existsByUsernameOrEmail(username, email);
    }

    @Override
    public User create(User user) {
        UserEntity entity = UserEntityMapper.toEntity(user);
        UserEntity saved = userJpaRepository.save(entity);
        return UserEntityMapper.toDomain(saved);
    }

    @Override
    public User update(User user) {
        UserEntity entity = UserEntityMapper.toEntity(user);
        UserEntity saved = userJpaRepository.save(entity);
        return UserEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    public long count() {
        return userJpaRepository.count();
    }

    @Override
    public long countNewUsersToday() {
        return userJpaRepository.countNewUsersToday();
    }
}

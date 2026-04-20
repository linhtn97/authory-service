package com.linhtn.authory_service.modules.user.domain.mapper;

import org.mapstruct.Mapper;

import com.linhtn.authory_service.config.auditor.AuditMapperConfig;
import com.linhtn.authory_service.modules.user.domain.dto.CustomerDto;
import com.linhtn.authory_service.modules.user.domain.entity.Customer;
import com.linhtn.shared_kernel.domain.mapper.BaseMapper;

@Mapper(componentModel = "spring", config = AuditMapperConfig.class)
public abstract class CustomerMapper implements BaseMapper<Customer, CustomerDto> {}

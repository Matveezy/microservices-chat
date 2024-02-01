package com.lab2.user.mapper;

public interface Mapper<D, E> {

    D mapToDto(E entity);

    default E mapToEntity(D dto) {
        return null;
    }

}

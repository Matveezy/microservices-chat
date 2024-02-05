package com.lab2.message.mapper;

public interface Mapper<D, E> {

    D mapToDto(E entity);

    default E mapToEntity(D dto) {
        return null;
    }

}

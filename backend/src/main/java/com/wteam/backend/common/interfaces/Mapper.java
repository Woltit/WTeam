package com.wteam.backend.common.interfaces;

public interface Mapper<Request, Response, Entity> {
    Response toResponse(Entity entity);
    Entity toEntity(Request dto);
}

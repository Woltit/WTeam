package com.wteam.backend.common.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

/**
 * Базова конфігурація для всіх MapStruct маперів у проєкті.
 * <p>
 * componentModel = "spring" - автоматично робить згенеровані мапери Spring бінами (@Component).
 * unmappedTargetPolicy = ReportingPolicy.IGNORE - ігнорує попередження про незамаплені поля в цільовому об'єкті.
 * </p>
 */
@MapperConfig(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MapStructConfig {
}

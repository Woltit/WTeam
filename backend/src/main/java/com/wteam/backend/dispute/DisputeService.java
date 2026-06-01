package com.wteam.backend.dispute;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервіс для керування суперечками (диспутами).
 * <p>
 * Обробляє бізнес-логіку, пов'язану зі створенням, розглядом та вирішенням суперечок між користувачами.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class DisputeService {
    private final DisputeRepository disputeRepository;
}

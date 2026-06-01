package com.wteam.backend.dispute;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для роботи з сутностями {@link Dispute}.
 * <p>
 * Забезпечує доступ до даних суперечок (диспутів) у базі даних.
 * </p>
 */
@Repository
public interface DisputeRepository extends JpaRepository<Dispute, Long> {
}

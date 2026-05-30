package com.wteam.backend.transaction;

import com.wteam.backend.booking.Booking;
import com.wteam.backend.common.entity.BaseEntityPart;
import com.wteam.backend.common.enums.TransactionStatus;
import com.wteam.backend.common.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

/**
 * Сутність, що представляє фінансову транзакцію, пов'язану з бронюванням.
 * Зберігає суму, тип та статус платежу.
 *
 * @see Booking
 * @see com.wteam.backend.common.enums.TransactionType
 * @see com.wteam.backend.common.enums.TransactionStatus
 */
@Entity
@Table(name = "transactions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Transaction extends BaseEntityPart {
    /**
     * Унікальний ідентифікатор транзакції.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transactions_gen")
    @SequenceGenerator(name = "transactions_gen", sequenceName = "transactions_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /**
     * Бронювання, за яким проводиться транзакція.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", referencedColumnName = "id", nullable = false)
    private Booking booking;

    /**
     * Сума транзакції.
     */
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    /**
     * Тип транзакції (платіж, застава, повернення тощо).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TransactionType type;

    /**
     * Ідентифікатор транзакції у зовнішній платіжній системі.
     */
    @Column(name = "external_tx_id")
    private String externalTxId;

    /**
     * Поточний статус транзакції.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TransactionStatus status;
}

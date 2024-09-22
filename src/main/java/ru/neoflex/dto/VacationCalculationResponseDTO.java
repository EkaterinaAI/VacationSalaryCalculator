package ru.neoflex.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Универсальный DTO для передачи информации о расчёте отпускных и статуса запроса.
 * Включает поля для статуса, сообщения, данных о расчёте, а также времени запроса.
 *
 * @author Ищук Екатерина
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Исключение полей с null
public class VacationCalculationResponseDTO {

    // Поля для общего респонса
    private String status; // "success" или "error"
    private String message; // Сообщение об успехе или ошибке
    private LocalDateTime timestamp; // Время запроса

    // Поля для успешного расчета отпускных
    private Integer workingDays; // Количество рабочих дней
    private BigDecimal vacationPay; // Сумма отпускных, округленная до 2 знаков
    private LocalDate startDate;
    private LocalDate endDate;
}

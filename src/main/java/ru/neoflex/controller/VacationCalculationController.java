package ru.neoflex.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.dto.VacationCalculationResponseDTO;
import ru.neoflex.service.VacationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Контроллер для расчёта отпускных.
 * Обрабатывает запросы для расчета отпускных с учетом праздничных и выходных дней.
 *
 * @author Ищук Екатерина
 */
@RestController
@RequestMapping("/calculate")
@RequiredArgsConstructor
public class VacationCalculationController {

    private final VacationService vacationService;

    /**
     * Рассчитывает отпускные с учётом количества дней отпуска и дат.
     *
     * @param averageSalary Средняя зарплата пользователя
     * @param startDate     Дата начала отпуска (формат: YYYY-MM-DD)
     * @param endDate       Дата окончания отпуска (формат: YYYY-MM-DD)
     * @return Респонс с информацией о статусе и расчёте
     */
    @GetMapping
    public VacationCalculationResponseDTO calculateVacationPayWithDates(
            @RequestParam BigDecimal averageSalary,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Подсчёт количества рабочих дней
        int workingDays = vacationService.countWorkingDays(startDate, endDate);

        // Рассчёт отпускных
        BigDecimal vacationPay = vacationService.calculateVacationPay(averageSalary, startDate, endDate);

        // Округляем отпускные до двух знаков после запятой
        vacationPay = vacationPay.setScale(2, RoundingMode.HALF_UP);

        // Возвращаем успешный ответ через Builder
        return VacationCalculationResponseDTO.builder()
                .status("success")
                .timestamp(LocalDateTime.now())
                .workingDays(workingDays)
                .vacationPay(vacationPay)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}

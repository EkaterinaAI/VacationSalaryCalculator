package ru.neoflex;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.neoflex.controller.VacationCalculationController;
import ru.neoflex.dto.VacationCalculationResponseDTO;
import ru.neoflex.service.VacationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class VacationCalculationTest {

    @Autowired
    private VacationCalculationController vacationController;

    @Autowired
    private VacationService vacationService;

    @Test
    public void testCalculateVacationPay() {
        // Тестовый случай: отпуск с 4 рабочими днями в рамках одного месяца
        VacationCalculationResponseDTO response = vacationController.calculateVacationPayWithDates(
                BigDecimal.valueOf(60000.0), LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 16)
        );

        assertEquals(BigDecimal.valueOf(16000.00).setScale(2, RoundingMode.HALF_UP), response.getVacationPay(),
                "Сумма отпускных неверная");
    }

    @Test
    public void testCalculateWorkingDays() {
        int workingDays = vacationService.countWorkingDays(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 16));
        assertEquals(4, workingDays, "Количество рабочих дней неверное");
    }

}

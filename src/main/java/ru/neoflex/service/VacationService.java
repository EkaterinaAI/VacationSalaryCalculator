package ru.neoflex.service;

import de.jollyday.Holiday;
import de.jollyday.HolidayManager;
import de.jollyday.ManagerParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Сервис для проверки рабочих дней с учётом праздничных дней и расчёта отпускных.
 *
 * @author Ищук Екатерина
 */
@Service
@Slf4j
public class VacationService {

    private static final HolidayManager holidayManager = HolidayManager.getInstance(ManagerParameters.create("ru"));

    /**
     * Метод для проверки, является ли день рабочим (не выходной и не праздничный).
     *
     * @param date Дата для проверки
     * @return true, если день рабочий, иначе false
     */
    public boolean isWorkingDay(LocalDate date) {
        // Проверка на выходной день (суббота или воскресенье)
        if (date.getDayOfWeek().getValue() >= 6) {
            return false;
        }

        // Получаем праздники для года даты
        Set<Holiday> holidays = holidayManager.getHolidays(date.getYear(), "ru");

        // Проверка, является ли дата праздничным днём
        boolean isWorkingDay = holidays.stream().noneMatch(holiday -> holiday.getDate().equals(date));
        log.debug(String.format("%s is %s", date, isWorkingDay));

        return isWorkingDay;
    }

    /**
     * Считает количество рабочих дней в заданном периоде.
     *
     * @param startDate Дата начала периода
     * @param endDate   Дата окончания периода
     * @return Количество рабочих дней
     */
    public int countWorkingDays(LocalDate startDate, LocalDate endDate) {
        int workingDays = 0;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            if (isWorkingDay(currentDate)) {
                workingDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

        return workingDays;
    }

    /**
     * Считает количество рабочих дней в месяце.
     *
     * @param yearMonth Год и месяц в формате YearMonth
     * @return Количество рабочих дней в месяце
     */
    public int countWorkingDaysInMonth(YearMonth yearMonth) {
        int workingDays = 0;
        LocalDate date = yearMonth.atDay(1);
        while (date.getMonth() == yearMonth.getMonth()) {
            if (isWorkingDay(date)) {
                workingDays++;
            }
            date = date.plusDays(1);
        }
        return workingDays;
    }

    /**
     * Рассчитывает сумму отпускных на основе средней зарплаты и периода отпуска.
     * Учитывает разбивку отпуска по месяцам и количество рабочих дней в каждом месяце.
     *
     * @param averageSalary Средняя зарплата сотрудника
     * @param startDate     Дата начала отпуска
     * @param endDate       Дата окончания отпуска
     * @return Сумма отпускных
     */
    public BigDecimal calculateVacationPay(BigDecimal averageSalary, LocalDate startDate, LocalDate endDate) {
        Map<YearMonth, Integer> vacationWorkingDaysPerMonth = new HashMap<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            if (isWorkingDay(currentDate)) {
                YearMonth currentMonth = YearMonth.from(currentDate);
                vacationWorkingDaysPerMonth.put(
                        currentMonth,
                        vacationWorkingDaysPerMonth.getOrDefault(currentMonth, 0) + 1
                );
            }
            currentDate = currentDate.plusDays(1);
        }

        BigDecimal totalVacationPay = BigDecimal.ZERO;

        for (Map.Entry<YearMonth, Integer> entry : vacationWorkingDaysPerMonth.entrySet()) {
            YearMonth ym = entry.getKey();
            int vacationWorkingDays = entry.getValue();
            int totalWorkingDaysInMonth = countWorkingDaysInMonth(ym);
            BigDecimal dailySalary = averageSalary.divide(BigDecimal.valueOf(totalWorkingDaysInMonth), 2, RoundingMode.HALF_UP);
            BigDecimal monthlyPay = dailySalary.multiply(BigDecimal.valueOf(vacationWorkingDays));
            totalVacationPay = totalVacationPay.add(monthlyPay);
        }

        return totalVacationPay;
    }
}

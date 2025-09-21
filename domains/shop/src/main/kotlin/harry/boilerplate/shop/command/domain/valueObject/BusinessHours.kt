package harry.boilerplate.shop.command.domain.valueObject

import harry.boilerplate.common.domain.entity.ValueObject
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.DayOfWeek
import java.time.LocalTime

@Embeddable
class BusinessHours private constructor(
    @Column(name = "monday_open")
    private var mondayOpen: LocalTime? = null,
    @Column(name = "monday_close")
    private var mondayClose: LocalTime? = null,
    @Column(name = "tuesday_open")
    private var tuesdayOpen: LocalTime? = null,
    @Column(name = "tuesday_close")
    private var tuesdayClose: LocalTime? = null,
    @Column(name = "wednesday_open")
    private var wednesdayOpen: LocalTime? = null,
    @Column(name = "wednesday_close")
    private var wednesdayClose: LocalTime? = null,
    @Column(name = "thursday_open")
    private var thursdayOpen: LocalTime? = null,
    @Column(name = "thursday_close")
    private var thursdayClose: LocalTime? = null,
    @Column(name = "friday_open")
    private var fridayOpen: LocalTime? = null,
    @Column(name = "friday_close")
    private var fridayClose: LocalTime? = null,
    @Column(name = "saturday_open")
    private var saturdayOpen: LocalTime? = null,
    @Column(name = "saturday_close")
    private var saturdayClose: LocalTime? = null,
    @Column(name = "sunday_open")
    private var sundayOpen: LocalTime? = null,
    @Column(name = "sunday_close")
    private var sundayClose: LocalTime? = null
) : ValueObject() {

    override val equalityComponents: Array<out Any?> = arrayOf(
        mondayOpen, mondayClose,
        tuesdayOpen, tuesdayClose,
        wednesdayOpen, wednesdayClose,
        thursdayOpen, thursdayClose,
        fridayOpen, fridayClose,
        saturdayOpen, saturdayClose,
        sundayOpen, sundayClose
    )

    fun isOpenOn(dayOfWeek: DayOfWeek): Boolean {
        val (open, close) = getHoursForDay(dayOfWeek)
        return open != null && close != null
    }

    fun isOpenAt(dayOfWeek: DayOfWeek, time: LocalTime): Boolean {
        if (!isOpenOn(dayOfWeek)) return false
        val (open, close) = getHoursForDay(dayOfWeek)
        return open != null && close != null && !time.isBefore(open) && time.isBefore(close)
    }

    fun toWeeklyHours(): Map<DayOfWeek, Pair<LocalTime?, LocalTime?>> = mapOf(
        DayOfWeek.MONDAY to Pair(mondayOpen, mondayClose),
        DayOfWeek.TUESDAY to Pair(tuesdayOpen, tuesdayClose),
        DayOfWeek.WEDNESDAY to Pair(wednesdayOpen, wednesdayClose),
        DayOfWeek.THURSDAY to Pair(thursdayOpen, thursdayClose),
        DayOfWeek.FRIDAY to Pair(fridayOpen, fridayClose),
        DayOfWeek.SATURDAY to Pair(saturdayOpen, saturdayClose),
        DayOfWeek.SUNDAY to Pair(sundayOpen, sundayClose)
    )

    private fun getHoursForDay(dayOfWeek: DayOfWeek): Pair<LocalTime?, LocalTime?> = when (dayOfWeek) {
        DayOfWeek.MONDAY -> Pair(mondayOpen, mondayClose)
        DayOfWeek.TUESDAY -> Pair(tuesdayOpen, tuesdayClose)
        DayOfWeek.WEDNESDAY -> Pair(wednesdayOpen, wednesdayClose)
        DayOfWeek.THURSDAY -> Pair(thursdayOpen, thursdayClose)
        DayOfWeek.FRIDAY -> Pair(fridayOpen, fridayClose)
        DayOfWeek.SATURDAY -> Pair(saturdayOpen, saturdayClose)
        DayOfWeek.SUNDAY -> Pair(sundayOpen, sundayClose)
    }

    companion object {
        fun from(weeklyHours: Map<DayOfWeek, Pair<LocalTime?, LocalTime?>>): BusinessHours {
            require(weeklyHours.isNotEmpty()) { "영업시간 정보는 필수입니다" }
            val hours = BusinessHours()
            weeklyHours.forEach { (day, timePair) ->
                when (day) {
                    DayOfWeek.MONDAY -> {
                        hours.mondayOpen = timePair.first
                        hours.mondayClose = timePair.second
                    }
                    DayOfWeek.TUESDAY -> {
                        hours.tuesdayOpen = timePair.first
                        hours.tuesdayClose = timePair.second
                    }
                    DayOfWeek.WEDNESDAY -> {
                        hours.wednesdayOpen = timePair.first
                        hours.wednesdayClose = timePair.second
                    }
                    DayOfWeek.THURSDAY -> {
                        hours.thursdayOpen = timePair.first
                        hours.thursdayClose = timePair.second
                    }
                    DayOfWeek.FRIDAY -> {
                        hours.fridayOpen = timePair.first
                        hours.fridayClose = timePair.second
                    }
                    DayOfWeek.SATURDAY -> {
                        hours.saturdayOpen = timePair.first
                        hours.saturdayClose = timePair.second
                    }
                    DayOfWeek.SUNDAY -> {
                        hours.sundayOpen = timePair.first
                        hours.sundayClose = timePair.second
                    }
                }
            }
            return hours
        }
    }

    @Suppress("unused")
    private constructor() : this(
        mondayOpen = null,
        mondayClose = null,
        tuesdayOpen = null,
        tuesdayClose = null,
        wednesdayOpen = null,
        wednesdayClose = null,
        thursdayOpen = null,
        thursdayClose = null,
        fridayOpen = null,
        fridayClose = null,
        saturdayOpen = null,
        saturdayClose = null,
        sundayOpen = null,
        sundayClose = null
    )
}

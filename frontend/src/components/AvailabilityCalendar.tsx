import { useState } from 'react';
import type { UnavailableDateRange } from '../api/bookings';
import { useLanguage } from '../contexts/LanguageContext';

interface Props {
    unavailableDates: UnavailableDateRange[];
    onRangeSelect: (start: string, end: string) => void;
    pricePerDay: number;
    depositAmount: number;
}

const toStr = (d: Date): string => {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
};

const DAY_NAMES: Record<'ua' | 'en', string[]> = {
    ua: ['Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб', 'Нд'],
    en: ['Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa', 'Su'],
};

const pluralDays = (n: number, lang: 'ua' | 'en') => {
    if (lang === 'en') {
        return n === 1 ? 'day' : 'days';
    }
    return n === 1 ? 'день' : n < 5 ? 'дні' : 'днів';
};

const AvailabilityCalendar = ({ unavailableDates, onRangeSelect, pricePerDay, depositAmount }: Props) => {
    const { language, t } = useLanguage();
    const today = toStr(new Date());
    const [viewDate, setViewDate] = useState(() => new Date(new Date().getFullYear(), new Date().getMonth(), 1));
    const [step, setStep] = useState<'start' | 'end'>('start');
    const [start, setStart] = useState<string | null>(null);
    const [end, setEnd] = useState<string | null>(null);
    const [hover, setHover] = useState<string | null>(null);

    const isUnavail = (date: string) =>
        unavailableDates.some(r => date >= r.startDate && date <= r.endDate);

    const rangeHasUnavail = (s: string, e: string) =>
        unavailableDates.some(r => r.startDate <= e && r.endDate >= s);

    const handleClick = (date: string) => {
        if (date < today || isUnavail(date)) return;

        if (step === 'start' || !start) {
            setStart(date);
            setEnd(null);
            setStep('end');
            onRangeSelect(date, '');
            return;
        }

        if (date < start) {
            setStart(date);
            setEnd(null);
            onRangeSelect(date, '');
            return;
        }

        if (rangeHasUnavail(start, date)) {
            setStart(date);
            setEnd(null);
            onRangeSelect(date, '');
            return;
        }

        setEnd(date);
        setStep('start');
        onRangeSelect(start, date);
    };

    const previewEnd =
        step === 'end' && start && hover && hover >= start && !rangeHasUnavail(start, hover)
            ? hover
            : null;

    const year = viewDate.getFullYear();
    const month = viewDate.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);

    let offset = firstDay.getDay() - 1;
    if (offset < 0) offset = 6;

    const cells: (string | null)[] = Array(offset).fill(null);
    for (let d = 1; d <= lastDay.getDate(); d++) {
        cells.push(toStr(new Date(year, month, d)));
    }
    while (cells.length % 7 !== 0) cells.push(null);

    const monthLabel = viewDate.toLocaleString(language === 'ua' ? 'uk-UA' : 'en-US', { month: 'long', year: 'numeric' });
    const activeEnd = end ?? previewEnd;
    const dayCount = start && activeEnd
        ? Math.ceil((new Date(activeEnd).getTime() - new Date(start).getTime()) / 86400000) + 1
        : 0;

    const dayNames = DAY_NAMES[language] || DAY_NAMES.ua;

    return (
        <div className="cal">
            <div className="cal-header">
                <button className="cal-nav" type="button" onClick={() => setViewDate(new Date(year, month - 1, 1))}>‹</button>
                <span className="cal-month">{monthLabel}</span>
                <button className="cal-nav" type="button" onClick={() => setViewDate(new Date(year, month + 1, 1))}>›</button>
            </div>

            <div className="cal-grid">
                {dayNames.map(d => <div key={d} className="cal-weekday">{d}</div>)}
                {cells.map((date, i) => {
                    if (!date) return <div key={`e${i}`} className="cal-day cal-day-empty" />;

                    const unavail = isUnavail(date);
                    const past = date < today;
                    const isStart = date === start;
                    const isEnd = date === end || date === previewEnd;
                    const inRange = !!(start && activeEnd && date > start && date < activeEnd);
                    const isToday = date === today;

                    let cls = 'cal-day';
                    if (past) cls += ' cal-day-past';
                    else if (unavail) cls += ' cal-day-unavail';
                    else if (isStart) cls += ' cal-day-selected';
                    else if (isEnd) cls += ' cal-day-selected';
                    else if (inRange) cls += ' cal-day-range';
                    if (isToday && !past) cls += ' cal-day-today';

                    return (
                        <div
                            key={date}
                            className={cls}
                            onClick={() => handleClick(date)}
                            onMouseEnter={() => setHover(date)}
                            onMouseLeave={() => setHover(null)}
                        >
                            {parseInt(date.split('-')[2])}
                        </div>
                    );
                })}
            </div>

            <div className="cal-legend">
                <span className="cal-legend-item"><span className="cal-swatch cal-swatch-avail" />{t('cal.available')}</span>
                <span className="cal-legend-item"><span className="cal-swatch cal-swatch-unavail" />{t('cal.booked')}</span>
                <span className="cal-legend-item"><span className="cal-swatch cal-swatch-sel" />{t('cal.selected')}</span>
            </div>



            {start && end && dayCount > 0 && (
                <div className="booking-price-preview">
                    {dayCount} {pluralDays(dayCount, language)} · ₴{dayCount * pricePerDay} + {t('cal.depositLabel')} ₴{depositAmount}
                </div>
            )}
        </div>
    );
};

export default AvailabilityCalendar;

import React from 'react';
import { useLanguage } from '../contexts/LanguageContext';

const DocsPage = () => {
    const { language } = useLanguage();

    const uaContent = (
        <div className="docs-content">
            <h2>Як користуватися RentGo?</h2>
            <p>RentGo — це платформа для оренди речей. Ви можете як здавати свої речі в оренду, так і орендувати речі інших користувачів.</p>

            <h3>Для орендарів (як орендувати річ)</h3>
            <ol>
                <li><strong>Пошук:</strong> Перейдіть до "Каталогу", щоб знайти потрібну річ. Ви можете фільтрувати за категорією або скористатися AI-помічником для пошуку.</li>
                <li><strong>Бронювання:</strong> Відкрийте сторінку речі, оберіть вільні дати на календарі та натисніть "Забронювати".</li>
                <li><strong>Очікування підтвердження:</strong> Власник речі отримає сповіщення. Ви зможете слідкувати за статусом бронювання у вкладці "Мої бронювання".</li>
                <li><strong>Оплата:</strong> Коли власник схвалить бронювання, статус зміниться на "APPROVED". У вас з'явиться кнопка "Оплатити (LiqPay)". Здійсніть оплату онлайн.</li>
                <li><strong>Користування та завершення:</strong> Після оплати та настання дати оренди ви можете користуватися річчю. По завершенню терміну власник натискає "Завершити", і ви зможете залишити відгук!</li>
            </ol>

            <h3>Для орендодавців (як здавати річ)</h3>
            <ol>
                <li><strong>Створення оголошення:</strong> Натисніть "Додати оголошення", заповніть деталі, ціни та завантажте фото. Зверніть увагу: ви можете вибрати головне фото за допомогою іконки "★".</li>
                <li><strong>Управління запитами:</strong> Коли хтось захоче орендувати вашу річ, ви побачите нове бронювання у вкладці "Мої бронювання" -> "Мої пропозиції". Вам потрібно його "Схвалити" або "Відхилити".</li>
                <li><strong>Очікування оплати:</strong> Після вашого схвалення орендар повинен оплатити оренду. Коли оплата пройде, статус зміниться на "PAID".</li>
                <li><strong>Передача та повернення:</strong> Передайте річ орендарю. Після успішного повернення натисніть кнопку "Завершити", щоб закрити угоду та залишити відгук про орендаря.</li>
            </ol>

            <h3>Чати та комунікація</h3>
            <p>Ви завжди можете зв'язатися з іншою стороною через внутрішні чати. На сторінці товару є кнопка "Написати власнику", а всі ваші розмови зберігаються у вкладці "Чати".</p>
        </div>
    );

    const enContent = (
        <div className="docs-content">
            <h2>How to use RentGo?</h2>
            <p>RentGo is an item rental platform. You can rent out your items or rent items from other users.</p>

            <h3>For Renters (How to rent an item)</h3>
            <ol>
                <li><strong>Search:</strong> Go to the "Catalog" to find the item you need. You can filter by category or use the AI helper.</li>
                <li><strong>Booking:</strong> Open the item page, select available dates on the calendar, and click "Book".</li>
                <li><strong>Wait for approval:</strong> The owner will receive a notification. You can track the booking status in the "My Bookings" tab.</li>
                <li><strong>Payment:</strong> Once the owner approves the booking, its status changes to "APPROVED". A "Pay (LiqPay)" button will appear. Make the payment online.</li>
                <li><strong>Usage and completion:</strong> After payment and when the rental period starts, you can use the item. At the end of the period, the owner clicks "Complete", and you can leave a review!</li>
            </ol>

            <h3>For Owners (How to rent out an item)</h3>
            <ol>
                <li><strong>Create a listing:</strong> Click "Add Listing", fill in details, prices, and upload photos. Note: you can select the main photo using the "★" icon.</li>
                <li><strong>Manage requests:</strong> When someone wants to rent your item, you will see a new booking in the "My Bookings" -> "My Offers" tab. You need to "Approve" or "Reject" it.</li>
                <li><strong>Wait for payment:</strong> After your approval, the renter must pay for the rental. Once the payment is successful, the status changes to "PAID".</li>
                <li><strong>Handover and return:</strong> Hand over the item to the renter. After a successful return, click the "Complete" button to close the deal and leave a review for the renter.</li>
            </ol>

            <h3>Chats and Communication</h3>
            <p>You can always contact the other party through internal chats. There is a "Message Owner" button on the item page, and all your conversations are saved in the "Chats" tab.</p>
        </div>
    );

    return (
        <div className="page">
            <div className="page-header">
                <h1 className="page-title">{language === 'ua' ? 'Документація' : 'Documentation'}</h1>
                <p className="page-subtitle">
                    {language === 'ua' ? 'Посібник користувача платформи RentGo' : 'RentGo platform user guide'}
                </p>
            </div>
            <div className="form-card" style={{ maxWidth: '800px', margin: '0 auto', padding: '2rem' }}>
                <style>
                    {`
                    .docs-content h2 { margin-top: 0; margin-bottom: 1rem; font-size: 1.5rem; color: var(--text); }
                    .docs-content h3 { margin-top: 2rem; margin-bottom: 1rem; font-size: 1.25rem; color: var(--text); }
                    .docs-content p { margin-bottom: 1rem; color: var(--text); line-height: 1.6; }
                    .docs-content ol { padding-left: 1.5rem; margin-bottom: 1.5rem; }
                    .docs-content li { margin-bottom: 0.75rem; color: var(--text); line-height: 1.6; }
                    `}
                </style>
                {language === 'ua' ? uaContent : enContent}
            </div>
        </div>
    );
};

export default DocsPage;

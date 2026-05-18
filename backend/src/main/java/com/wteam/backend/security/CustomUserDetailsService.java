package com.wteam.backend.security;

import com.wteam.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Кастомна реалізація інтерфейсу {@link UserDetailsService} для Spring Security.
 * <p>
 * Цей сервіс використовується підсистемою автентифікації Spring Security для пошуку
 * та завантаження облікових даних користувача під час авторизації (наприклад, при логіні або валідації JWT).
 * </p>
 *
 * @see UserDetailsService
 * @see SecurityUser
 * @see UserRepository
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Завантажує дані користувача за його унікальним ідентифікатором (у цій системі — email).
     * <p>
     * Метод виконує пошук у базі даних через {@link UserRepository}. Якщо користувача знайдено,
     * його JPA-сутність автоматично огортається в адаптер {@link SecurityUser}, який реалізує
     * необхідний для Spring Security інтерфейс {@link UserDetails}.
     * </p>
     *
     * @param email електронна пошта користувача, яка виступає в ролі унікального логіна (username).
     * @return об'єкт {@link UserDetails}, що містить дані користувача та його права доступу.
     * @throws UsernameNotFoundException якщо користувача з вказаним email не знайдено в базі даних.
     */
    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

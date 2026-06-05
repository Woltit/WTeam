import { useTheme } from '../contexts/ThemeContext';

const ThemeToggle = () => {
    const { theme, toggleTheme } = useTheme();

    return (
        <button
            type="button"
            className="theme-toggle"
            onClick={toggleTheme}
            title={theme === 'light' ? 'Увімкнути темну тему' : 'Увімкнути світлу тему'}
            aria-label={theme === 'light' ? 'Увімкнути темну тему' : 'Увімкнути світлу тему'}
        >
            {theme === 'light' ? '🌙' : '☀️'}
        </button>
    );
};

export default ThemeToggle;

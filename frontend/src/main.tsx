import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import App from './App.tsx';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import { ThemeProvider } from './contexts/ThemeContext';
import { LanguageProvider } from './contexts/LanguageContext';

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <ThemeProvider>
            <LanguageProvider>
                <BrowserRouter>
                    <AuthProvider>
                        <App />
                    </AuthProvider>
                </BrowserRouter>
            </LanguageProvider>
        </ThemeProvider>
    </StrictMode>,
);


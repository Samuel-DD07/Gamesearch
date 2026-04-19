import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter, useNavigate } from 'react-router-dom';
import LoginPage from './LoginPage';
import { authService } from '../services/api';

// Mock react-router-dom to track navigation
jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: jest.fn(),
}));

// Mock API
jest.mock('../services/api', () => ({
    authService: {
        login: jest.fn(),
    }
}));

describe('LoginPage', () => {
    const mockNavigate = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
        useNavigate.mockReturnValue(mockNavigate);
        // Reset localStorage
        Storage.prototype.setItem = jest.fn();
    });

    it('renders the login form', () => {
        render(
            <MemoryRouter>
                <LoginPage />
            </MemoryRouter>
        );

        expect(screen.getByText('Administration')).toBeInTheDocument();
        expect(screen.getByPlaceholderText('Ex: admin')).toBeInTheDocument();
        expect(screen.getByPlaceholderText('••••••••')).toBeInTheDocument();
    });

    it('handles successful login and redirects to /admin', async () => {
        authService.login.mockResolvedValue({ data: { token: 'fake-jwt-token' } });

        render(
            <MemoryRouter>
                <LoginPage />
            </MemoryRouter>
        );

        fireEvent.change(screen.getByPlaceholderText('Ex: admin'), { target: { value: 'admin' } });
        fireEvent.change(screen.getByPlaceholderText('••••••••'), { target: { value: 'password123' } });
        
        fireEvent.click(screen.getByRole('button', { name: /Se connecter/i }));

        await waitFor(() => {
            expect(authService.login).toHaveBeenCalledWith('admin', 'password123');
            expect(localStorage.setItem).toHaveBeenCalledWith('token', 'fake-jwt-token');
            expect(mockNavigate).toHaveBeenCalledWith('/admin');
        });
    });

    it('displays an error message upon failed login', async () => {
        authService.login.mockRejectedValue({ 
            response: { 
                data: { message: 'Invalid credentials' } 
            } 
        });

        render(
            <MemoryRouter>
                <LoginPage />
            </MemoryRouter>
        );

        fireEvent.change(screen.getByPlaceholderText('Ex: admin'), { target: { value: 'wrong' } });
        fireEvent.change(screen.getByPlaceholderText('••••••••'), { target: { value: 'wrong' } });
        
        fireEvent.click(screen.getByRole('button', { name: /Se connecter/i }));

        const errorMessage = await screen.findByText('Invalid credentials');
        expect(errorMessage).toBeInTheDocument();
        expect(mockNavigate).not.toHaveBeenCalled();
    });
});

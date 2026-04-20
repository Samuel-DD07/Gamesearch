import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import HomePage from './HomePage';
import { gameService } from '../services/api';

// Mock API calls
jest.mock('../services/api', () => ({
    gameService: {
        getRecentGames: jest.fn(),
        getPopularGames: jest.fn(),
        getGames: jest.fn(),
    }
}));

const mockGamesList = [
    { id: '1', title: 'Game 1', rating: 9.0 },
    { id: '2', title: 'Game 2', rating: 8.5 }
];

const future = { v7_startTransition: true, v7_relativeSplatPath: true };

describe('HomePage', () => {
    beforeEach(() => {
        // Reset mocks before each test
        jest.clearAllMocks();

        gameService.getRecentGames.mockResolvedValue({ data: mockGamesList });
        gameService.getPopularGames.mockResolvedValue({ data: mockGamesList });
        gameService.getGames.mockResolvedValue({ 
            data: { 
                content: mockGamesList, 
                totalPages: 1 
            } 
        });
    });

    it('renders the main title and fetches games on mount', async () => {
        render(
            <MemoryRouter future={future}>
                <HomePage />
            </MemoryRouter>
        );

        // Check if main title renders
        expect(screen.getByText('Discover Your Next Game')).toBeInTheDocument();

        // Wait for APIs to be called
        await waitFor(() => {
            expect(gameService.getRecentGames).toHaveBeenCalledTimes(1);
            expect(gameService.getPopularGames).toHaveBeenCalledTimes(1);
            expect(gameService.getGames).toHaveBeenCalledTimes(1);
        });

        // The games should be rendered multiple times (recent, popular, full catalog)
        // We'll just check if Game 1 is in the document
        const game1Elements = await screen.findAllByText('Game 1');
        expect(game1Elements.length).toBeGreaterThan(0);
    });

    it('shows "No games found" when the catalog is empty', async () => {
        gameService.getGames.mockResolvedValue({ 
            data: { 
                content: [], 
                totalPages: 0 
            } 
        });

        render(
            <MemoryRouter future={future}>
                <HomePage />
            </MemoryRouter>
        );

        const emptyMessage = await screen.findByText('No games found');
        expect(emptyMessage).toBeInTheDocument();
    });

    it('opens filter panel when clicking on Filters button', async () => {
        render(
            <MemoryRouter future={future}>
                <HomePage />
            </MemoryRouter>
        );

        const filtersBtn = screen.getByRole('button', { name: /Filters/i });
        fireEvent.click(filtersBtn);

        await waitFor(() => {
            expect(screen.getByText('All Genres')).toBeInTheDocument();
            expect(screen.getByText('All Platforms')).toBeInTheDocument();
        });
    });
});

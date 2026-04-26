import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import GameDetailsPage from './GameDetailsPage';
import { gameService } from '../services/api';

// Mock the API service
jest.mock('../services/api', () => ({
    gameService: {
        getGameDetails: jest.fn()
    }
}));

const mockGame = {
    id: '1',
    title: 'Test Game',
    coverUrl: 'http://test.com/cover.jpg',
    rating: 4.5,
    releaseYear: 2024,
    publisher: 'Test Publisher',
    description: 'Test Description',
    platforms: ['PC', 'PS5'],
    genres: ['Action', 'RPG'],
    tags: ['Singleplayer', 'Story Rich']
};

describe('GameDetailsPage', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    test('renders loading state initially', () => {
        gameService.getGameDetails.mockReturnValue(new Promise(() => {}));
        render(
            <MemoryRouter initialEntries={['/game/1']}>
                <Routes>
                    <Route path="/game/:id" element={<GameDetailsPage />} />
                </Routes>
            </MemoryRouter>
        );
        expect(screen.getByText(/Loading quest details.../i)).toBeInTheDocument();
    });

    test('renders game details after successful fetch', async () => {
        gameService.getGameDetails.mockResolvedValue({ data: mockGame });
        
        render(
            <MemoryRouter initialEntries={['/game/1']}>
                <Routes>
                    <Route path="/game/:id" element={<GameDetailsPage />} />
                </Routes>
            </MemoryRouter>
        );

        await waitFor(() => {
            expect(screen.getByText('Test Game')).toBeInTheDocument();
        });

        expect(screen.getByText('2024')).toBeInTheDocument();
        expect(screen.getByText('Test Publisher')).toBeInTheDocument();
        expect(screen.getByText('Test Description')).toBeInTheDocument();
        expect(screen.getByText('PC')).toBeInTheDocument();
        expect(screen.getByText('Action')).toBeInTheDocument();
        expect(screen.getByText('#singleplayer')).toBeInTheDocument();
    });

    test('renders error state when fetch fails', async () => {
        gameService.getGameDetails.mockRejectedValue(new Error('Fetch failed'));
        
        render(
            <MemoryRouter initialEntries={['/game/1']}>
                <Routes>
                    <Route path="/game/:id" element={<GameDetailsPage />} />
                </Routes>
            </MemoryRouter>
        );

        await waitFor(() => {
            expect(screen.getByText(/Game Not Found/i)).toBeInTheDocument();
        });
        expect(screen.getByText(/Could not find this game/i)).toBeInTheDocument();
    });
});

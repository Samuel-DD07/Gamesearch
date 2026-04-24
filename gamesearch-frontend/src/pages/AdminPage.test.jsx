import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import AdminPage from './AdminPage';
import { gameService, partnerService } from '../services/api';

// Mock API
jest.mock('../services/api', () => ({
    gameService: {
        getGames: jest.fn(),
        createGame: jest.fn(),
        updateGame: jest.fn(),
        deleteGame: jest.fn(),
        getRecentGames: jest.fn(),
        getPopularGames: jest.fn(),
    },
    partnerService: {
        getAllPartners: jest.fn(),
        register: jest.fn(),
        submitGame: jest.fn(),
        bulkImport: jest.fn(),
        getIngestionStatus: jest.fn(),
    }
}));

const future = { v7_startTransition: true, v7_relativeSplatPath: true };

describe('AdminPage', () => {
    beforeEach(() => {
        jest.clearAllMocks();
        
        // Default mock values to prevent render crashes
        gameService.getGames.mockResolvedValue({ data: { content: [], totalElements: 0, totalPages: 0 } });
        partnerService.getAllPartners.mockResolvedValue({ data: [] });
    });

    it('renders the initial dashboard view', async () => {
        await act(async () => {
            render(
                <MemoryRouter future={future}>
                    <AdminPage />
                </MemoryRouter>
            );
        });

        expect(screen.getByText(/System Overview/i)).toBeInTheDocument();
    });

    it('switches to games catalog and shows the game list', async () => {
        gameService.getGames.mockResolvedValue({
            data: {
                content: [{ id: '1', title: 'Test Game Mock', releaseYear: 2024, platforms: ['PC'] }],
                totalElements: 1,
                totalPages: 1
            }
        });

        await act(async () => {
            render(
                <MemoryRouter future={future}>
                    <AdminPage />
                </MemoryRouter>
            );
        });

        const gamesTab = screen.getByRole('button', { name: /Games Catalog/i });
        fireEvent.click(gamesTab);

        await waitFor(() => {
            expect(screen.getByText('Test Game Mock')).toBeInTheDocument();
        });
    });

    it('opens the add game modal when clicking Add Game', async () => {
        await act(async () => {
            render(
                <MemoryRouter future={future}>
                    <AdminPage />
                </MemoryRouter>
            );
        });

        fireEvent.click(screen.getByRole('button', { name: /Games Catalog/i }));
        
        await waitFor(() => {
            expect(screen.getByRole('button', { name: /Add Game/i })).toBeInTheDocument();
        });

        fireEvent.click(screen.getByRole('button', { name: /Add Game/i }));

        await waitFor(() => {
            expect(screen.getByText(/Add New Game/i)).toBeInTheDocument();
        });
    });
});

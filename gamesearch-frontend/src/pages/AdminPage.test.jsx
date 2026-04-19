import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import AdminPage from './AdminPage';
import { partnerService } from '../services/api';

// Mock API
jest.mock('../services/api', () => ({
    partnerService: {
        submitGame: jest.fn(),
        getIngestionStatus: jest.fn(),
    }
}));

describe('AdminPage', () => {
    beforeEach(() => {
        jest.clearAllMocks();
        // Mock setInterval and clearInterval
        jest.useFakeTimers();
    });

    afterEach(() => {
        jest.useRealTimers();
    });

    it('renders the admin dashboard completely', () => {
        render(
            <MemoryRouter>
                <AdminPage />
            </MemoryRouter>
        );

        expect(screen.getByText('Partner Dashboard')).toBeInTheDocument();
        expect(screen.getByText('Catalog Ingestion')).toBeInTheDocument();
        expect(screen.getByText('System Status')).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /Sync Now/i })).toBeInTheDocument();
    });

    it('triggers a sync and starts polling for status', async () => {
        partnerService.submitGame.mockResolvedValue({ 
            data: { gameId: 'ext-123', status: 'PENDING', message: 'Kafka message sent' } 
        });

        render(
            <MemoryRouter>
                <AdminPage />
            </MemoryRouter>
        );

        const syncBtn = screen.getByRole('button', { name: /Sync Now/i });
        fireEvent.click(syncBtn);

        await waitFor(() => {
            expect(partnerService.submitGame).toHaveBeenCalledTimes(1);
        });

        // The monitor panel should open
        expect(await screen.findByText('Live Ingestion Monitor')).toBeInTheDocument();
        expect(screen.getByText('PENDING')).toBeInTheDocument();

        // Advance timers to trigger polling
        partnerService.getIngestionStatus.mockResolvedValue({
            data: { gameId: 'ext-123', status: 'SUCCESS', message: 'Ingested', internalGameId: 'db-123' }
        });

        jest.advanceTimersByTime(2000);

        await waitFor(() => {
            expect(partnerService.getIngestionStatus).toHaveBeenCalledWith('ext-123');
        });

        expect(await screen.findByText('SUCCESS')).toBeInTheDocument();
        expect(screen.getByText(/Ingested in Catalog/i)).toBeInTheDocument();
    });
});

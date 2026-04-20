import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import GameCard from './GameCard';

const future = { v7_startTransition: true, v7_relativeSplatPath: true };

describe('GameCard', () => {
    const mockGame = {
        id: '123',
        title: 'Cyberpunk 2077',
        coverUrl: 'http://example.com/cover.jpg',
        rating: 8.5,
        releaseYear: 2020,
        genres: ['RPG', 'Action'],
        platforms: ['PC', 'PS5']
    };

    it('renders game details correctly', () => {
        render(
            <MemoryRouter future={future}>
                <GameCard game={mockGame} />
            </MemoryRouter>
        );

        expect(screen.getByText('Cyberpunk 2077')).toBeInTheDocument();
        expect(screen.getByText('2020')).toBeInTheDocument();
        expect(screen.getByText('8.5')).toBeInTheDocument();
        
        // Check image
        const image = screen.getByAltText('Cyberpunk 2077');
        expect(image).toHaveAttribute('src', 'http://example.com/cover.jpg');

        // Check genres
        expect(screen.getByText('RPG')).toBeInTheDocument();
        expect(screen.getByText('Action')).toBeInTheDocument();
        
        // Link to details
        const detailsLink = screen.getByRole('link', { name: /Details/i });
        expect(detailsLink).toHaveAttribute('href', '/games/123');
    });
});

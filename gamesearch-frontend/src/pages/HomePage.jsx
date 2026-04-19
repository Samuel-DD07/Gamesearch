import React, { useState, useEffect, useCallback } from 'react';
import { gameService } from '../services/api';
import GameCard from '../components/GameCard';
import { Search, Filter, SlidersHorizontal, ChevronLeft, ChevronRight, Loader2 } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

const HomePage = () => {
    const [games, setGames] = useState([]);
    const [recentGames, setRecentGames] = useState([]);
    const [popularGames, setPopularGames] = useState([]);
    const [loading, setLoading] = useState(true);
    const [loadingHome, setLoadingHome] = useState(true);
    const [filters, setFilters] = useState({ q: '', genre: '', platform: '', year: '' });
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [showFilters, setShowFilters] = useState(false);

    const fetchHomeData = async () => {
        setLoadingHome(true);
        try {
            const [recentRes, popularRes] = await Promise.all([
                gameService.getRecentGames(),
                gameService.getPopularGames()
            ]);
            setRecentGames(recentRes.data);
            setPopularGames(popularRes.data);
        } catch (err) {
            console.error('Failed to fetch home data:', err);
        } finally {
            setLoadingHome(false);
        }
    };

    const fetchGames = useCallback(async () => {
        setLoading(true);
        try {
            const { data } = await gameService.getGames(page, 12, filters);
            setGames(data.content);
            setTotalPages(data.totalPages);
        } catch (err) {
            console.error('Failed to fetch catalog:', err);
        } finally {
            setLoading(false);
        }
    }, [page, filters]);

    useEffect(() => {
        fetchHomeData();
    }, []);

    useEffect(() => {
        const timer = setTimeout(() => fetchGames(), 400);
        return () => clearTimeout(timer);
    }, [fetchGames]);

    const handleFilterChange = (e) => {
        const { name, value } = e.target;
        setFilters(prev => ({ ...prev, [name]: value }));
        setPage(0);
    };

    const GameCarousel = ({ title, items, icon: Icon }) => (
        <section className="space-y-4">
            <div className="flex items-center gap-2 mb-6">
                <div className="p-2 bg-brand-500/10 rounded-lg">
                    <Icon className="h-5 w-5 text-brand-500" />
                </div>
                <h2 className="text-2xl font-bold text-white">{title}</h2>
            </div>
            <div className="flex gap-6 overflow-x-auto pb-6 scrollbar-hide -mx-4 px-4 snap-x">
                {items.map(game => (
                    <div key={game.id} className="min-w-[280px] md:min-w-[320px] snap-start">
                        <GameCard game={game} />
                    </div>
                ))}
            </div>
        </section>
    );

    return (
        <div className="space-y-16 pb-20">
            <section className="text-center py-16 relative overflow-hidden">
                <div className="absolute top-0 left-1/2 -translate-x-1/2 w-full h-full bg-brand-500/5 blur-3xl rounded-full" />
                <motion.h1
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    className="text-5xl md:text-7xl font-black mb-6 bg-gradient-to-br from-white to-slate-500 bg-clip-text text-transparent relative z-10"
                >
                    Discover Your Next Game
                </motion.h1>
                <p className="text-slate-400 max-w-2xl mx-auto text-xl relative z-10">
                    Browse through our curated collection of premium video games.
                    Filtered, categorized, and detailed just for you.
                </p>
            </section>

            {!loadingHome && (
                <div className="space-y-16">
                    {recentGames.length > 0 && (
                        <GameCarousel title="Nouveautés" items={recentGames} icon={Loader2} />
                    )}
                    {popularGames.length > 0 && (
                        <GameCarousel title="Les mieux notés" items={popularGames} icon={SlidersHorizontal} />
                    )}
                </div>
            )}

            <div className="space-y-8 pt-8 border-t border-slate-800/50">
                <div className="flex items-center gap-2 mb-2">
                    <Search className="h-5 w-5 text-slate-500" />
                    <h2 className="text-2xl font-bold text-white">Full Catalog</h2>
                </div>

                <div className="flex flex-col md:flex-row gap-4 items-center justify-between bg-slate-900/30 p-4 rounded-2xl border border-slate-800/50 backdrop-blur-sm">
                    <div className="relative w-full md:max-w-md">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-slate-500" />
                        <input
                            type="text"
                            name="q"
                            placeholder="Search by title, publisher or description..."
                            className="w-full pl-10 pr-4 py-2.5 bg-slate-950 border border-slate-800 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all"
                            value={filters.q}
                            onChange={handleFilterChange}
                        />
                    </div>

                    <div className="flex items-center gap-3 w-full md:w-auto">
                        <button
                            onClick={() => setShowFilters(!showFilters)}
                            className={`flex items-center gap-2 px-4 py-2.5 rounded-xl border transition-all font-medium ${showFilters ? 'bg-brand-500/10 border-brand-500 text-brand-400' : 'bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700'}`}
                        >
                            <SlidersHorizontal className="h-4 w-4" />
                            <span>Filters</span>
                        </button>

                        <div className="h-10 w-px bg-slate-800 hidden md:block" />

                        <div className="text-sm font-medium text-slate-500">
                            {games.length} results shown
                        </div>
                    </div>
                </div>

                <AnimatePresence>
                    {showFilters && (
                        <motion.div
                            initial={{ height: 0, opacity: 0 }}
                            animate={{ height: 'auto', opacity: 1 }}
                            exit={{ height: 0, opacity: 0 }}
                            className="overflow-hidden"
                        >
                            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 p-6 bg-slate-900/20 rounded-2xl border border-slate-800/50">
                                <div className="space-y-2">
                                    <label className="text-xs font-bold text-slate-500 uppercase flex items-center gap-2">
                                        <Filter className="h-3 w-3" /> Genre
                                    </label>
                                    <select
                                        name="genre"
                                        value={filters.genre}
                                        onChange={handleFilterChange}
                                        className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 outline-none focus:border-brand-500"
                                    >
                                        <option value="">All Genres</option>
                                        <option value="RPG">RPG</option>
                                        <option value="Action">Action</option>
                                        <option value="Adventure">Adventure</option>
                                        <option value="Strategy">Strategy</option>
                                        <option value="Souls-like">Souls-like</option>
                                    </select>
                                </div>
                                <div className="space-y-2">
                                    <label className="text-xs font-bold text-slate-500 uppercase">Platform</label>
                                    <select
                                        name="platform"
                                        value={filters.platform}
                                        onChange={handleFilterChange}
                                        className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 outline-none focus:border-brand-500"
                                    >
                                        <option value="">All Platforms</option>
                                        <option value="PC">PC</option>
                                        <option value="PS5">PS5</option>
                                        <option value="Xbox Series X/S">Xbox Series X/S</option>
                                    </select>
                                </div>
                                <div className="space-y-2">
                                    <label className="text-xs font-bold text-slate-500 uppercase">Year</label>
                                    <input
                                        type="number"
                                        name="year"
                                        placeholder="e.g. 2023"
                                        value={filters.year}
                                        onChange={handleFilterChange}
                                        className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 outline-none focus:border-brand-500"
                                    />
                                </div>
                            </div>
                        </motion.div>
                    )}
                </AnimatePresence>

                {loading ? (
                    <div className="flex flex-col items-center justify-center py-40 gap-4">
                        <Loader2 className="h-12 w-12 text-brand-500 animate-spin" />
                        <p className="text-slate-500 animate-pulse font-medium">Fetching catalog...</p>
                    </div>
                ) : games.length > 0 ? (
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                        {games.map(game => (
                            <GameCard key={game.id} game={game} />
                        ))}
                    </div>
                ) : (
                    <div className="text-center py-40 border-2 border-dashed border-slate-900 rounded-3xl">
                        <div className="text-6xl mb-4">👾</div>
                        <h3 className="text-xl font-bold text-slate-300">No games found</h3>
                        <p className="text-slate-600">Try adjusting your filters or search term.</p>
                    </div>
                )}

                {totalPages > 1 && (
                    <div className="flex items-center justify-center gap-4 py-8">
                        <button
                            disabled={page === 0}
                            onClick={() => setPage(prev => prev - 1)}
                            className="p-2 rounded-lg bg-slate-900 border border-slate-800 disabled:opacity-30 disabled:cursor-not-allowed hover:border-slate-600 transition-colors"
                        >
                            <ChevronLeft />
                        </button>
                        <span className="text-sm font-bold text-slate-400">
                            Page {page + 1} of {totalPages}
                        </span>
                        <button
                            disabled={page >= totalPages - 1}
                            onClick={() => setPage(prev => prev + 1)}
                            className="p-2 rounded-lg bg-slate-900 border border-slate-800 disabled:opacity-30 disabled:cursor-not-allowed hover:border-slate-600 transition-colors"
                        >
                            <ChevronRight />
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default HomePage;

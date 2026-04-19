import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { gameService } from '../services/api';
import { ChevronLeft, Star, Calendar, Building2, Monitor, Gamepad2, Tag, Loader2, AlertCircle } from 'lucide-react';
import { motion } from 'framer-motion';

const GameDetailsPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [game, setGame] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchDetails = async () => {
            try {
                const { data } = await gameService.getGameDetails(id);
                setGame(data);
            } catch (err) {
                setError('Could not find this game. It might have been removed.');
            } finally {
                setLoading(false);
            }
        };
        fetchDetails();
    }, [id]);

    if (loading) {
        return (
            <div className="flex flex-col items-center justify-center py-40 gap-4">
                <Loader2 className="h-12 w-12 text-brand-500 animate-spin" />
                <p className="text-slate-500 font-medium tracking-wide">Loading quest details...</p>
            </div>
        );
    }

    if (error || !game) {
        return (
            <div className="max-w-md mx-auto py-20 text-center">
                <div className="bg-red-500/10 border border-red-500/20 text-red-400 p-8 rounded-3xl">
                    <AlertCircle className="h-12 w-12 mx-auto mb-4" />
                    <h2 className="text-xl font-bold mb-2">Game Not Found</h2>
                    <p className="text-sm opacity-80 mb-6">{error}</p>
                    <button
                        onClick={() => navigate('/')}
                        className="px-6 py-2 bg-slate-900 border border-slate-800 rounded-xl hover:bg-slate-800 transition-colors"
                    >
                        Go Back Home
                    </button>
                </div>
            </div>
        );
    }

    return (
        <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="space-y-12"
        >
            <button
                onClick={() => navigate('/')}
                className="flex items-center gap-2 group text-slate-400 hover:text-white transition-colors"
            >
                <ChevronLeft className="h-5 w-5 group-hover:-translate-x-1 transition-transform" />
                <span className="font-bold">Back to Catalog</span>
            </button>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-start">
                {}
                <motion.div
                    initial={{ x: -40, opacity: 0 }}
                    animate={{ x: 0, opacity: 1 }}
                    className="relative rounded-3xl overflow-hidden border border-slate-800 shadow-2xl shadow-brand-500/5 group"
                >
                    <img
                        src={game.coverUrl}
                        alt={game.title}
                        className="w-full h-auto object-cover group-hover:scale-105 transition-transform duration-700"
                    />
                    <div className="absolute inset-0 bg-gradient-to-t from-slate-950/80 via-transparent to-transparent" />
                </motion.div>

                {}
                <div className="space-y-8">
                    <div className="space-y-4">
                        <div className="flex items-center gap-3">
                            <span className="px-3 py-1 bg-brand-500/20 text-brand-400 text-xs font-black uppercase tracking-tighter rounded-full border border-brand-500/30">
                                Verified Entry
                            </span>
                            <div className="flex items-center gap-1.5 text-yellow-500 font-black">
                                <Star className="h-5 w-5 fill-yellow-500" />
                                <span className="text-xl leading-none">{game.rating}</span>
                            </div>
                        </div>

                        <h1 className="text-5xl md:text-6xl font-black text-white leading-tight">
                            {game.title}
                        </h1>

                        <div className="flex flex-wrap gap-4 text-slate-400">
                            <div className="flex items-center gap-1.5 bg-slate-900/50 px-3 py-1.5 rounded-full border border-slate-800/50">
                                <Calendar className="h-4 w-4" />
                                <span className="text-sm font-medium">{game.releaseYear}</span>
                            </div>
                            <div className="flex items-center gap-1.5 bg-slate-900/50 px-3 py-1.5 rounded-full border border-slate-800/50">
                                <Building2 className="h-4 w-4" />
                                <span className="text-sm font-medium">{game.publisher}</span>
                            </div>
                        </div>
                    </div>

                    <div className="space-y-4">
                        <h2 className="text-xs font-black uppercase tracking-[0.2em] text-slate-500">The Story</h2>
                        <p className="text-lg text-slate-300 leading-relaxed font-medium">
                            {game.description}
                        </p>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-8 pt-8 border-t border-slate-800/50">
                        <div className="space-y-4">
                            <h2 className="text-xs font-black uppercase tracking-[0.2em] text-slate-500 flex items-center gap-2">
                                <Monitor className="h-4 w-4" /> Platforms
                            </h2>
                            <div className="flex flex-wrap gap-2">
                                {game.platforms?.map(p => (
                                    <span key={p} className="px-3 py-1.5 bg-slate-900 text-slate-300 rounded-lg border border-slate-800 text-sm font-bold">
                                        {p}
                                    </span>
                                ))}
                            </div>
                        </div>

                        <div className="space-y-4">
                            <h2 className="text-xs font-black uppercase tracking-[0.2em] text-slate-500 flex items-center gap-2">
                                <Gamepad2 className="h-4 w-4" /> Genres
                            </h2>
                            <div className="flex flex-wrap gap-2">
                                {game.genres?.map(g => (
                                    <span key={g} className="px-3 py-1.5 bg-brand-600/10 text-brand-400 rounded-lg border border-brand-500/20 text-sm font-bold">
                                        {g}
                                    </span>
                                ))}
                            </div>
                        </div>
                    </div>

                    {game.tags?.length > 0 && (
                        <div className="pt-8 space-y-4 text-sm">
                            <div className="flex items-center gap-2 text-slate-500 font-bold uppercase tracking-widest text-[10px]">
                                <Tag className="h-3 w-3" /> Popular Tags
                            </div>
                            <div className="flex flex-wrap gap-2">
                                {game.tags.map(tag => (
                                    <span key={tag} className="text-slate-400 font-medium hover:text-white cursor-help transition-colors">
                                        #{tag.toLowerCase().replace(' ', '')}
                                    </span>
                                ))}
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </motion.div>
    );
};

export default GameDetailsPage;

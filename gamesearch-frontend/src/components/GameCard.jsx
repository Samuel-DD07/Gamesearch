import React from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Star, Monitor, Gamepad2 } from 'lucide-react';

const GameCard = ({ game }) => {
    return (
        <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            whileHover={{ y: -4 }}
            transition={{ duration: 0.3 }}
            className="group relative flex flex-col overflow-hidden rounded-2xl border border-slate-800 bg-slate-900/50 hover:border-brand-500/50 transition-colors"
        >
            <Link to={`/games/${game.id}`} className="block relative aspect-[16/9] overflow-hidden">
                <img
                    src={game.coverUrl}
                    alt={game.title}
                    className="h-full w-full object-cover transition-transform duration-500 group-hover:scale-110"
                    onError={(e) => {
                        e.target.onerror = null;
                        e.target.src = "https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=2070&auto=format&fit=crop";
                    }}
                />
                <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-transparent to-transparent opacity-60" />
                <div className="absolute top-3 right-3 flex items-center gap-1 rounded-full bg-slate-950/60 px-2 py-1 backdrop-blur-md border border-slate-800/50">
                    <Star className="h-3 w-3 text-yellow-500 fill-yellow-500" />
                    <span className="text-xs font-bold">{game.rating}</span>
                </div>
            </Link>

            <div className="flex flex-1 flex-col p-5">
                <div className="flex items-start justify-between gap-2 mb-2">
                    <h3 className="text-lg font-bold text-white group-hover:text-brand-400 transition-colors">
                        {game.title}
                    </h3>
                    <span className="text-xs font-medium text-slate-500 whitespace-nowrap">{game.releaseYear}</span>
                </div>

                <div className="flex flex-wrap gap-1.5 mb-4">
                    {game.genres?.slice(0, 3).map((genre) => (
                        <span key={genre} className="rounded-md bg-slate-800 px-2 py-0.5 text-[10px] font-semibold text-slate-300 uppercase tracking-wider">
                            {genre}
                        </span>
                    ))}
                </div>

                <div className="mt-auto pt-4 border-t border-slate-800/50 flex items-center justify-between">
                    <div className="flex gap-2">
                        {game.platforms?.some(p => p.includes('PC')) && <Monitor className="h-4 w-4 text-slate-500" />}
                        {game.platforms?.some(p => p.includes('PS') || p.includes('Xbox')) && <Gamepad2 className="h-4 w-4 text-slate-500" />}
                    </div>
                    <Link
                        to={`/games/${game.id}`}
                        className="text-sm font-bold text-brand-500 hover:text-brand-400 transition-colors flex items-center gap-1"
                    >
                        Details <span className="text-lg leading-none">&rarr;</span>
                    </Link>
                </div>
            </div>
        </motion.div>
    );
};

export default GameCard;

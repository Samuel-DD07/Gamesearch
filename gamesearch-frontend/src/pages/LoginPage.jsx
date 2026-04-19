import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/api';
import { Lock, User, Loader2, ArrowRight, ShieldCheck } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

const LoginPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        try {
            const { data } = await authService.login(username, password);
            localStorage.setItem('token', data.token);
            navigate('/admin');
        } catch (err) {
            setError(err.response?.data?.message || 'Identifiant ou mot de passe incorrect.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-[80vh] flex items-center justify-center p-4">
            <div className="absolute top-0 left-1/2 -translate-x-1/2 w-full max-w-4xl h-full bg-brand-500/5 blur-[120px] rounded-full -z-10" />
            
            <motion.div 
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                className="w-full max-w-md"
            >
                <div className="bg-slate-900/50 backdrop-blur-xl border border-slate-800 rounded-3xl p-8 shadow-2xl relative overflow-hidden">
                    <div className="absolute top-0 right-0 p-8 opacity-5">
                        <Lock className="h-24 w-24" />
                    </div>

                    <div className="flex flex-col items-center text-center mb-8">
                        <div className="h-16 w-16 rounded-2xl bg-gradient-to-tr from-brand-600 to-brand-400 flex items-center justify-center mb-4 shadow-lg shadow-brand-500/20">
                            <ShieldCheck className="h-8 w-8 text-white" />
                        </div>
                        <h1 className="text-3xl font-bold text-white mb-2">Administration</h1>
                        <p className="text-slate-400">Connectez-vous pour gérer le catalogue de jeux.</p>
                    </div>

                    <form onSubmit={handleLogin} className="space-y-6">
                        {error && (
                            <motion.div 
                                initial={{ opacity: 0, scale: 0.95 }}
                                animate={{ opacity: 1, scale: 1 }}
                                className="bg-red-500/10 border border-red-500/50 text-red-400 p-4 rounded-xl text-sm font-medium"
                            >
                                {error}
                            </motion.div>
                        )}

                        <div className="space-y-2">
                            <label className="text-sm font-bold text-slate-500 uppercase ml-1">Identifiant</label>
                            <div className="relative">
                                <User className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-slate-500" />
                                <input
                                    type="text"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    className="w-full pl-12 pr-4 py-3.5 bg-slate-950/50 border border-slate-800 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all placeholder:text-slate-700"
                                    placeholder="Ex: admin"
                                    required
                                />
                            </div>
                        </div>

                        <div className="space-y-2">
                            <label className="text-sm font-bold text-slate-500 uppercase ml-1">Mot de passe</label>
                            <div className="relative">
                                <Lock className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-slate-500" />
                                <input
                                    type="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    className="w-full pl-12 pr-4 py-3.5 bg-slate-950/50 border border-slate-800 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all placeholder:text-slate-700"
                                    placeholder="••••••••"
                                    required
                                />
                            </div>
                        </div>

                        <button
                            type="submit"
                            disabled={loading}
                            className="group w-full relative h-14 bg-brand-600 hover:bg-brand-500 text-white rounded-xl font-bold transition-all disabled:opacity-50 disabled:cursor-not-allowed overflow-hidden shadow-lg shadow-brand-500/20"
                        >
                            <AnimatePresence mode="wait">
                                {loading ? (
                                    <motion.div 
                                        key="loading"
                                        initial={{ opacity: 0 }} 
                                        animate={{ opacity: 1 }}
                                        exit={{ opacity: 0 }}
                                        className="absolute inset-0 flex items-center justify-center"
                                    >
                                        <Loader2 className="h-6 w-6 animate-spin" />
                                    </motion.div>
                                ) : (
                                    <motion.div 
                                        key="submit"
                                        initial={{ opacity: 0 }} 
                                        animate={{ opacity: 1 }}
                                        exit={{ opacity: 0 }}
                                        className="flex items-center justify-center gap-2"
                                    >
                                        Se connecter
                                        <ArrowRight className="h-5 w-5 group-hover:translate-x-1 transition-transform" />
                                    </motion.div>
                                )}
                            </AnimatePresence>
                        </button>
                    </form>
                </div>
                
                <p className="text-center mt-8 text-slate-600 text-sm">
                    Accès réservé au personnel autorisé uniquement.
                </p>
            </motion.div>
        </div>
    );
};

export default LoginPage;

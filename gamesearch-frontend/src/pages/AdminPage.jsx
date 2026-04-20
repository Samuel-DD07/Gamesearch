import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Database, Zap, ShieldCheck, Activity, Loader2, CheckCircle2, AlertCircle } from 'lucide-react';
import { partnerService } from '../services/api';

const AdminPage = () => {
    const [syncing, setSyncing] = useState(false);
    const [status, setStatus] = useState(null);
    const [polling, setPolling] = useState(false);

    const triggerSync = async () => {
        setSyncing(true);
        try {
            const testGame = {
                gameId: `KAFKA-SYNC-${Date.now()}`,
                title: "Kafka Full-Stack Proof",
                releaseYear: 2024,
                genres: ["Simulation", "Technology"],
                platforms: ["PC", "Cloud"],
                publisher: "Kafka Studios",
                description: "Démonstration de l'asynchronisme de bout en bout.",
                rating: 5.0,
                tags: ["Kafka", "Async", "Proof"]
            };
            const { data } = await partnerService.submitGame(testGame);
            setStatus(data);
            setPolling(true);
        } catch (error) {
            console.error("Sync failed:", error);
        } finally {
            setSyncing(false);
        }
    };

    useEffect(() => {
        let interval;
        if (polling && status?.gameId) {
            interval = setInterval(async () => {
                try {
                    const { data } = await partnerService.getIngestionStatus(status.gameId);
                    setStatus(data);
                    if (data.status === 'SUCCESS' || data.status === 'ERROR') {
                        setPolling(false);
                        clearInterval(interval);
                    }
                } catch (error) {
                    console.error("Polling failed:", error);
                    setPolling(false);
                    clearInterval(interval);
                }
            }, 2000);
        }
        return () => clearInterval(interval);
    }, [polling, status]);

    return (
        <div className="space-y-12">
            <header>
                <h1 className="text-4xl font-black text-white mb-2">Partner Dashboard</h1>
                <p className="text-slate-400">Manage your game catalog and ingestion pipelines.</p>
            </header>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="p-6 rounded-2xl bg-slate-900/50 border border-slate-800 space-y-4 shadow-lg shadow-brand-500/5">
                    <Database className="h-8 w-8 text-brand-500" />
                    <h3 className="font-bold text-lg">Catalog Ingestion</h3>
                    <p className="text-sm text-slate-500">Trigger a manual synchronization of your latest game metadata.</p>
                    <button 
                        onClick={triggerSync}
                        disabled={syncing || polling}
                        className="w-full h-12 flex items-center justify-center bg-brand-600 hover:bg-brand-500 text-white font-bold rounded-xl transition-all disabled:opacity-50 disabled:cursor-not-allowed shadow-lg shadow-brand-500/20"
                    >
                        {syncing ? <Loader2 className="h-5 w-5 animate-spin" /> : "Sync Now"}
                    </button>
                </div>

                <div className="p-6 rounded-2xl bg-slate-900/50 border border-slate-800 space-y-4">
                    <Zap className="h-8 w-8 text-yellow-500" />
                    <h3 className="font-bold text-lg">Real-time Monitoring</h3>
                    <p className="text-sm text-slate-500">The ingestion pipeline is active and monitoring the Kafka broker health.</p>
                </div>
            </div>

            <AnimatePresence>
                {status && (
                    <motion.div 
                        initial={{ opacity: 0, height: 0 }}
                        animate={{ opacity: 1, height: 'auto' }}
                        exit={{ opacity: 0, height: 0 }}
                        className="p-8 rounded-3xl bg-slate-900/40 border border-brand-500/20 shadow-xl shadow-brand-500/5 overflow-hidden"
                    >
                        <div className="flex items-center justify-between mb-6">
                            <div className="flex items-center gap-3">
                                <Zap className="h-5 w-5 text-brand-500" />
                                <h2 className="text-xl font-bold italic tracking-tight uppercase">Live Ingestion Monitor</h2>
                            </div>
                            <div className={`flex items-center gap-2 px-3 py-1 rounded-full text-[10px] font-black uppercase border ${
                                status.status === 'SUCCESS' ? 'bg-green-500/10 text-green-400 border-green-500/20' :
                                status.status === 'PENDING' ? 'bg-yellow-500/10 text-yellow-400 border-yellow-500/20' :
                                'bg-red-500/10 text-red-400 border-red-500/20'
                            }`}>
                                {status.status === 'PENDING' && <Loader2 className="h-3 w-3 animate-spin" />}
                                {status.status === 'SUCCESS' && <CheckCircle2 className="h-3 w-3" />}
                                {status.status === 'ERROR' && <AlertCircle className="h-3 w-3" />}
                                {status.status}
                            </div>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 text-sm">
                            <div className="space-y-4">
                                <div className="flex justify-between border-b border-slate-800 pb-2">
                                    <span className="text-slate-500 uppercase font-bold text-[10px]">Game ID</span>
                                    <span className="text-slate-200 font-mono">{status.gameId}</span>
                                </div>
                                <div className="flex justify-between border-b border-slate-800 pb-2">
                                    <span className="text-slate-500 uppercase font-bold text-[10px]">Submitted At</span>
                                    <span className="text-slate-200">{new Date(status.createdAt).toLocaleTimeString()}</span>
                                </div>
                            </div>
                            <div className="space-y-2">
                                <span className="text-slate-500 uppercase font-bold text-[10px]">Backend Message</span>
                                <p className="p-3 bg-slate-950/50 rounded-xl border border-slate-800 text-slate-300">
                                    {status.message}
                                </p>
                                {status.internalGameId && (
                                    <div className="mt-4 p-3 bg-green-500/5 border border-green-500/10 rounded-xl text-green-400 font-medium">
                                        ✓ Ingested in Catalog (DB ID: {status.internalGameId})
                                    </div>
                                )}
                            </div>
                        </div>
                    </motion.div>
                )}
            </AnimatePresence>

            <div className="p-8 rounded-3xl bg-slate-900/20 border border-slate-800/50">
                <div className="flex items-center gap-3 mb-6">
                    <Activity className="h-5 w-5 text-brand-500" />
                    <h2 className="text-xl font-bold">System Status</h2>
                </div>
                <div className="space-y-4">
                    <div className="flex items-center justify-between py-3 border-b border-slate-800/50">
                        <span className="text-slate-400">Backend API</span>
                        <span className="px-2 py-1 bg-green-500/10 text-green-500 text-[10px] font-black uppercase rounded-md border border-green-500/20">Operational</span>
                    </div>
                    <div className="flex items-center justify-between py-3 border-b border-slate-800/50">
                        <span className="text-slate-400">Kafka Cluster</span>
                        <span className="px-2 py-1 bg-green-500/10 text-green-500 text-[10px] font-black uppercase rounded-md border border-green-500/20">Operational</span>
                    </div>
                    <div className="flex items-center justify-between py-3">
                        <span className="text-slate-400">Ingestion Consumer</span>
                        <span className="px-2 py-1 bg-slate-800 text-slate-500 text-[10px] font-black uppercase rounded-md">Standby</span>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AdminPage;

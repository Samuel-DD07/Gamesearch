import React, { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
    LayoutDashboard, 
    Gamepad2, 
    Users, 
    Handshake, 
    Plus, 
    Upload, 
    Search, 
    Trash2, 
    Edit, 
    Loader2, 
    CheckCircle2, 
    AlertCircle,
    Activity,
    FileJson,
    Save,
    X,
    Filter
} from 'lucide-react';
import { gameService, partnerService } from '../services/api';

const AdminPage = () => {
    const [activeTab, setActiveTab] = useState('dashboard');
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState(null);

    // Dashboard State
    const [stats, setStats] = useState({ games: 0, partners: 0 });
    
    // Games State
    const [games, setGames] = useState([]);
    const [gameFilters, setGameFilters] = useState({ q: '', page: 0 });
    const [totalPages, setTotalPages] = useState(0);
    const [showGameForm, setShowGameForm] = useState(false);
    const [editingGame, setEditingGame] = useState(null);
    const [isBulkUpload, setIsBulkUpload] = useState(false);

    // Partners State
    const [partners, setPartners] = useState([]);
    const [showPartnerForm, setShowPartnerForm] = useState(false);

    useEffect(() => {
        if (activeTab === 'dashboard') fetchStats();
        if (activeTab === 'games') fetchGames();
        if (activeTab === 'partners') fetchPartners();
    }, [activeTab, gameFilters.page]);

    const fetchStats = async () => {
        try {
            const [gRes, pRes] = await Promise.all([
                gameService.getGames(0, 1),
                partnerService.getAllPartners()
            ]);
            setStats({
                games: gRes.data.totalElements,
                partners: pRes.data.length
            });
        } catch (e) { console.error(e); }
    };

    const fetchGames = async () => {
        setLoading(true);
        try {
            const { data } = await gameService.getGames(gameFilters.page, 10, { q: gameFilters.q });
            setGames(data.content);
            setTotalPages(data.totalPages);
        } catch (e) { console.error(e); }
        setLoading(false);
    };

    const fetchPartners = async () => {
        setLoading(true);
        try {
            const { data } = await partnerService.getAllPartners();
            setPartners(data);
        } catch (e) { console.error(e); }
        setLoading(false);
    };

    const handleDeleteGame = async (id) => {
        if (!window.confirm('Are you sure you want to delete this game?')) return;
        try {
            await gameService.deleteGame(id);
            setMessage({ type: 'success', text: 'Game deleted successfully' });
            fetchGames();
        } catch (e) { setMessage({ type: 'error', text: 'Failed to delete game' }); }
    };

    const notify = (type, text) => {
        setMessage({ type, text });
        setTimeout(() => setMessage(null), 5000);
    };

    return (
        <div className="flex flex-col lg:flex-row gap-8 min-h-[80vh]">
            {/* Sidebar Navigation */}
            <aside className="lg:w-64 space-y-2">
                <NavButton 
                    active={activeTab === 'dashboard'} 
                    onClick={() => setActiveTab('dashboard')}
                    icon={<LayoutDashboard size={20} />}
                    label="Dashboard"
                />
                <NavButton 
                    active={activeTab === 'games'} 
                    onClick={() => setActiveTab('games')}
                    icon={<Gamepad2 size={20} />}
                    label="Games Catalog"
                />
                <NavButton 
                    active={activeTab === 'partners'} 
                    onClick={() => setActiveTab('partners')}
                    icon={<Handshake size={20} />}
                    label="Partners"
                />
                <NavButton 
                    active={activeTab === 'users'} 
                    onClick={() => setActiveTab('users')}
                    icon={<Users size={20} />}
                    label="Users"
                />
            </aside>

            {/* Main Content */}
            <main className="flex-1 bg-slate-900/40 border border-slate-800 rounded-3xl p-6 lg:p-8 relative overflow-hidden backdrop-blur-xl">
                <AnimatePresence mode="wait">
                    {message && (
                        <motion.div 
                            initial={{ y: -50, opacity: 0 }}
                            animate={{ y: 0, opacity: 1 }}
                            exit={{ y: -50, opacity: 0 }}
                            className={`absolute top-6 right-6 z-50 flex items-center gap-3 px-4 py-3 rounded-xl border shadow-2xl ${
                                message.type === 'success' ? 'bg-green-500/10 text-green-400 border-green-500/20' : 'bg-red-500/10 text-red-400 border-red-500/20'
                            }`}
                        >
                            {message.type === 'success' ? <CheckCircle2 size={18}/> : <AlertCircle size={18}/>}
                            <span className="text-sm font-bold">{message.text}</span>
                        </motion.div>
                    )}
                </AnimatePresence>

                <AnimatePresence mode="wait">
                    {activeTab === 'dashboard' && (
                        <TabWrapper key="dashboard">
                            <h2 className="text-2xl font-black mb-8 italic uppercase tracking-tight">System Overview</h2>
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 mb-8">
                                <StatCard icon={<Gamepad2 />} label="Total Games" value={stats.games} color="text-brand-500" />
                                <StatCard icon={<Handshake />} label="Active Partners" value={stats.partners} color="text-yellow-500" />
                            </div>
                            <div className="p-6 rounded-2xl bg-slate-950/30 border border-slate-800">
                                <div className="flex items-center gap-3 mb-4">
                                    <Activity className="h-5 w-5 text-brand-500" />
                                    <h3 className="font-bold">Health Monitor</h3>
                                </div>
                                <div className="space-y-4">
                                    <HealthRow label="Backend API" status="Operational" />
                                    <HealthRow label="Kafka Broker" status="Operational" />
                                </div>
                            </div>
                        </TabWrapper>
                    )}

                    {activeTab === 'games' && (
                        <TabWrapper key="games">
                            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8">
                                <h2 className="text-2xl font-black italic uppercase tracking-tight">Games Catalog</h2>
                                <div className="flex gap-2 w-full sm:w-auto">
                                    <button 
                                        onClick={() => { setEditingGame(null); setIsBulkUpload(true); setShowGameForm(true); }}
                                        className="flex-1 sm:flex-none flex items-center justify-center gap-2 px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-xl transition-colors border border-slate-700 font-bold text-sm"
                                    >
                                        <Upload size={16} /> Bulk CSV
                                    </button>
                                    <button 
                                        onClick={() => { setEditingGame(null); setIsBulkUpload(false); setShowGameForm(true); }}
                                        className="flex-1 sm:flex-none flex items-center justify-center gap-2 px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-xl transition-all shadow-lg shadow-brand-500/20 font-bold text-sm"
                                    >
                                        <Plus size={16} /> Add Game
                                    </button>
                                </div>
                            </div>

                            <div className="relative mb-6">
                                <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500" size={18} />
                                <input 
                                    type="text" 
                                    placeholder="Search in catalog..."
                                    className="w-full bg-slate-950/50 border border-slate-800 rounded-12 pl-12 pr-4 py-3 outline-none focus:border-brand-500 transition-colors"
                                    value={gameFilters.q}
                                    onChange={(e) => setGameFilters({...gameFilters, q: e.target.value})}
                                    onKeyDown={(e) => e.key === 'Enter' && fetchGames()}
                                />
                            </div>

                            <div className="overflow-x-auto">
                                <table className="w-full text-left border-separate border-spacing-y-2">
                                    <thead>
                                        <tr className="text-slate-500 text-[10px] uppercase font-black tracking-widest px-4">
                                            <th className="pb-4 pl-4">Title</th>
                                            <th className="pb-4">Release</th>
                                            <th className="pb-4">Platforms</th>
                                            <th className="pb-4 text-right pr-4">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody className="text-sm">
                                        {loading ? (
                                            <tr><td colSpan="4" className="text-center py-10"><Loader2 className="animate-spin inline-block mr-2" /> Loading catalog...</td></tr>
                                        ) : games.length === 0 ? (
                                            <tr><td colSpan="4" className="text-center py-10 text-slate-500">No games found.</td></tr>
                                        ) : games.map(game => (
                                            <tr key={game.id} className="bg-slate-950/20 hover:bg-slate-950/40 transition-colors group">
                                                <td className="py-4 pl-4 rounded-l-2xl font-bold text-slate-200">{game.title}</td>
                                                <td className="py-4 text-slate-400">{game.releaseYear}</td>
                                                <td className="py-4">
                                                    <div className="flex gap-1">
                                                        {game.platforms?.slice(0, 2).map((p, i) => (
                                                            <span key={i} className="px-2 py-0.5 bg-slate-800 rounded-md text-[10px] text-slate-400">{p}</span>
                                                        ))}
                                                        {game.platforms?.length > 2 && <span className="text-[10px] text-slate-600">+{game.platforms.length - 2}</span>}
                                                    </div>
                                                </td>
                                                <td className="py-4 pr-4 rounded-r-2xl text-right">
                                                    <div className="flex justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                                        <button 
                                                            onClick={() => { setEditingGame(game); setIsBulkUpload(false); setShowGameForm(true); }}
                                                            className="p-2 hover:text-brand-500 transition-colors"
                                                        >
                                                            <Edit size={16} />
                                                        </button>
                                                        <button 
                                                            onClick={() => handleDeleteGame(game.id)}
                                                            className="p-2 hover:text-red-500 transition-colors"
                                                        >
                                                            <Trash2 size={16} />
                                                        </button>
                                                    </div>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                            
                            {totalPages > 1 && (
                                <div className="flex justify-center gap-2 mt-6">
                                    <button 
                                        disabled={gameFilters.page === 0}
                                        onClick={() => setGameFilters({...gameFilters, page: gameFilters.page - 1})}
                                        className="px-4 py-2 rounded-xl bg-slate-800 disabled:opacity-30"
                                    >
                                        Prev
                                    </button>
                                    <span className="px-4 py-2 text-slate-500">Page {gameFilters.page + 1} of {totalPages}</span>
                                    <button 
                                        disabled={gameFilters.page >= totalPages - 1}
                                        onClick={() => setGameFilters({...gameFilters, page: gameFilters.page + 1})}
                                        className="px-4 py-2 rounded-xl bg-slate-800 disabled:opacity-30"
                                    >
                                        Next
                                    </button>
                                </div>
                            )}
                        </TabWrapper>
                    )}

                    {activeTab === 'partners' && (
                        <TabWrapper key="partners">
                            <div className="flex justify-between items-center mb-8">
                                <h2 className="text-2xl font-black italic uppercase tracking-tight">Partners Management</h2>
                                <button 
                                    onClick={() => setShowPartnerForm(true)}
                                    className="flex items-center gap-2 px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-xl transition-all shadow-lg shadow-brand-500/20 font-bold text-sm"
                                >
                                    <Plus size={16} /> Register Partner
                                </button>
                            </div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                {loading && partners.length === 0 ? (
                                    <div className="col-span-full text-center py-10 text-slate-500"><Loader2 className="animate-spin inline-block" /></div>
                                ) : partners.map(partner => (
                                    <div key={partner.id} className="p-4 rounded-2xl bg-slate-950/30 border border-slate-800 flex items-center justify-between">
                                        <div className="flex items-center gap-4">
                                            <div className="h-10 w-10 rounded-full bg-slate-800 flex items-center justify-center text-brand-500 font-black">
                                                {partner.name?.charAt(0).toUpperCase()}
                                            </div>
                                            <div>
                                                <h4 className="font-bold text-slate-200">{partner.name}</h4>
                                                <p className="text-xs text-slate-500">UUID: {partner.id.substring(0, 8)}...</p>
                                            </div>
                                        </div>
                                        <span className={`px-2 py-0.5 rounded-full text-[9px] font-black uppercase ${partner.active ? 'bg-green-500/10 text-green-500' : 'bg-red-500/10 text-red-500'}`}>
                                            {partner.active ? 'Active' : 'Inactive'}
                                        </span>
                                    </div>
                                ))}
                            </div>
                        </TabWrapper>
                    )}

                    {activeTab === 'users' && (
                        <TabWrapper key="users">
                            <h2 className="text-2xl font-black italic uppercase tracking-tight mb-8">User Directory</h2>
                            <div className="p-12 text-center rounded-3xl bg-slate-950/20 border border-dashed border-slate-800">
                                <Users size={48} className="mx-auto mb-4 text-slate-700" />
                                <p className="text-slate-500 max-w-xs mx-auto font-medium">L'intégration LDAP/User Management est en cours de développement pour la V2.</p>
                                <div className="mt-8 flex flex-col items-center gap-4">
                                    <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 w-full max-w-sm flex items-center gap-4">
                                        <div className="h-12 w-12 rounded-2xl bg-brand-600 flex items-center justify-center text-white font-black text-xl shadow-lg shadow-brand-500/20">A</div>
                                        <div className="text-left">
                                            <h4 className="font-black text-slate-100">Administrator</h4>
                                            <p className="text-xs text-slate-500">Super User (In-Memory)</p>
                                        </div>
                                        <span className="ml-auto px-2 py-1 bg-brand-500/10 text-brand-500 text-[8px] font-black uppercase rounded border border-brand-500/20">Master</span>
                                    </div>
                                </div>
                            </div>
                        </TabWrapper>
                    )}
                </AnimatePresence>
            </main>

            {/* Modals / Forms */}
            <AnimatePresence>
                {showGameForm && (
                    <Modal onClose={() => setShowGameForm(false)} title={isBulkUpload ? "Bulk Catalog Import" : (editingGame ? "Edit Game" : "Add New Game")}>
                        {isBulkUpload ? (
                            <BulkUploadForm onComplete={() => { setShowGameForm(false); fetchGames(); }} notify={notify} />
                        ) : (
                            <GameForm 
                                initialData={editingGame} 
                                onComplete={() => { setShowGameForm(false); fetchGames(); }} 
                                notify={notify}
                            />
                        )}
                    </Modal>
                )}

                {showPartnerForm && (
                    <Modal onClose={() => setShowPartnerForm(false)} title="Register New Partner">
                        <PartnerForm 
                            onComplete={(res) => { setShowPartnerForm(false); fetchPartners(); }} 
                            notify={notify}
                        />
                    </Modal>
                )}
            </AnimatePresence>
        </div>
    );
};

/* --- UI Components --- */

const NavButton = ({ active, onClick, icon, label }) => (
    <button 
        onClick={onClick}
        className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all font-bold text-sm ${
            active 
            ? 'bg-brand-600 text-white shadow-lg shadow-brand-600/20' 
            : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200'
        }`}
    >
        {icon}
        {label}
    </button>
);

const TabWrapper = ({ children }) => (
    <motion.div
        initial={{ opacity: 0, x: 20 }}
        animate={{ opacity: 1, x: 0 }}
        exit={{ opacity: 0, x: -20 }}
        transition={{ duration: 0.2 }}
    >
        {children}
    </motion.div>
);

const StatCard = ({ icon, label, value, color }) => (
    <div className="p-6 rounded-2xl bg-slate-950/40 border border-slate-800 shadow-xl">
        <div className={`p-2 w-fit rounded-lg bg-slate-900 border border-slate-800 mb-4 ${color}`}>
            {icon}
        </div>
        <div className="text-3xl font-black text-white mb-1">{value}</div>
        <div className="text-xs text-slate-500 uppercase font-black tracking-widest">{label}</div>
    </div>
);

const HealthRow = ({ label, status }) => (
    <div className="flex items-center justify-between py-2 border-b border-slate-800/50 last:border-0">
        <span className="text-sm text-slate-400">{label}</span>
        <span className={`text-[9px] font-black uppercase px-2 py-0.5 rounded ${
            status === 'Operational' ? 'bg-green-500/10 text-green-500' : 'bg-slate-800 text-slate-500'
        }`}>
            {status}
        </span>
    </div>
);

const Modal = ({ children, onClose, title }) => (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
        <motion.div 
            initial={{ opacity: 0 }} 
            animate={{ opacity: 1 }} 
            exit={{ opacity: 0 }}
            onClick={onClose}
            className="absolute inset-0 bg-slate-950/80 backdrop-blur-sm"
        />
        <motion.div 
            initial={{ scale: 0.9, opacity: 0, y: 20 }}
            animate={{ scale: 1, opacity: 1, y: 0 }}
            exit={{ scale: 0.9, opacity: 0, y: 20 }}
            className="relative w-full max-w-xl bg-slate-900 border border-slate-800 rounded-3xl shadow-2xl overflow-hidden"
        >
            <div className="flex items-center justify-between p-6 border-b border-slate-800 bg-slate-900/50">
                <h3 className="text-xl font-black italic uppercase italic tracking-tight">{title}</h3>
                <button onClick={onClose} className="p-2 hover:bg-slate-800 rounded-lg text-slate-400"><X size={20} /></button>
            </div>
            <div className="p-6 max-h-[80vh] overflow-y-auto">
                {children}
            </div>
        </motion.div>
    </div>
);

/* --- Form Components --- */

const GameForm = ({ initialData, onComplete, notify }) => {
    const [formData, setFormData] = useState(initialData || {
        title: '',
        releaseYear: new Date().getFullYear(),
        publisher: '',
        description: '',
        coverUrl: '',
        rating: 5.0,
        genres: [],
        platforms: [],
        tags: []
    });
    const [submitting, setSubmitting] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSubmitting(true);
        try {
            if (initialData?.id) {
                await gameService.updateGame(initialData.id, formData);
                notify('success', 'Game updated successfully');
            } else {
                await gameService.createGame(formData);
                notify('success', 'Game created successfully');
            }
            onComplete();
        } catch (error) {
            notify('error', 'Operation failed: ' + (error.response?.data?.message || error.message));
        }
        setSubmitting(false);
    };

    const handleListChange = (key, value) => {
        const list = value.split(',').map(s => s.trim()).filter(s => s !== '');
        setFormData({ ...formData, [key]: list });
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
                <Input label="Game Title" value={formData.title} onChange={v => setFormData({...formData, title: v})} required />
                <Input label="Release Year" type="number" value={formData.releaseYear} onChange={v => setFormData({...formData, releaseYear: parseInt(v)})} required />
            </div>
            <div className="grid grid-cols-2 gap-4">
                <Input label="Publisher" value={formData.publisher} onChange={v => setFormData({...formData, publisher: v})} />
                <Input label="Rating (0-10)" type="number" step="0.1" value={formData.rating} onChange={v => setFormData({...formData, rating: parseFloat(v)})} />
            </div>
            <Input label="Cover URL" value={formData.coverUrl} onChange={v => setFormData({...formData, coverUrl: v})} />
            <Input label="Genres (comma separated)" value={formData.genres.join(', ')} onChange={v => handleListChange('genres', v)} placeholder="RPG, Action, Indié" />
            <Input label="Platforms (comma separated)" value={formData.platforms.join(', ')} onChange={v => handleListChange('platforms', v)} placeholder="PC, PS5, Xbox Series" />
            <Input label="Tags (comma separated)" value={formData.tags.join(', ')} onChange={v => handleListChange('tags', v)} />
            <div>
                <label className="block text-[10px] font-black uppercase text-slate-500 mb-1.5 ml-1">Description</label>
                <textarea 
                    className="w-full bg-slate-950/50 border border-slate-800 rounded-xl p-3 outline-none focus:border-brand-500 min-h-[100px] text-sm"
                    value={formData.description}
                    onChange={e => setFormData({...formData, description: e.target.value})}
                />
            </div>
            <button 
                type="submit" 
                disabled={submitting}
                className="w-full py-3 bg-brand-600 hover:bg-brand-500 text-white rounded-xl font-bold flex items-center justify-center gap-2 transition-all shadow-xl shadow-brand-500/20 disabled:opacity-50"
            >
                {submitting ? <Loader2 size={18} className="animate-spin" /> : <><Save size={18} /> Save Game</>}
            </button>
        </form>
    );
};

const BulkUploadForm = ({ onComplete, notify }) => {
    const [file, setFile] = useState(null);
    const [uploading, setUploading] = useState(false);
    const [results, setResults] = useState(null);

    const handleUpload = async () => {
        if (!file) return;
        setUploading(true);
        try {
            const { data } = await partnerService.bulkImport(file);
            setResults(data);
            notify('success', `Import complete: ${data.successful} success, ${data.failed} fails`);
        } catch (error) {
            notify('error', 'Upload failed: ' + (error.response?.data?.message || error.message));
        }
        setUploading(false);
    };

    return (
        <div className="space-y-6">
            {!results ? (
                <>
                    <div className="p-8 border-2 border-dashed border-slate-800 rounded-3xl text-center bg-slate-950/20 group hover:border-brand-500/50 transition-colors cursor-pointer relative">
                        <input 
                            type="file" 
                            accept=".csv,.json"
                            onChange={e => setFile(e.target.files[0])}
                            className="absolute inset-0 opacity-0 cursor-pointer"
                        />
                        <div className="space-y-2">
                            <div className="mx-auto w-12 h-12 rounded-2xl bg-slate-900 border border-slate-800 flex items-center justify-center text-slate-500 group-hover:text-brand-500 group-hover:scale-110 transition-all">
                                <Plus size={24} />
                            </div>
                            <p className="text-sm font-bold text-slate-400">
                                {file ? file.name : "Cliquez ou glissez un fichier .csv ou .json"}
                            </p>
                            <p className="text-[10px] text-slate-600 uppercase font-bold tracking-widest">Format CSV supporté avec en-têtes</p>
                        </div>
                    </div>
                    <button 
                        onClick={handleUpload}
                        disabled={!file || uploading}
                        className="w-full py-3 bg-brand-600 hover:bg-brand-500 text-white rounded-xl font-bold flex items-center justify-center gap-2 transition-all disabled:opacity-50"
                    >
                        {uploading ? <Loader2 size={18} className="animate-spin" /> : <><Upload size={18} /> Start Ingestion</>}
                    </button>
                    <div className="p-4 bg-yellow-500/5 border border-yellow-500/10 rounded-xl">
                        <p className="text-[10px] text-yellow-500/80 font-medium leading-relaxed uppercase tracking-wide">
                            Note: Le fichier sera ingéré asynchronement via le pipeline Kafka. Vous pourrez suivre l'état global dans le catalogue après rafraîchissement.
                        </p>
                    </div>
                </>
            ) : (
                <div className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        <div className="p-4 bg-green-500/10 rounded-2xl border border-green-500/20 text-center">
                            <div className="text-2xl font-black text-green-400">{results.successful}</div>
                            <div className="text-[10px] uppercase font-black text-green-500/60 tracking-widest">Successful</div>
                        </div>
                        <div className="p-4 bg-red-500/10 rounded-2xl border border-red-500/20 text-center">
                            <div className="text-2xl font-black text-red-400">{results.failed}</div>
                            <div className="text-[10px] uppercase font-black text-red-500/60 tracking-widest">Failed</div>
                        </div>
                    </div>
                    {results.errors?.length > 0 && (
                        <div className="mt-4">
                            <h4 className="text-[10px] font-black text-slate-500 uppercase mb-2">Error Log</h4>
                            <div className="max-h-40 overflow-y-auto bg-slate-950 rounded-xl p-3 border border-slate-800 space-y-1">
                                {results.errors.map((err, i) => (
                                    <p key={i} className="text-[11px] text-red-400/80 font-mono">{err}</p>
                                ))}
                            </div>
                        </div>
                    )}
                    <button onClick={onComplete} className="w-full py-3 bg-slate-800 hover:bg-slate-700 text-white rounded-xl font-bold transition-all mt-4">Close & Refresh</button>
                </div>
            )}
        </div>
    );
};

const PartnerForm = ({ onComplete, notify }) => {
    const [name, setName] = useState('');
    const [submitting, setSubmitting] = useState(false);
    const [result, setResult] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSubmitting(true);
        try {
            const { data } = await partnerService.register({ name });
            setResult(data);
            notify('success', 'Partner registered');
        } catch (error) {
            notify('error', 'Registration failed');
        }
        setSubmitting(false);
    };

    return (
        <div className="space-y-4">
            {!result ? (
                <form onSubmit={handleSubmit} className="space-y-4">
                    <Input label="Partner Name" value={name} onChange={setName} required placeholder="Ex: Sony Interactive" />
                    <button 
                        type="submit" 
                        disabled={submitting || !name}
                        className="w-full py-3 bg-brand-600 hover:bg-brand-500 text-white rounded-xl font-bold flex items-center justify-center gap-2 transition-all disabled:opacity-50"
                    >
                        {submitting ? <Loader2 size={18} className="animate-spin" /> : "Generate API Key"}
                    </button>
                </form>
            ) : (
                <div className="space-y-6 animate-in fade-in zoom-in duration-300">
                    <div className="text-center p-4 bg-green-500/10 border border-green-500/20 rounded-2xl">
                        <CheckCircle2 size={32} className="mx-auto text-green-500 mb-2" />
                        <h4 className="font-bold text-green-400">Registration Success!</h4>
                    </div>
                    <div className="space-y-2">
                        <label className="text-[10px] font-black text-slate-500 uppercase tracking-widest ml-1">Universal API Key</label>
                        <div className="p-4 bg-slate-950 rounded-2xl border border-brand-500/30 text-brand-400 font-mono text-xs break-all shadow-inner">
                            {result.apiKey}
                        </div>
                        <p className="text-[9px] text-red-500/60 font-black uppercase text-center mt-2 leading-relaxed">
                            Avertissement : Cette clé ne sera plus jamais affichée. <br/> Copiez-la maintenant avec précaution.
                        </p>
                    </div>
                    <button onClick={() => onComplete(result)} className="w-full py-3 bg-slate-800 hover:bg-slate-700 text-white rounded-xl font-bold transition-all">Terminer</button>
                </div>
            )}
        </div>
    );
};

/* --- Helper Components --- */

const Input = ({ label, type = "text", value, onChange, required, placeholder, ...props }) => (
    <div className="space-y-1.5 flex-1">
        <label className="block text-[10px] font-black uppercase text-slate-500 ml-1">{label}</label>
        <input 
            type={type}
            value={value}
            onChange={e => onChange(e.target.value)}
            required={required}
            placeholder={placeholder}
            className="w-full bg-slate-950/50 border border-slate-800 rounded-xl px-4 py-2.5 outline-none focus:border-brand-500 transition-colors text-sm"
            {...props}
        />
    </div>
);

export default AdminPage;

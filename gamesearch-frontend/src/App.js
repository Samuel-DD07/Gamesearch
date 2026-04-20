import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import HomePage from './pages/HomePage';
import GameDetailsPage from './pages/GameDetailsPage';
import AdminPage from './pages/AdminPage';
import LoginPage from './pages/LoginPage';
import { authService } from './services/api';

const ProtectedRoute = ({ children }) => {
    if (!authService.isLoggedIn()) {
        return <Navigate to="/login" replace />;
    }
    return children;
};

const Navbar = () => {
    const location = useLocation(); // Trigger re-render on navigation
    
    return (
        <nav className="sticky top-0 z-50 border-b border-slate-800 bg-slate-950/80 backdrop-blur-md">
            <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
                <div className="flex h-16 items-center justify-between">
                    <div className="flex items-center gap-2">
                        <div className="h-8 w-8 rounded-lg bg-gradient-to-tr from-brand-600 to-brand-400 flex items-center justify-center font-black text-white">G</div>
                        <span className="text-xl font-bold tracking-tight bg-gradient-to-r from-white to-slate-400 bg-clip-text text-transparent">
                            GameSearch
                        </span>
                    </div>
                    <div className="hidden sm:block">
                        <div className="flex space-x-4">
                            <a href="/" className="px-3 py-2 text-sm font-medium text-slate-300 hover:text-white transition-colors">Home</a>
                            {authService.isLoggedIn() ? (
                                <div className="flex items-center gap-4">
                                    <a href="/admin" className="px-3 py-2 text-sm font-medium text-slate-300 hover:text-white transition-colors">Admin</a>
                                    <button 
                                        onClick={() => { authService.logout(); window.location.href = '/'; }}
                                        className="px-3 py-2 text-sm font-medium text-red-400 hover:text-red-300 transition-colors"
                                    >
                                        Logout
                                    </button>
                                </div>
                            ) : (
                                <a href="/login" className="px-3 py-2 text-sm font-medium text-slate-400 hover:text-white transition-colors">Admin</a>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </nav>
    );
};

function App() {
    return (
        <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
            <div className="min-h-screen bg-slate-950 text-slate-100 selection:bg-brand-500/30">
                <Navbar />
                <main className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
                    <Routes>
                        <Route path="/" element={<HomePage />} />
                        <Route path="/games/:id" element={<GameDetailsPage />} />
                        <Route path="/login" element={<LoginPage />} />
                        <Route path="/admin" element={<ProtectedRoute><AdminPage /></ProtectedRoute>} />
                    </Routes>
                </main>
            </div>
        </Router>
    );
}

export default App;

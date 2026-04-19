import axios from 'axios';

// Par défaut, l'application tape sur le même nom de domaine (relatif)
// Le /api sera intercepté par Nginx (en prod) ou http-proxy-middleware (en dev)
const API_URL = process.env.REACT_APP_API_URL || '/api';

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export const authService = {
    login: (username, password) => api.post('/auth/login', { username, password }),
    logout: () => localStorage.removeItem('token'),
    getToken: () => localStorage.getItem('token'),
    isLoggedIn: () => !!localStorage.getItem('token'),
};

export const gameService = {
    getGames: (page = 0, size = 12, filters = {}) => {
        const params = { ...filters, page, size };
        return api.get('/games', { params });
    },
    getGameDetails: (id) => api.get(`/games/${id}`),
    getRecentGames: () => api.get('/games/recent'),
    getPopularGames: () => api.get('/games/popular'),
};

export const partnerService = {
    register: (data) => api.post('/partner/register', data),
    submitGame: (data) => api.post('/partner/games', data),
    getIngestionStatus: (gameId) => api.get(`/partner/ingestions/${gameId}`),
};

export default api;

// Centralized configuration for API URLs
// Ensure your .env file has VITE_API_BASE_URL set, or it will default to localhost

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

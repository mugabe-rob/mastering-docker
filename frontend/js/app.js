/**
 * Main Application Module
 * Coordinates between API and UI, handles application lifecycle
 */

class LibraryApp {
    constructor() {
        this.initialized = false;
        this.autoRefreshInterval = null;
        this.init();
    }

    async init() {
        try {
            // Wait for DOM to be fully loaded
            if (document.readyState === 'loading') {
                document.addEventListener('DOMContentLoaded', () => this.initialize());
            } else {
                await this.initialize();
            }
        } catch (error) {
            console.error('Failed to initialize application:', error);
            ui.showToast('Failed to initialize application', 'error');
        }
    }

    async initialize() {
        console.log('Initializing Library Management System...');
        
        try {
            // Show loading
            ui.showLoading();

            // Check API connection
            const isConnected = await libraryAPI.checkConnection();
            
            if (isConnected) {
                ui.showToast('Connected to backend API', 'success');
            } else {
                ui.showToast('Running in offline mode with sample data', 'warning');
            }

            // Load initial dashboard data
            await ui.loadDashboardData();

            // Setup additional event listeners
            this.setupAdditionalEventListeners();

            // Setup auto-refresh for real-time updates
            this.setupAutoRefresh();

            // Setup keyboard shortcuts
            this.setupKeyboardShortcuts();

            // Mark as initialized
            this.initialized = true;
            
            console.log('Library Management System initialized successfully');
            
        } catch (error) {
            console.error('Initialization error:', error);
            ui.showToast('Initialization failed', 'error');
        } finally {
            ui.hideLoading();
        }
    }

    setupAdditionalEventListeners() {
        // Handle page visibility changes for auto-refresh
        document.addEventListener('visibilitychange', () => {
            if (!document.hidden && this.initialized) {
                this.refreshCurrentPageData();
            }
        });

        // Handle window focus for data refresh
        window.addEventListener('focus', () => {
            if (this.initialized) {
                this.refreshCurrentPageData();
            }
        });

        // Handle browser back/forward buttons
        window.addEventListener('popstate', (event) => {
            if (event.state && event.state.page) {
                ui.showPage(event.state.page);
            }
        });

        // Add click handlers for dynamic buttons
        document.addEventListener('click', (event) => {
            this.handleDynamicClicks(event);
        });

        // Add form validation on input
        document.addEventListener('input', (event) => {
            if (event.target.tagName === 'INPUT' || event.target.tagName === 'SELECT') {
                ui.clearError(event.target);
            }
        });

        // Add Enter key support for buttons
        document.addEventListener('keydown', (event) => {
            if (event.key === 'Enter' && event.target.type === 'button') {
                event.target.click();
            }
        });
    }

    setupAutoRefresh() {
        // Refresh data every 30 seconds when page is visible
        this.autoRefreshInterval = setInterval(() => {
            if (!document.hidden && this.initialized) {
                this.refreshCurrentPageData();
            }
        }, 30000);
    }

    setupKeyboardShortcuts() {
        document.addEventListener('keydown', (event) => {
            // Only handle shortcuts when not typing in inputs
            if (event.target.tagName === 'INPUT' || event.target.tagName === 'TEXTAREA') {
                return;
            }

            // Ctrl/Cmd + combinations
            if (event.ctrlKey || event.metaKey) {
                switch (event.key) {
                    case '1':
                        event.preventDefault();
                        ui.showPage('dashboard');
                        break;
                    case '2':
                        event.preventDefault();
                        ui.showPage('books');
                        break;
                    case '3':
                        event.preventDefault();
                        ui.showPage('members');
                        break;
                    case '4':
                        event.preventDefault();
                        ui.showPage('transactions');
                        break;
                    case '5':
                        event.preventDefault();
                        ui.showPage('reports');
                        break;
                    case 'n':
                        event.preventDefault();
                        ui.openModal('addBookModal');
                        break;
                    case 'm':
                        event.preventDefault();
                        ui.openModal('addMemberModal');
                        break;
                }
            }

            // Escape key to close modals
            if (event.key === 'Escape') {
                const activeModal = document.querySelector('.modal.active');
                if (activeModal) {
                    ui.closeModal(activeModal.id);
                }
            }
        });
    }

    handleDynamicClicks(event) {
        const target = event.target;
        
        // Handle button clicks that might be dynamically generated
        if (target.matches('.btn[onclick]')) {
            // Let the onclick handler execute
            return;
        }

        // Handle row clicks in tables
        if (target.closest('tr') && target.closest('.data-table')) {
            const row = target.closest('tr');
            const table = target.closest('.data-table');
            
            // Add visual feedback for row selection
            table.querySelectorAll('tr').forEach(r => r.classList.remove('selected'));
            row.classList.add('selected');
        }
    }

    async refreshCurrentPageData() {
        try {
            await ui.loadPageData(ui.currentPage);
        } catch (error) {
            console.error('Failed to refresh page data:', error);
        }
    }

    // Public methods for external use

    async refreshAllData() {
        try {
            ui.showLoading();
            await Promise.all([
                ui.loadDashboardData(),
                ui.loadBooksData(),
                ui.loadMembersData(),
                ui.loadTransactionsData(),
                ui.loadReportsData()
            ]);
            ui.showToast('All data refreshed', 'success');
        } catch (error) {
            ui.showToast('Failed to refresh data', 'error');
        } finally {
            ui.hideLoading();
        }
    }

    // Export/Import functionality
    async exportData() {
        try {
            ui.showLoading();
            
            const [booksResponse, membersResponse, statsResponse] = await Promise.all([
                libraryAPI.getAllBooks(),
                libraryAPI.getAllMembers(),
                libraryAPI.getStatistics()
            ]);

            const exportData = {
                timestamp: new Date().toISOString(),
                books: booksResponse.data,
                members: membersResponse.data,
                statistics: statsResponse.data
            };

            const dataStr = JSON.stringify(exportData, null, 2);
            const dataBlob = new Blob([dataStr], { type: 'application/json' });
            const url = URL.createObjectURL(dataBlob);
            
            const link = document.createElement('a');
            link.href = url;
            link.download = `library_data_${new Date().toISOString().split('T')[0]}.json`;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            URL.revokeObjectURL(url);

            ui.showToast('Data exported successfully', 'success');
        } catch (error) {
            ui.showToast('Export failed', 'error');
        } finally {
            ui.hideLoading();
        }
    }

    // Performance monitoring
    measurePerformance(operationName, fn) {
        return async (...args) => {
            const startTime = performance.now();
            try {
                const result = await fn(...args);
                const endTime = performance.now();
                console.log(`${operationName} took ${endTime - startTime} milliseconds`);
                return result;
            } catch (error) {
                const endTime = performance.now();
                console.error(`${operationName} failed after ${endTime - startTime} milliseconds:`, error);
                throw error;
            }
        };
    }

    // Error handling and recovery
    handleError(error, context = '') {
        console.error(`Error in ${context}:`, error);
        
        // Show user-friendly error messages
        let message = 'An unexpected error occurred';
        
        if (error.message.includes('fetch')) {
            message = 'Network error - please check your connection';
        } else if (error.message.includes('not found')) {
            message = 'The requested item was not found';
        } else if (error.message.includes('validation')) {
            message = 'Please check your input and try again';
        }
        
        ui.showToast(message, 'error');
    }

    // Utility methods
    formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    generateReport() {
        // This could generate a printable report
        window.print();
    }

    // Cleanup on page unload
    cleanup() {
        if (this.autoRefreshInterval) {
            clearInterval(this.autoRefreshInterval);
        }
    }
}

// Global error handler
window.addEventListener('error', (event) => {
    console.error('Global error:', event.error);
    if (window.app) {
        window.app.handleError(event.error, 'Global');
    }
});

// Unhandled promise rejection handler
window.addEventListener('unhandledrejection', (event) => {
    console.error('Unhandled promise rejection:', event.reason);
    if (window.app) {
        window.app.handleError(event.reason, 'Promise');
    }
    event.preventDefault();
});

// Cleanup on page unload
window.addEventListener('beforeunload', () => {
    if (window.app) {
        window.app.cleanup();
    }
});

// Initialize application
const app = new LibraryApp();

// Make app globally available for debugging
window.app = app;

// Add some developer tools for debugging
if (typeof window !== 'undefined') {
    window.debugTools = {
        refreshData: () => app.refreshAllData(),
        exportData: () => app.exportData(),
        showStats: () => console.table(libraryAPI.mockData.statistics),
        showBooks: () => console.table(libraryAPI.mockData.books),
        showMembers: () => console.table(libraryAPI.mockData.members),
        toggleAPI: () => {
            libraryAPI.isOnline = !libraryAPI.isOnline;
            console.log('API mode:', libraryAPI.isOnline ? 'Online' : 'Offline');
        }
    };
    
    console.log('Debug tools available: window.debugTools');
    console.log('Available commands:');
    console.log('- refreshData(): Refresh all data');
    console.log('- exportData(): Export data to file');
    console.log('- showStats(): Show statistics in table');
    console.log('- showBooks(): Show books in table');
    console.log('- showMembers(): Show members in table');
    console.log('- toggleAPI(): Toggle between online/offline mode');
}

/**
 * UI Management Module
 * Handles all UI interactions, animations, and user feedback
 */

class UIManager {
    constructor() {
        this.currentPage = 'dashboard';
        this.loadingCount = 0;
        this.initializeEventListeners();
    }

    // Initialize all event listeners
    initializeEventListeners() {
        // Navigation
        this.setupNavigation();
        
        // Modal controls
        this.setupModals();
        
        // Mobile menu toggle
        this.setupMobileMenu();
        
        // Quick action buttons
        this.setupQuickActions();
        
        // Form submissions
        this.setupForms();
        
        // Search functionality
        this.setupSearch();
        
        // Filter functionality
        this.setupFilters();
    }

    // Setup navigation
    setupNavigation() {
        const navLinks = document.querySelectorAll('.nav-link');
        
        navLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const targetPage = link.getAttribute('data-page');
                this.showPage(targetPage);
                
                // Update active nav link
                navLinks.forEach(l => l.classList.remove('active'));
                link.classList.add('active');
                
                // Close mobile menu if open
                this.closeMobileMenu();
            });
        });
    }

    // Setup modal controls
    setupModals() {
        // Modal close buttons
        document.querySelectorAll('.close, [data-modal]').forEach(element => {
            element.addEventListener('click', (e) => {
                const modalId = element.getAttribute('data-modal');
                if (modalId) {
                    this.closeModal(modalId);
                }
            });
        });

        // Close modal when clicking outside
        document.querySelectorAll('.modal').forEach(modal => {
            modal.addEventListener('click', (e) => {
                if (e.target === modal) {
                    this.closeModal(modal.id);
                }
            });
        });

        // Prevent modal content clicks from closing modal
        document.querySelectorAll('.modal-content').forEach(content => {
            content.addEventListener('click', (e) => {
                e.stopPropagation();
            });
        });
    }

    // Setup mobile menu
    setupMobileMenu() {
        const navToggle = document.getElementById('navToggle');
        const navMenu = document.getElementById('navMenu');
        
        if (navToggle) {
            navToggle.addEventListener('click', () => {
                navMenu.classList.toggle('active');
            });
        }
    }

    // Setup quick action buttons
    setupQuickActions() {
        document.querySelectorAll('.action-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                const action = btn.getAttribute('data-action');
                this.handleQuickAction(action);
            });
        });
    }

    // Setup form submissions
    setupForms() {
        // Add Book Form
        const addBookForm = document.getElementById('addBookForm');
        if (addBookForm) {
            addBookForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleAddBook();
            });
        }

        // Add Member Form
        const addMemberForm = document.getElementById('addMemberForm');
        if (addMemberForm) {
            addMemberForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleAddMember();
            });
        }

        // Borrow Form
        const borrowForm = document.getElementById('borrowForm');
        if (borrowForm) {
            borrowForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleBorrowBook();
            });
        }

        // Return Form
        const returnForm = document.getElementById('returnForm');
        if (returnForm) {
            returnForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleReturnBook();
            });
        }
    }

    // Setup search functionality
    setupSearch() {
        const searchBtn = document.getElementById('searchBooksBtn');
        const searchInput = document.getElementById('bookSearchInput');
        
        if (searchBtn) {
            searchBtn.addEventListener('click', () => {
                this.handleBookSearch();
            });
        }

        if (searchInput) {
            searchInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    this.handleBookSearch();
                }
            });
        }
    }

    // Setup filter functionality
    setupFilters() {
        const bookFilter = document.getElementById('bookFilter');
        
        if (bookFilter) {
            bookFilter.addEventListener('change', () => {
                this.handleBookFilter();
            });
        }
    }

    // Page navigation
    showPage(pageId) {
        // Hide all pages
        document.querySelectorAll('.page').forEach(page => {
            page.classList.remove('active');
        });
        
        // Show target page
        const targetPage = document.getElementById(pageId);
        if (targetPage) {
            targetPage.classList.add('active');
            this.currentPage = pageId;
            
            // Load page-specific data
            this.loadPageData(pageId);
        }
    }

    // Load data for specific pages
    async loadPageData(pageId) {
        switch (pageId) {
            case 'dashboard':
                await this.loadDashboardData();
                break;
            case 'books':
                await this.loadBooksData();
                break;
            case 'members':
                await this.loadMembersData();
                break;
            case 'transactions':
                await this.loadTransactionsData();
                break;
            case 'reports':
                await this.loadReportsData();
                break;
        }
    }

    // Modal management
    openModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    }

    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('active');
            document.body.style.overflow = '';
            
            // Clear form if it exists
            const form = modal.querySelector('form');
            if (form) {
                form.reset();
            }
        }
    }

    // Mobile menu management
    closeMobileMenu() {
        const navMenu = document.getElementById('navMenu');
        if (navMenu) {
            navMenu.classList.remove('active');
        }
    }

    // Loading state management
    showLoading() {
        this.loadingCount++;
        const spinner = document.getElementById('loadingSpinner');
        if (spinner) {
            spinner.classList.add('active');
        }
    }

    hideLoading() {
        this.loadingCount = Math.max(0, this.loadingCount - 1);
        if (this.loadingCount === 0) {
            const spinner = document.getElementById('loadingSpinner');
            if (spinner) {
                spinner.classList.remove('active');
            }
        }
    }

    // Toast notifications
    showToast(message, type = 'info', duration = 5000) {
        const container = document.getElementById('toastContainer');
        if (!container) return;

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerHTML = `
            <div class="toast-content">
                <i class="fas fa-${this.getToastIcon(type)}"></i>
                <span>${message}</span>
            </div>
        `;

        container.appendChild(toast);

        // Auto remove after duration
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, duration);
    }

    getToastIcon(type) {
        switch (type) {
            case 'success': return 'check-circle';
            case 'error': return 'exclamation-circle';
            case 'warning': return 'exclamation-triangle';
            default: return 'info-circle';
        }
    }

    // Form validation
    validateForm(formElement) {
        const inputs = formElement.querySelectorAll('input[required], select[required]');
        let isValid = true;

        inputs.forEach(input => {
            if (!input.value.trim()) {
                this.highlightError(input);
                isValid = false;
            } else {
                this.clearError(input);
            }
        });

        return isValid;
    }

    highlightError(input) {
        input.style.borderColor = '#ef4444';
        input.style.boxShadow = '0 0 0 3px rgba(239, 68, 68, 0.1)';
    }

    clearError(input) {
        input.style.borderColor = '';
        input.style.boxShadow = '';
    }

    // Quick action handlers
    handleQuickAction(action) {
        switch (action) {
            case 'add-book':
                this.openModal('addBookModal');
                break;
            case 'add-member':
                this.openModal('addMemberModal');
                break;
            case 'borrow-book':
                this.showPage('transactions');
                document.getElementById('borrowMemberId').focus();
                break;
            case 'return-book':
                this.showPage('transactions');
                document.getElementById('returnMemberId').focus();
                break;
        }
    }

    // Data loading methods
    async loadDashboardData() {
        try {
            this.showLoading();
            const response = await libraryAPI.getStatistics();
            
            if (response.success) {
                this.updateDashboardStats(response.data);
            }
            
            // Update system time
            this.updateSystemTime();
        } catch (error) {
            this.showToast('Failed to load dashboard data', 'error');
        } finally {
            this.hideLoading();
        }
    }

    async loadBooksData() {
        try {
            this.showLoading();
            const filter = document.getElementById('bookFilter')?.value || 'all';
            const response = await libraryAPI.getAllBooks(filter);
            
            if (response.success) {
                this.updateBooksTable(response.data);
            }
        } catch (error) {
            this.showToast('Failed to load books data', 'error');
        } finally {
            this.hideLoading();
        }
    }

    async loadMembersData() {
        try {
            this.showLoading();
            const response = await libraryAPI.getAllMembers();
            
            if (response.success) {
                this.updateMembersTable(response.data);
            }
        } catch (error) {
            this.showToast('Failed to load members data', 'error');
        } finally {
            this.hideLoading();
        }
    }

    async loadTransactionsData() {
        try {
            this.showLoading();
            const response = await libraryAPI.getBorrowedBooks();
            
            if (response.success) {
                this.updateBorrowingsTable(response.data);
            }
        } catch (error) {
            this.showToast('Failed to load transactions data', 'error');
        } finally {
            this.hideLoading();
        }
    }

    async loadReportsData() {
        try {
            this.showLoading();
            const [statsResponse, overdueResponse] = await Promise.all([
                libraryAPI.getStatistics(),
                libraryAPI.getOverdueBooks()
            ]);
            
            if (statsResponse.success) {
                this.updateReportsStats(statsResponse.data);
            }
            
            if (overdueResponse.success) {
                this.updateOverdueList(overdueResponse.data);
            }
        } catch (error) {
            this.showToast('Failed to load reports data', 'error');
        } finally {
            this.hideLoading();
        }
    }

    // Update UI methods
    updateDashboardStats(stats) {
        document.getElementById('totalBooks').textContent = stats.totalBooks || 0;
        document.getElementById('availableBooks').textContent = stats.availableBooks || 0;
        document.getElementById('totalMembers').textContent = stats.totalMembers || 0;
        document.getElementById('overdueBooks').textContent = stats.overdueBooks || 0;
    }

    updateBooksTable(books) {
        const tbody = document.getElementById('booksTableBody');
        if (!tbody) return;

        tbody.innerHTML = books.map(book => `
            <tr>
                <td>${book.bookId}</td>
                <td>${book.title}</td>
                <td>${book.author}</td>
                <td>${book.isbn}</td>
                <td>
                    <span class="status ${book.available ? 'available' : 'borrowed'}">
                        ${book.available ? 'Available' : 'Borrowed'}
                    </span>
                </td>
                <td>
                    ${book.available ? 
                        '<button class="btn btn-sm btn-success" onclick="ui.borrowBookFromTable(\'' + book.bookId + '\')">Borrow</button>' :
                        '<button class="btn btn-sm btn-warning" onclick="ui.returnBookFromTable(\'' + book.bookId + '\')">Return</button>'
                    }
                </td>
            </tr>
        `).join('');
    }

    updateMembersTable(members) {
        const tbody = document.getElementById('membersTableBody');
        if (!tbody) return;

        tbody.innerHTML = members.map(member => `
            <tr>
                <td>${member.id}</td>
                <td>${member.name}</td>
                <td>${member.email}</td>
                <td>${libraryAPI.formatDate(member.membershipDate)}</td>
                <td>${member.borrowedBooks?.length || 0}/${member.maxBooksAllowed}</td>
                <td>
                    <span class="status ${member.active ? 'active' : 'inactive'}">
                        ${member.active ? 'Active' : 'Inactive'}
                    </span>
                </td>
                <td>
                    <button class="btn btn-sm btn-primary" onclick="ui.viewMemberDetails('${member.id}')">
                        View Details
                    </button>
                </td>
            </tr>
        `).join('');
    }

    updateBorrowingsTable(borrowedBooks) {
        const tbody = document.getElementById('borrowingsTableBody');
        if (!tbody) return;

        tbody.innerHTML = borrowedBooks.map(book => `
            <tr>
                <td>${book.title}</td>
                <td>${book.memberName}</td>
                <td>${libraryAPI.formatDate(book.borrowDate)}</td>
                <td>${libraryAPI.formatDate(book.returnDate)}</td>
                <td>
                    <span class="status ${book.isOverdue ? 'overdue' : 'borrowed'}">
                        ${book.isOverdue ? 'Overdue' : 'Active'}
                    </span>
                </td>
                <td>
                    <button class="btn btn-sm btn-warning" onclick="ui.returnBookFromTable('${book.bookId}')">
                        Return
                    </button>
                </td>
            </tr>
        `).join('');
    }

    updateReportsStats(stats) {
        document.getElementById('reportTotalBooks').textContent = stats.totalBooks || 0;
        document.getElementById('reportAvailableBooks').textContent = stats.availableBooks || 0;
        document.getElementById('reportBorrowedBooks').textContent = stats.borrowedBooks || 0;
        document.getElementById('reportTotalMembers').textContent = stats.totalMembers || 0;
    }

    updateOverdueList(overdueBooks) {
        const container = document.getElementById('overdueList');
        if (!container) return;

        if (overdueBooks.length === 0) {
            container.innerHTML = '<p>No overdue books! 🎉</p>';
            return;
        }

        container.innerHTML = overdueBooks.map(book => `
            <div class="overdue-item">
                <div class="book-title">${book.title}</div>
                <div class="borrower">Borrowed by: ${book.memberName}</div>
                <div class="due-date">Due: ${libraryAPI.formatDate(book.returnDate)}</div>
            </div>
        `).join('');
    }

    updateSystemTime() {
        const timeElement = document.getElementById('systemTime');
        if (timeElement) {
            timeElement.textContent = new Date().toLocaleString();
        }
    }

    // Form handlers
    async handleAddBook() {
        const form = document.getElementById('addBookForm');
        if (!this.validateForm(form)) {
            this.showToast('Please fill in all required fields', 'error');
            return;
        }

        try {
            this.showLoading();
            
            const bookData = {
                bookId: document.getElementById('bookId').value,
                title: document.getElementById('bookTitle').value,
                author: document.getElementById('bookAuthor').value,
                isbn: document.getElementById('bookIsbn').value
            };

            const response = await libraryAPI.addBook(bookData);
            
            if (response.success) {
                this.showToast('Book added successfully!', 'success');
                this.closeModal('addBookModal');
                
                // Refresh data if on books page
                if (this.currentPage === 'books') {
                    await this.loadBooksData();
                }
                
                // Update dashboard stats
                await this.loadDashboardData();
            }
        } catch (error) {
            this.showToast(error.message, 'error');
        } finally {
            this.hideLoading();
        }
    }

    async handleAddMember() {
        const form = document.getElementById('addMemberForm');
        if (!this.validateForm(form)) {
            this.showToast('Please fill in all required fields', 'error');
            return;
        }

        try {
            this.showLoading();
            
            const memberData = {
                id: document.getElementById('memberId').value,
                name: document.getElementById('memberName').value,
                email: document.getElementById('memberEmail').value,
                maxBooksAllowed: parseInt(document.getElementById('maxBooks').value) || 5
            };

            const response = await libraryAPI.addMember(memberData);
            
            if (response.success) {
                this.showToast('Member added successfully!', 'success');
                this.closeModal('addMemberModal');
                
                // Refresh data if on members page
                if (this.currentPage === 'members') {
                    await this.loadMembersData();
                }
                
                // Update dashboard stats
                await this.loadDashboardData();
            }
        } catch (error) {
            this.showToast(error.message, 'error');
        } finally {
            this.hideLoading();
        }
    }

    async handleBorrowBook() {
        const form = document.getElementById('borrowForm');
        if (!this.validateForm(form)) {
            this.showToast('Please fill in all required fields', 'error');
            return;
        }

        try {
            this.showLoading();
            
            const memberId = document.getElementById('borrowMemberId').value;
            const bookId = document.getElementById('borrowBookId').value;

            const response = await libraryAPI.borrowBook(memberId, bookId);
            
            if (response.success) {
                this.showToast('Book borrowed successfully!', 'success');
                form.reset();
                
                // Refresh transaction data
                await this.loadTransactionsData();
                
                // Update dashboard stats
                await this.loadDashboardData();
            }
        } catch (error) {
            this.showToast(error.message, 'error');
        } finally {
            this.hideLoading();
        }
    }

    async handleReturnBook() {
        const form = document.getElementById('returnForm');
        if (!this.validateForm(form)) {
            this.showToast('Please fill in all required fields', 'error');
            return;
        }

        try {
            this.showLoading();
            
            const memberId = document.getElementById('returnMemberId').value;
            const bookId = document.getElementById('returnBookId').value;

            const response = await libraryAPI.returnBook(memberId, bookId);
            
            if (response.success) {
                this.showToast('Book returned successfully!', 'success');
                form.reset();
                
                // Refresh transaction data
                await this.loadTransactionsData();
                
                // Update dashboard stats
                await this.loadDashboardData();
            }
        } catch (error) {
            this.showToast(error.message, 'error');
        } finally {
            this.hideLoading();
        }
    }

    async handleBookSearch() {
        const searchInput = document.getElementById('bookSearchInput');
        const query = searchInput.value.trim();
        
        if (!query) {
            await this.loadBooksData();
            return;
        }

        try {
            this.showLoading();
            const response = await libraryAPI.searchBooks(query, 'title');
            
            if (response.success) {
                this.updateBooksTable(response.data);
            }
        } catch (error) {
            this.showToast('Search failed', 'error');
        } finally {
            this.hideLoading();
        }
    }

    async handleBookFilter() {
        await this.loadBooksData();
    }

    // Table action handlers
    borrowBookFromTable(bookId) {
        document.getElementById('borrowBookId').value = bookId;
        this.showPage('transactions');
        document.getElementById('borrowMemberId').focus();
    }

    returnBookFromTable(bookId) {
        document.getElementById('returnBookId').value = bookId;
        this.showPage('transactions');
        document.getElementById('returnMemberId').focus();
    }

    viewMemberDetails(memberId) {
        // This could open a modal with member details
        this.showToast(`Member details for ${memberId} - Feature coming soon!`, 'info');
    }
}

// Create and export UI manager instance
const ui = new UIManager();

// Make it globally available
window.ui = ui;

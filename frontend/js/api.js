/**
 * API Management Module
 * Handles all communication with the Java backend
 */

class LibraryAPI {
    constructor() {
        this.baseURL = 'http://localhost:8080/api';
        this.isOnline = false;
        this.mockData = this.initMockData();
    }

    // Initialize mock data for offline development
    initMockData() {
        return {
            books: [
                {
                    bookId: 'B001',
                    title: 'The Great Gatsby',
                    author: 'F. Scott Fitzgerald',
                    isbn: '978-0-7432-7356-5',
                    available: true,
                    borrowedBy: null,
                    borrowDate: null,
                    returnDate: null
                },
                {
                    bookId: 'B002',
                    title: 'To Kill a Mockingbird',
                    author: 'Harper Lee',
                    isbn: '978-0-06-112008-4',
                    available: false,
                    borrowedBy: 'M001',
                    borrowDate: '2025-01-15',
                    returnDate: '2025-01-29'
                },
                {
                    bookId: 'B003',
                    title: '1984',
                    author: 'George Orwell',
                    isbn: '978-0-452-28423-4',
                    available: true,
                    borrowedBy: null,
                    borrowDate: null,
                    returnDate: null
                },
                {
                    bookId: 'B004',
                    title: 'Pride and Prejudice',
                    author: 'Jane Austen',
                    isbn: '978-0-14-143951-8',
                    available: false,
                    borrowedBy: 'M002',
                    borrowDate: '2025-01-10',
                    returnDate: '2025-01-24'
                },
                {
                    bookId: 'B005',
                    title: 'The Catcher in the Rye',
                    author: 'J.D. Salinger',
                    isbn: '978-0-316-76948-0',
                    available: true,
                    borrowedBy: null,
                    borrowDate: null,
                    returnDate: null
                }
            ],
            members: [
                {
                    id: 'M001',
                    name: 'John Doe',
                    email: 'john.doe@email.com',
                    membershipDate: '2024-01-15',
                    maxBooksAllowed: 5,
                    active: true,
                    borrowedBooks: ['B002']
                },
                {
                    id: 'M002',
                    name: 'Jane Smith',
                    email: 'jane.smith@email.com',
                    membershipDate: '2024-02-20',
                    maxBooksAllowed: 5,
                    active: true,
                    borrowedBooks: ['B004']
                },
                {
                    id: 'M003',
                    name: 'Robert Johnson',
                    email: 'robert.j@email.com',
                    membershipDate: '2024-03-10',
                    maxBooksAllowed: 5,
                    active: true,
                    borrowedBooks: []
                }
            ],
            statistics: {
                totalBooks: 5,
                availableBooks: 3,
                borrowedBooks: 2,
                totalMembers: 3,
                totalLibrarians: 2,
                overdueBooks: 0
            }
        };
    }

    // Check if backend is available
    async checkConnection() {
        try {
            const response = await this.makeRequest('/statistics', 'GET');
            this.isOnline = response.success;
            return this.isOnline;
        } catch (error) {
            this.isOnline = false;
            console.log('Backend not available, using mock data');
            return false;
        }
    }

    // Generic request method with error handling
    async makeRequest(endpoint, method = 'GET', data = null) {
        const url = `${this.baseURL}${endpoint}`;
        
        const options = {
            method,
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        };

        if (data && (method === 'POST' || method === 'PUT')) {
            options.body = JSON.stringify(data);
        }

        try {
            const response = await fetch(url, options);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const result = await response.json();
            return result;
        } catch (error) {
            console.error(`API Error (${method} ${endpoint}):`, error);
            throw error;
        }
    }

    // Books API methods
    async getAllBooks(filter = 'all') {
        if (!this.isOnline) {
            let books = [...this.mockData.books];
            if (filter === 'available') {
                books = books.filter(book => book.available);
            } else if (filter === 'borrowed') {
                books = books.filter(book => !book.available);
            }
            return { success: true, data: books };
        }

        try {
            let endpoint = '/books';
            if (filter === 'available') {
                endpoint += '?available=true';
            } else if (filter === 'borrowed') {
                endpoint += '?borrowed=true';
            }
            
            return await this.makeRequest(endpoint);
        } catch (error) {
            throw new Error(`Failed to fetch books: ${error.message}`);
        }
    }

    async addBook(bookData) {
        if (!this.isOnline) {
            // Add to mock data
            const newBook = {
                ...bookData,
                available: true,
                borrowedBy: null,
                borrowDate: null,
                returnDate: null
            };
            this.mockData.books.push(newBook);
            this.mockData.statistics.totalBooks++;
            this.mockData.statistics.availableBooks++;
            return { success: true, data: 'Book added successfully' };
        }

        try {
            return await this.makeRequest('/books', 'POST', bookData);
        } catch (error) {
            throw new Error(`Failed to add book: ${error.message}`);
        }
    }

    async searchBooks(query, type = 'title') {
        if (!this.isOnline) {
            const books = this.mockData.books.filter(book => {
                if (type === 'title') {
                    return book.title.toLowerCase().includes(query.toLowerCase());
                } else if (type === 'author') {
                    return book.author.toLowerCase().includes(query.toLowerCase());
                }
                return false;
            });
            return { success: true, data: books };
        }

        try {
            const endpoint = `/search/books?${type}=${encodeURIComponent(query)}`;
            return await this.makeRequest(endpoint);
        } catch (error) {
            throw new Error(`Failed to search books: ${error.message}`);
        }
    }

    // Members API methods
    async getAllMembers() {
        if (!this.isOnline) {
            return { success: true, data: [...this.mockData.members] };
        }

        try {
            return await this.makeRequest('/members');
        } catch (error) {
            throw new Error(`Failed to fetch members: ${error.message}`);
        }
    }

    async addMember(memberData) {
        if (!this.isOnline) {
            const newMember = {
                ...memberData,
                membershipDate: new Date().toISOString().split('T')[0],
                active: true,
                borrowedBooks: []
            };
            this.mockData.members.push(newMember);
            this.mockData.statistics.totalMembers++;
            return { success: true, data: 'Member added successfully' };
        }

        try {
            return await this.makeRequest('/members', 'POST', memberData);
        } catch (error) {
            throw new Error(`Failed to add member: ${error.message}`);
        }
    }

    // Transaction methods
    async borrowBook(memberId, bookId) {
        if (!this.isOnline) {
            // Mock borrow operation
            const book = this.mockData.books.find(b => b.bookId === bookId);
            const member = this.mockData.members.find(m => m.id === memberId);
            
            if (!book) {
                throw new Error(`Book with ID ${bookId} not found`);
            }
            if (!member) {
                throw new Error(`Member with ID ${memberId} not found`);
            }
            if (!book.available) {
                throw new Error(`Book '${book.title}' is not available`);
            }
            if (member.borrowedBooks.length >= member.maxBooksAllowed) {
                throw new Error(`Member has reached borrowing limit`);
            }

            // Update book and member
            book.available = false;
            book.borrowedBy = memberId;
            book.borrowDate = new Date().toISOString().split('T')[0];
            book.returnDate = new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
            member.borrowedBooks.push(bookId);
            
            // Update statistics
            this.mockData.statistics.availableBooks--;
            this.mockData.statistics.borrowedBooks++;
            
            return { success: true, data: 'Book borrowed successfully' };
        }

        try {
            return await this.makeRequest('/borrow', 'POST', { memberId, bookId });
        } catch (error) {
            throw new Error(`Failed to borrow book: ${error.message}`);
        }
    }

    async returnBook(memberId, bookId) {
        if (!this.isOnline) {
            // Mock return operation
            const book = this.mockData.books.find(b => b.bookId === bookId);
            const member = this.mockData.members.find(m => m.id === memberId);
            
            if (!book) {
                throw new Error(`Book with ID ${bookId} not found`);
            }
            if (!member) {
                throw new Error(`Member with ID ${memberId} not found`);
            }
            if (book.available) {
                throw new Error(`Book '${book.title}' is not currently borrowed`);
            }
            if (book.borrowedBy !== memberId) {
                throw new Error(`Book '${book.title}' was not borrowed by this member`);
            }

            // Update book and member
            book.available = true;
            book.borrowedBy = null;
            book.borrowDate = null;
            book.returnDate = null;
            member.borrowedBooks = member.borrowedBooks.filter(id => id !== bookId);
            
            // Update statistics
            this.mockData.statistics.availableBooks++;
            this.mockData.statistics.borrowedBooks--;
            
            return { success: true, data: 'Book returned successfully' };
        }

        try {
            return await this.makeRequest('/return', 'POST', { memberId, bookId });
        } catch (error) {
            throw new Error(`Failed to return book: ${error.message}`);
        }
    }

    // Statistics method
    async getStatistics() {
        if (!this.isOnline) {
            return { success: true, data: { ...this.mockData.statistics } };
        }

        try {
            return await this.makeRequest('/statistics');
        } catch (error) {
            throw new Error(`Failed to fetch statistics: ${error.message}`);
        }
    }

    // Get borrowed books with member details
    async getBorrowedBooks() {
        const booksResponse = await this.getAllBooks('borrowed');
        const membersResponse = await this.getAllMembers();
        
        if (!booksResponse.success || !membersResponse.success) {
            throw new Error('Failed to fetch borrowed books data');
        }

        const borrowedBooks = booksResponse.data.map(book => {
            const member = membersResponse.data.find(m => m.id === book.borrowedBy);
            return {
                ...book,
                memberName: member ? member.name : 'Unknown',
                isOverdue: book.returnDate ? new Date(book.returnDate) < new Date() : false
            };
        });

        return { success: true, data: borrowedBooks };
    }

    // Get overdue books
    async getOverdueBooks() {
        const borrowedResponse = await this.getBorrowedBooks();
        
        if (!borrowedResponse.success) {
            throw new Error('Failed to fetch overdue books');
        }

        const overdueBooks = borrowedResponse.data.filter(book => book.isOverdue);
        return { success: true, data: overdueBooks };
    }

    // Utility method to format date
    formatDate(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    }

    // Generate unique ID for new items
    generateId(prefix, existingItems) {
        const numbers = existingItems
            .map(item => item.id || item.bookId)
            .filter(id => id.startsWith(prefix))
            .map(id => parseInt(id.substring(prefix.length)))
            .filter(num => !isNaN(num));
        
        const maxNumber = numbers.length > 0 ? Math.max(...numbers) : 0;
        return `${prefix}${(maxNumber + 1).toString().padStart(3, '0')}`;
    }
}

// Create and export API instance
const libraryAPI = new LibraryAPI();

// Initialize connection check
libraryAPI.checkConnection().then(isOnline => {
    if (isOnline) {
        console.log('Connected to backend API');
    } else {
        console.log('Using mock data - backend not available');
    }
});

// Export for use in other modules
window.libraryAPI = libraryAPI;

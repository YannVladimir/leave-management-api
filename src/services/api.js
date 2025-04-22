import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true
});

// Add request interceptor for authentication
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Leave Request Module
export const leaveRequestService = {
  // Basic CRUD operations
  create: (data) => api.post('/leave-requests', data),
  update: (id, data) => api.put(`/leave-requests/${id}`, data),
  delete: (id) => api.delete(`/leave-requests/${id}`),
  getById: (id) => api.get(`/leave-requests/${id}`),
  
  // User-specific operations
  getMyRequests: () => api.get('/leave-requests/my-requests'),
  getMyRequestsByStatus: (status) => api.get(`/leave-requests/my-requests/status/${status}`),
  
  // Team operations
  getTeamRequests: () => api.get('/leave-requests/team'),
  
  // Request actions
  approve: (id) => api.put(`/leave-requests/${id}/approve`),
  reject: (id) => api.put(`/leave-requests/${id}/reject`),
  cancel: (id) => api.put(`/leave-requests/${id}/cancel`),
  
  // Filtering
  getByDateRange: (startDate, endDate) => 
    api.get('/leave-requests/date-range', { params: { startDate, endDate } })
};

// Leave Report Module
export const leaveReportService = {
  // User reports
  getUserLeaveDays: (userId, startDate, endDate) => 
    api.get(`/reports/user/${userId}`, { params: { startDate, endDate } }),
  
  // Department reports
  getDepartmentLeaves: (departmentId, period) => 
    api.get('/reports/department-leaves', { params: { departmentId, period } }),
  
  // Analytics
  getCommonLeaveTypes: (period) => 
    api.get('/reports/common-leave-types', { params: { period } }),
  getBusiestMonths: (period) => 
    api.get('/reports/busiest-months', { params: { period } })
};

// Leave Balance Module
export const leaveBalanceService = {
  // User balances
  getMyBalances: () => api.get('/leave-balances/my-balances'),
  getMyBalanceByType: (leaveType) => api.get(`/leave-balances/my-balances/${leaveType}`),
  
  // Other user balances
  getUserBalances: (userId) => api.get(`/leave-balances/user/${userId}`),
  getUserBalanceByType: (userId, leaveType) => 
    api.get(`/leave-balances/user/${userId}/${leaveType}`),
  
  // Balance operations
  accrue: (data) => api.post('/leave-balances/accrue', data),
  carryForward: (data) => api.post('/leave-balances/carry-forward', data),
  adjust: (data) => api.post('/leave-balances/adjust', data),
  
  // Reports
  getSummary: () => api.get('/leave-balances/summary'),
  getHistory: () => api.get('/leave-balances/history')
};

// Holiday Module
export const holidayService = {
  // Basic CRUD operations
  create: (data) => api.post('/holidays', data),
  update: (id, data) => api.put(`/holidays/${id}`, data),
  delete: (id) => api.delete(`/holidays/${id}`),
  getAll: () => api.get('/holidays'),
  
  // Public holidays
  getPublicHolidays: () => api.get('/holidays/public'),
  
  // Filtering
  getHolidaysBetween: (startDate, endDate) => 
    api.get('/holidays/between', { params: { startDate, endDate } })
};

// Document Module
export const documentService = {
  // Basic operations
  upload: (formData) => api.post('/documents/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }),
  getById: (id) => api.get(`/documents/${id}`),
  delete: (id) => api.delete(`/documents/${id}`),
  
  // User documents
  getMyDocuments: () => api.get('/documents/my-documents'),
  
  // Metadata
  getDocumentTypes: () => api.get('/documents/types')
};

// User Role Module
export const userRoleService = {
  updateUserRoles: (userId, roles) => api.put(`/user-roles/${userId}/roles`, roles),
  getAvailableRoles: () => api.get('/user-roles/roles')
};

// Authentication Module
export const authService = {
  login: (credentials) => api.post('/auth/login', credentials),
  logout: () => api.post('/auth/logout'),
  refreshToken: () => api.post('/auth/refresh-token'),
  getCurrentUser: () => api.get('/auth/me')
};

export default api; 
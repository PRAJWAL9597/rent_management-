import axios from 'axios';

const API = axios.create({
  baseURL: "http://localhost:8081/api",
});

export const tenantAPI = {
  getAll: () => API.get('/tenants'), //
};

export const meterAPI = {
  // Uses your endpoint: POST /api/meter-readings/room/{roomNo}
  addReading: (roomNo, data) => API.post(`/meter-readings/room/${roomNo}`, data),
};

export const invoiceAPI = {
  // Uses your endpoint: POST /api/invoices/bulk
  generateBulk: (month, commonUnits) => 
    API.post(`/invoices/bulk?month=${month}&totalCommonUnits=${commonUnits}`),
  
  // Uses your endpoint: GET /api/invoices/month/{month}/download-all
  downloadAll: (month) => `${API.defaults.baseURL}/invoices/month/${month}/download-all`,
};
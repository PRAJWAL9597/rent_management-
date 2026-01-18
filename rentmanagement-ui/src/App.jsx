import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { 
  Download, Users, Zap, Plus, FileText, ChevronRight, 
  LayoutDashboard, X, Loader2, Settings, Eye 
} from 'lucide-react';

const API_BASE = "http://localhost:8081/api";

const App = () => {
  const [tenants, setTenants] = useState([]);
  const [monthlyInvoices, setMonthlyInvoices] = useState([]); // Historical data
  const [month, setMonth] = useState("2026-01");
  const [commonUnits, setCommonUnits] = useState(50);
  const [loading, setLoading] = useState(false);
  
  // Modals State
  const [selectedTenant, setSelectedTenant] = useState(null);
  const [showTenantModal, setShowTenantModal] = useState(false);
  const [showSettings, setShowSettings] = useState(false);
  
  // Form States
  const [readings, setReadings] = useState({ prev: 0, curr: '' });
  const [pricing, setPricing] = useState({
    roomRent: '',
    unitPrice: '',
    effectiveFrom: new Date().toISOString().split('T')[0]
  });
  const [newTenant, setNewTenant] = useState({ 
    name: '', roomNo: '', phoneNo: '', meterId: '', 
    aadharNo: '', email: '', joiningDate: new Date().toISOString().split('T')[0] 
  });

  // Load tenants and historical invoices on startup or month change
  useEffect(() => { 
    loadTenants(); 
    fetchMonthlyHistory();
  }, [month]);

  const loadTenants = async () => {
    try {
      const res = await axios.get(`${API_BASE}/tenants`);
      setTenants(res.data);
    } catch (err) { console.error("Failed to load tenants"); }
  };

  const fetchMonthlyHistory = async () => {
    try {
      const res = await axios.get(`${API_BASE}/invoices/month/${month}`);
      setMonthlyInvoices(res.data);
    } catch (err) { setMonthlyInvoices([]); }
  };

  const openReadingModal = async (tenant) => {
    setSelectedTenant(tenant);
    try {
      const res = await axios.get(`${API_BASE}/meter-readings/latest/${tenant.roomNo}`);
      setReadings({ prev: res.data || 0, curr: '' });
    } catch (err) { setReadings({ prev: 0, curr: '' }); }
  };

  const handleAddTenant = async () => {
    try {
      await axios.post(`${API_BASE}/tenants`, { ...newTenant, active: true });
      alert("Tenant Registered!");
      setShowTenantModal(false);
      loadTenants();
    } catch (err) { alert("Error adding tenant. Check room/phone uniqueness."); }
  };

  const handleSavePricing = async () => {
    try {
      await axios.post(`${API_BASE}/pricing`, pricing);
      alert("Pricing Policy Updated!");
      setShowSettings(false);
    } catch (err) { alert("Failed to save pricing policy."); }
  };

  const handleSaveReading = async () => {
    try {
      await axios.post(`${API_BASE}/meter-readings/room/${selectedTenant.roomNo}`, {
        readingMonth: `${month}-01`,
        previousReading: readings.prev,
        currentReading: readings.curr
      });
      alert("Reading Saved!");
      setSelectedTenant(null);
    } catch (err) { alert("Error saving reading."); }
  };

  const triggerBulkInvoicing = async () => {
    setLoading(true);
    try {
      const res = await axios.post(`${API_BASE}/invoices/bulk?month=${month}&totalCommonUnits=${commonUnits}`);
      alert(res.data);
      fetchMonthlyHistory(); // Refresh history table
    } catch (err) { alert("Bulk generation failed."); }
    finally { setLoading(false); }
  };

  return (
    <div className="flex min-h-screen bg-slate-50 font-sans">
      {/* Sidebar */}
      <aside className="w-64 bg-[#001f3f] text-white p-6 hidden md:flex flex-col border-r border-slate-800">
        <h1 className="text-xl font-black mb-10 tracking-tight text-blue-400 italic">Nilkantheshwer Hights</h1>
        <nav className="space-y-4 font-bold text-sm">
          <div className="flex items-center space-x-3 bg-blue-600 p-3 rounded-xl shadow-lg shadow-blue-900/40 cursor-pointer">
            <LayoutDashboard size={18} /> <span>Dashboard</span>
          </div>
          <div onClick={() => setShowSettings(true)} className="flex items-center space-x-3 text-slate-400 p-3 hover:text-white transition cursor-pointer">
            <Settings size={18} /> <span>Pricing Settings</span>
          </div>
        </nav>
      </aside>

      <main className="flex-1 p-10 max-w-7xl mx-auto">
        <header className="flex justify-between items-center mb-10">
          <div>
            <h2 className="text-3xl font-black text-slate-800 tracking-tight">Management Console</h2>
            <p className="text-slate-500 font-medium italic">Owner Dashboard for Nilkantheshwar Heights</p>
          </div>
          <div className="flex space-x-4">
            <button onClick={() => setShowTenantModal(true)} className="flex items-center space-x-2 bg-blue-600 text-white px-5 py-2.5 rounded-xl font-bold hover:bg-blue-700 transition shadow-lg">
              <Plus size={18} /> <span>Add Tenant</span>
            </button>
            <button onClick={() => window.location.href=`${API_BASE}/invoices/month/${month}/download-all`} className="flex items-center space-x-2 bg-slate-900 text-white px-5 py-2.5 rounded-xl font-bold hover:bg-slate-800 transition">
              <Download size={18} /> <span>ZIP Invoices</span>
            </button>
          </div>
        </header>

        {/* Global Controls */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
          <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
            <label className="text-[10px] font-black text-slate-400 uppercase mb-2 block">Billing Month</label>
            <input type="month" value={month} onChange={(e) => setMonth(e.target.value)} className="w-full text-lg font-bold bg-slate-50 p-2 rounded-lg" />
          </div>
          <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
            <label className="text-[10px] font-black text-slate-400 uppercase mb-2 block">Common Units Total</label>
            <input type="number" value={commonUnits} onChange={(e) => setCommonUnits(e.target.value)} className="w-full text-lg font-bold bg-slate-50 p-2 rounded-lg" />
          </div>
          <button onClick={triggerBulkInvoicing} disabled={loading} className="bg-blue-600 text-white rounded-2xl font-black text-xs uppercase tracking-widest hover:bg-blue-700 transition">
            {loading ? <Loader2 className="animate-spin" /> : "Run Bulk Invoicing"}
          </button>
        </div>

        {/* Active Tenant List */}
        <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden mb-12">
          <div className="p-6 border-b border-slate-100 font-black text-slate-700 uppercase text-xs tracking-widest bg-slate-50/50">Active Tenants</div>
          <table className="w-full text-left">
            <tbody className="divide-y divide-slate-100">
              {tenants.map((t) => (
                <tr key={t.id} className="hover:bg-slate-50 transition group">
                  <td className="px-8 py-5 font-bold text-slate-800 uppercase text-sm">{t.name}</td>
                  <td className="px-8 py-5 font-bold text-blue-600">Room {t.roomNo}</td>
                  <td className="px-8 py-5 text-right font-black text-slate-400 group-hover:text-blue-600 text-[10px] uppercase cursor-pointer" onClick={() => openReadingModal(t)}>Add Reading —&gt;</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* HISTORY TABLE: Database Fetch */}
        <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
          <div className="p-6 border-b border-slate-100 flex justify-between items-center bg-slate-50/50">
            <h3 className="font-black text-slate-700 uppercase tracking-tight text-xs">Generated Invoices for {month}</h3>
            <FileText className="text-slate-400" size={18} />
          </div>
          <table className="w-full text-left">
            <tbody className="divide-y divide-slate-100">
              {monthlyInvoices.map((inv) => (
                <tr key={inv.id} className="hover:bg-slate-50 transition">
                  <td className="px-8 py-4 font-bold text-slate-700 text-sm">{inv.tenantName}</td>
                  <td className="px-8 py-4 font-bold text-blue-600 text-sm">Room {inv.roomNo}</td>
                  <td className="px-8 py-4 text-slate-500 font-medium text-xs">Total: ₹{inv.totalAmount}</td>
                  <td className="px-8 py-4 text-right">
                    <button onClick={() => window.location.href=`${API_BASE}/invoices/${inv.id}/download`} className="text-blue-600 font-black text-[10px] uppercase hover:underline flex items-center justify-end w-full">
                      <Download size={12} className="mr-1" /> Download PDF
                    </button>
                  </td>
                </tr>
              ))}
              {monthlyInvoices.length === 0 && (
                <tr><td className="p-10 text-center text-slate-400 italic text-sm">No history found for this month.</td></tr>
              )}
            </tbody>
          </table>
        </div>

        {/* MODAL: SETTINGS (Pricing Policy) */}
        {showSettings && (
          <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-3xl p-8 w-full max-w-md shadow-2xl relative">
              <button onClick={() => setShowSettings(false)} className="absolute right-6 top-6 text-slate-400 hover:text-slate-600"><X /></button>
              <h3 className="text-2xl font-black mb-6">Pricing Settings</h3>
              <div className="space-y-4 mb-8">
                <div>
                  <label className="text-[10px] font-black text-slate-400 uppercase mb-1 block">Monthly Room Rent (₹)</label>
                  <input type="number" placeholder="5000" className="w-full p-4 bg-slate-50 rounded-2xl font-black border border-slate-200" onChange={e => setPricing({...pricing, roomRent: e.target.value})} />
                </div>
                <div>
                  <label className="text-[10px] font-black text-slate-400 uppercase mb-1 block">Unit Price (₹)</label>
                  <input type="number" placeholder="10.50" className="w-full p-4 bg-slate-50 rounded-2xl font-black border border-slate-200" onChange={e => setPricing({...pricing, unitPrice: e.target.value})} />
                </div>
                <div>
                  <label className="text-[10px] font-black text-slate-400 uppercase mb-1 block">Effective From</label>
                  <input type="date" value={pricing.effectiveFrom} className="w-full p-4 bg-slate-50 rounded-2xl font-black border border-slate-200" onChange={e => setPricing({...pricing, effectiveFrom: e.target.value})} />
                </div>
              </div>
              <button onClick={handleSavePricing} className="w-full py-4 bg-emerald-600 text-white rounded-2xl font-black uppercase tracking-widest shadow-xl shadow-emerald-600/20 hover:bg-emerald-700 transition">Save Policy</button>
            </div>
          </div>
        )}

        {/* MODAL: ADD TENANT */}
        {showTenantModal && (
          <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-3xl p-8 w-full max-w-lg shadow-2xl relative">
              <button onClick={() => setShowTenantModal(false)} className="absolute right-6 top-6 text-slate-400 hover:text-slate-600"><X /></button>
              <h3 className="text-2xl font-black mb-6">New Tenant</h3>
              <div className="grid grid-cols-2 gap-4 mb-8">
                <input placeholder="Name" className="p-3 bg-slate-50 rounded-xl font-bold border col-span-2" onChange={e => setNewTenant({...newTenant, name: e.target.value})} />
                <input placeholder="Room" className="p-3 bg-slate-50 rounded-xl font-bold border" onChange={e => setNewTenant({...newTenant, roomNo: e.target.value})} />
                <input placeholder="Meter ID" className="p-3 bg-slate-50 rounded-xl font-bold border" onChange={e => setNewTenant({...newTenant, meterId: e.target.value})} />
                <input placeholder="Phone" className="p-3 bg-slate-50 rounded-xl font-bold border" onChange={e => setNewTenant({...newTenant, phoneNo: e.target.value})} />
                <input placeholder="Aadhar" className="p-3 bg-slate-50 rounded-xl font-bold border" onChange={e => setNewTenant({...newTenant, aadharNo: e.target.value})} />
                <input type="date" value={newTenant.joiningDate} className="p-3 bg-slate-50 rounded-xl font-bold border col-span-2" onChange={e => setNewTenant({...newTenant, joiningDate: e.target.value})} />
              </div>
              <button onClick={handleAddTenant} className="w-full py-4 bg-slate-900 text-white rounded-2xl font-black uppercase shadow-xl hover:bg-slate-800 transition">Save Tenant</button>
            </div>
          </div>
        )}

        {/* MODAL: RECORD READING */}
        {selectedTenant && (
          <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-3xl p-8 w-full max-w-md shadow-2xl relative">
              <button onClick={() => setSelectedTenant(null)} className="absolute right-6 top-6 text-slate-400 hover:text-slate-600"><X /></button>
              <h3 className="text-2xl font-black mb-2 text-slate-800 tracking-tight">Add Reading</h3>
              <p className="text-slate-500 mb-8 font-bold text-[10px] uppercase tracking-widest">Room {selectedTenant.roomNo} — {selectedTenant.name}</p>
              <div className="space-y-6 mb-8">
                <div>
                  <label className="text-[10px] font-black text-slate-400 block mb-1 uppercase tracking-widest">Previous (Auto)</label>
                  <input type="number" disabled className="w-full p-4 bg-slate-100 rounded-2xl font-black text-slate-400 border border-slate-200 cursor-not-allowed" value={readings.prev} />
                </div>
                <div>
                  <label className="text-[10px] font-black text-blue-500 block mb-1 uppercase tracking-widest">Current Reading</label>
                  <input type="number" autoFocus className="w-full p-4 bg-slate-50 rounded-2xl font-black border-2 border-blue-600 focus:outline-none focus:ring-4 focus:ring-blue-100 transition" value={readings.curr} onChange={e => setReadings({...readings, curr: e.target.value})} />
                </div>
              </div>
              <button onClick={handleSaveReading} className="w-full py-4 bg-blue-600 text-white rounded-2xl font-black uppercase tracking-widest shadow-xl shadow-blue-600/30 hover:bg-blue-700 transition">Save Data</button>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default App;
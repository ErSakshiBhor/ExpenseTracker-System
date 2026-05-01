import React, { useState, useEffect } from 'react';
import {
  PieChart, Pie, Cell,
  BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Legend
} from 'recharts';
import { jsPDF } from 'jspdf';
import './App.css';

// Define base API URL from environment variable
const API_URL = process.env.REACT_APP_API_URL;

// Debug log to verify API URL is loaded
console.log("API URL:", API_URL);

const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899', '#06b6d4'];

function Login({ setView, setUser }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = (e) => {
    e.preventDefault();
    setError('');
    if (!email || !password) {
      setError('Please fill in all fields.');
      return;
    }
    setLoading(true);
    fetch(`${API_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    })
      .then(async (res) => {
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Invalid credentials');
        return data;
      })
      .then((data) => {
        setUser({ name: data.name, email: data.email, userId: data.userId });
        setView('dashboard');
      })
      .catch((err) => {
        console.error('Login error:', err);
        // Show "Failed to fetch" only for network errors
        if (err.message === 'Failed to fetch' || err.name === 'TypeError') {
          setError('Failed to fetch. Please check your network connection and try again.');
        } else {
          setError(err.message);
        }
      })
      .finally(() => setLoading(false));
  };

  return (
    <div className="auth-container">
      <div className="auth-card dashboard-card">
        <h2>Welcome Back</h2>
        {error && <div className="auth-error">{error}</div>}
        <form onSubmit={handleLogin} className="auth-form">
          <div className="form-group">
            <label>Email</label>
            <input type="email" value={email} onChange={e => setEmail(e.target.value)} required className="form-input" placeholder="you@example.com" />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)} required className="form-input" placeholder="••••••••" />
          </div>
          <button type="submit" className="parse-btn submit-tx-btn" disabled={loading}>
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>
        <p className="auth-switch">
          Don't have an account? <span onClick={() => setView('signup')}>Sign Up</span>
        </p>
      </div>
    </div>
  );
}

function Signup({ setView, setUser }) {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSignup = (e) => {
    e.preventDefault();
    setError('');
    if (!name || !email || !password) {
      setError('Please fill in all fields.');
      return;
    }
    setLoading(true);
    fetch(`${API_URL}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, email, password })
    })
      .then(async (res) => {
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Signup failed');
        alert('Signup successful! Please login to continue.');
        setView('login');
      })
      .catch((err) => {
        console.error('Signup error:', err);
        // Show "Failed to fetch" only for network errors
        if (err.message === 'Failed to fetch' || err.name === 'TypeError') {
          setError('Failed to fetch. Please check your network connection and try again.');
        } else {
          setError(err.message);
        }
      })
      .finally(() => setLoading(false));
  };

  return (
    <div className="auth-container">
      <div className="auth-card dashboard-card">
        <h2>Create Account</h2>
        {error && <div className="auth-error">{error}</div>}
        <form onSubmit={handleSignup} className="auth-form">
          <div className="form-group">
            <label>Name</label>
            <input type="text" value={name} onChange={e => setName(e.target.value)} required className="form-input" placeholder="e.g. John Doe" />
          </div>
          <div className="form-group">
            <label>Email</label>
            <input type="email" value={email} onChange={e => setEmail(e.target.value)} required className="form-input" placeholder="you@example.com" />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)} required className="form-input" placeholder="••••••••" />
          </div>
          <button type="submit" className="parse-btn submit-tx-btn" disabled={loading}>
            {loading ? 'Signing up...' : 'Sign Up'}
          </button>
        </form>
        <p className="auth-switch">
          Already have an account? <span onClick={() => setView('login')}>Login</span>
        </p>
      </div>
    </div>
  );
}

function Dashboard({ isDark, userId }) {
  const [transactions, setTransactions] = useState([]);
  const [analysis, setAnalysis] = useState({});
  const [insights, setInsights] = useState([]);
  const [monthlyReport, setMonthlyReport] = useState({});
  const [topMerchants, setTopMerchants] = useState({});
  
  // SMS Parse state
  const [smsText, setSmsText] = useState('');
  const [isParsing, setIsParsing] = useState(false);

  // Manual Add Transaction state
  const [newTx, setNewTx] = useState({
    category: '',
    amount: '',
    merchant: '',
    date: '',
    type: 'expense'
  });
  const [isSubmitting, setIsSubmitting] = useState(false);

  const loadData = () => {
    // Include userId in all API calls
    const userParam = userId ? `?userId=${userId}` : '';
    
    fetch(`${API_URL}/transactions${userParam}`, {
      headers: { 'Content-Type': 'application/json' }
    })
      .then((res) => res.json())
      .then((data) => setTransactions(data))
      .catch((err) => console.error('Error fetching transactions:', err));

    fetch(`${API_URL}/transactions/analysis${userParam}`, {
      headers: { 'Content-Type': 'application/json' }
    })
      .then((res) => res.json())
      .then((data) => setAnalysis(data))
      .catch((err) => console.error('Error fetching analysis:', err));

    fetch(`${API_URL}/transactions/insights${userParam}`, {
      headers: { 'Content-Type': 'application/json' }
    })
      .then((res) => res.json())
      .then((data) => setInsights(data))
      .catch((err) => console.error('Error fetching insights:', err));

    fetch(`${API_URL}/transactions/monthly-report${userParam}`, {
      headers: { 'Content-Type': 'application/json' }
    })
      .then((res) => res.json())
      .then((data) => setMonthlyReport(data))
      .catch((err) => console.error('Error fetching monthly report:', err));

    fetch(`${API_URL}/transactions/top-merchants${userParam}`, {
      headers: { 'Content-Type': 'application/json' }
    })
      .then((res) => res.json())
      .then((data) => setTopMerchants(data))
      .catch((err) => console.error('Error fetching top merchants:', err));
  };

  useEffect(() => {
    loadData();
  }, [userId]);

  const handleParseSms = () => {
    if (!smsText.trim()) return;
    setIsParsing(true);

    fetch(`${API_URL}/transactions/parse-sms`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sms: smsText, userId: userId })
    })
      .then(res => {
        if (!res.ok) throw new Error('Failed to parse SMS');
        return res.json();
      })
      .then(data => {
        setSmsText('');
        loadData();
      })
      .catch(err => {
        console.error('SMS Parse error:', err);
        // Show "Failed to fetch" only for network errors
        if (err.message === 'Failed to fetch' || err.name === 'TypeError') {
          alert('Failed to fetch. Please check your network connection and try again.');
        } else {
          alert('Error parsing SMS. Ensure format is correct and backend is running.');
        }
      })
      .finally(() => {
        setIsParsing(false);
      });
  };

  const handleTxChange = (e) => {
    const { name, value } = e.target;
    setNewTx(prev => ({ ...prev, [name]: value }));
  };

  const handleTxSubmit = (e) => {
    e.preventDefault();
    if (!newTx.category || !newTx.amount || !newTx.merchant || !newTx.date) return;
    
    setIsSubmitting(true);
    fetch(`${API_URL}/transactions`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        ...newTx,
        userId: userId,
        date: newTx.date + "T00:00:00",
        amount: parseFloat(newTx.amount)
      })
    })
      .then(res => {
        if (!res.ok) throw new Error('Failed to add transaction');
        return res.json();
      })
      .then(() => {
        setNewTx({ category: '', amount: '', merchant: '', date: '', type: 'expense' });
        loadData();
      })
      .catch(err => {
        console.error('Transaction submit error:', err);
        // Show "Failed to fetch" only for network errors
        if (err.message === 'Failed to fetch' || err.name === 'TypeError') {
          alert('Failed to fetch. Please check your network connection and try again.');
        } else {
          alert('Error adding transaction.');
        }
      })
      .finally(() => setIsSubmitting(false));
  };

  const totalExpenses = transactions.reduce((sum, t) => sum + (t.amount || 0), 0);

  // CSV Export
  const downloadCSV = () => {
    const headers = ['Date', 'Category', 'Merchant', 'Amount', 'Type'];
    const csvRows = [headers.join(',')];

    transactions.forEach(t => {
      const dateVal = t.date ? new Date(t.date).toLocaleDateString() : 'N/A';
      const catVal = t.category || 'N/A';
      const merchVal = t.merchant || 'N/A';
      const amtVal = t.amount || 0;
      const typeVal = t.type || 'Expense';
      
      const row = [
        `"${dateVal}"`, 
        `"${catVal}"`, 
        `"${merchVal}"`, 
        amtVal, 
        `"${typeVal}"`
      ];
      csvRows.push(row.join(','));
    });

    const csvData = csvRows.join('\n');
    const blob = new Blob([csvData], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.setAttribute('hidden', '');
    a.setAttribute('href', url);
    a.setAttribute('download', 'transactions_report.csv');
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  };

  // PDF Export
  const downloadPDF = () => {
    const doc = new jsPDF();
    doc.setFont("helvetica", "bold");
    doc.setFontSize(22);
    doc.text("Expense Summary Report", 14, 22);

    doc.setFontSize(14);
    doc.setFont("helvetica", "normal");
    doc.text(`Total Expenses: ₹${totalExpenses.toFixed(2)}`, 14, 35);

    doc.setFontSize(16);
    doc.setFont("helvetica", "bold");
    doc.text("Category Analysis", 14, 50);

    doc.setFontSize(12);
    doc.setFont("helvetica", "normal");
    let yPos = 60;
    Object.entries(analysis).forEach(([cat, val]) => {
      doc.text(`${cat.toUpperCase()}: ₹${Number(val).toFixed(2)}`, 14, yPos);
      yPos += 8;
    });

    yPos += 10;
    doc.setFontSize(16);
    doc.setFont("helvetica", "bold");
    doc.text("Monthly Report", 14, yPos);

    doc.setFontSize(12);
    doc.setFont("helvetica", "normal");
    yPos += 10;
    Object.entries(monthlyReport).forEach(([month, val]) => {
      doc.text(`${month.toUpperCase()}: ₹${Number(val).toFixed(2)}`, 14, yPos);
      yPos += 8;
    });

    doc.save("expense_summary.pdf");
  };

  // Prepare Chart Data
  const categoryData = Object.entries(analysis).map(([name, value]) => ({ name, value }));
  const monthlyData = Object.entries(monthlyReport)
    .sort((a, b) => a[0].localeCompare(b[0])) 
    .map(([name, value]) => ({ name, value }));
    
  // Dynamic tick filling based on theme
  const chartAxisStroke = isDark ? '#94a3b8' : '#64748b';

  return (
    <div className="dashboard-container">
      {/* Export Action Buttons */}
      <div className="export-group">
        <button className="export-btn" onClick={downloadCSV}>
          📄 Download CSV Report
        </button>
        <button className="export-btn" onClick={downloadPDF}>
          📊 Download PDF Report
        </button>
      </div>

      {/* Top Total Expenses Summary */}
      <section className="summary-card">
        <h2>Total Expenses</h2>
        <p className="summary-amount">₹{totalExpenses.toFixed(2)}</p>
      </section>

      {/* Manual Add Transaction Form */}
      <section className="dashboard-card form-card" style={{ marginBottom: '2rem' }}>
        <h2>Add Transaction</h2>
        <form onSubmit={handleTxSubmit} className="tx-form">
          <div className="form-group">
            <label>Date</label>
            <input type="date" name="date" value={newTx.date} onChange={handleTxChange} required className="form-input" />
          </div>
          <div className="form-group">
            <label>Merchant</label>
            <input type="text" name="merchant" value={newTx.merchant} onChange={handleTxChange} required className="form-input" placeholder="e.g. Zomato" />
          </div>
          <div className="form-group">
            <label>Category</label>
            <input type="text" name="category" value={newTx.category} onChange={handleTxChange} required className="form-input" placeholder="e.g. food" />
          </div>
          <div className="form-group">
            <label>Amount (₹)</label>
            <input type="number" step="0.01" name="amount" value={newTx.amount} onChange={handleTxChange} required className="form-input" placeholder="0.00" />
          </div>
          <div className="form-group">
            <label>Type</label>
            <select name="type" value={newTx.type} onChange={handleTxChange} className="form-input">
              <option value="expense">Expense</option>
              <option value="income">Income</option>
            </select>
          </div>
          <button type="submit" className="parse-btn submit-tx-btn" disabled={isSubmitting}>
            {isSubmitting ? 'Adding...' : '+ Save'}
          </button>
        </form>
      </section>

      {/* Parse SMS Widget */}
      <section className="dashboard-card sms-card" style={{ marginBottom: '2rem' }}>
        <h2>Add Transaction via SMS Matcher</h2>
        <div className="sms-input-group">
          <textarea
            value={smsText}
            onChange={(e) => setSmsText(e.target.value)}
            placeholder="Paste bank SMS here (e.g. Rs.500 debited from A/c XX1234 at SWIGGY on 25-Mar)..."
            className="sms-textarea"
            rows={2}
          />
          <button
            onClick={handleParseSms}
            className="parse-btn"
            disabled={isParsing || !smsText.trim()}
          >
            {isParsing ? 'Parsing...' : 'Parse SMS'}
          </button>
        </div>
      </section>

      <div className="dashboard-grid">
        {/* Section: Category Analysis Pie Chart */}
        <section className="dashboard-card">
          <h2>Category Distribution</h2>
          <div className="chart-container">
            {categoryData.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={categoryData}
                    cx="50%"
                    cy="50%"
                    innerRadius={70}
                    outerRadius={100}
                    paddingAngle={5}
                    dataKey="value"
                    stroke="none"
                  >
                    {categoryData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip formatter={(value) => `₹${Number(value).toFixed(2)}`} />
                  <Legend verticalAlign="bottom" height={36}/>
                </PieChart>
              </ResponsiveContainer>
            ) : (
              <p className="empty-text">No category data to display.</p>
            )}
          </div>
        </section>

        {/* Section: Monthly Report Bar Chart */}
        <section className="dashboard-card">
          <h2>Monthly Overview</h2>
          <div className="chart-container">
            {monthlyData.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={monthlyData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                  <XAxis dataKey="name" stroke={chartAxisStroke} tickLine={false} axisLine={false} />
                  <YAxis stroke={chartAxisStroke} tickLine={false} axisLine={false} tickFormatter={(v) => `₹${v}`} />
                  <Tooltip cursor={{ fill: isDark ? 'rgba(255,255,255,0.05)' : 'rgba(0,0,0,0.05)' }} formatter={(value) => `₹${Number(value).toFixed(2)}`} />
                  <Bar dataKey="value" fill="#8b5cf6" radius={[6, 6, 0, 0]}>
                    {monthlyData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <p className="empty-text">No monthly data to display.</p>
            )}
          </div>
        </section>

        <section className="dashboard-card">
          <h2>Top 5 Merchants</h2>
          <div className="card-content">
            {Object.keys(topMerchants).length > 0 ? (
              <ul className="transaction-list">
                {Object.entries(topMerchants).map(([merchant, amount], index) => (
                  <li key={index} className="transaction-item">
                    <span className="category">
                      {index + 1}. {merchant.toUpperCase()}
                    </span>
                    <span className="amount">₹{Number(amount).toFixed(2)}</span>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-text">No merchant data to display.</p>
            )}
          </div>
        </section>

        {/* Section: Transactions List */}
        <section className="dashboard-card">
          <h2>Recent Transactions</h2>
          <div className="card-content">
            {transactions.length > 0 ? (
              <ul className="transaction-list">
                {transactions
                .slice() 
                .map((t, index) => (
                  <li key={t.id || index} className="transaction-item">
                    <span className="category">
                      {t.merchant || 'Unknown'}{' '}
                      <span style={{ fontSize: '0.8rem', color: isDark ? '#64748b' : '#94a3b8' }}>({t.category || 'Uncategorized'})</span>
                    </span>
                    <span className="amount">₹{t.amount?.toFixed(2) || t.amount}</span>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-text">No transactions found.</p>
            )}
          </div>
        </section>

        {/* Section: Insights */}
        <section className="dashboard-card">
          <h2>Smart Insights</h2>
          <div className="card-content">
            {insights.length > 0 ? (
              <ul className="insights-list">
                {insights.map((insight, index) => (
                  <li key={index} className="insight-item">
                    {insight}
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-text">No insights generated yet.</p>
            )}
          </div>
        </section>
      </div>
    </div>
  );
}

function App() {
  const [view, setView] = useState('login'); // 'login', 'signup', 'dashboard'
  const [user, setUser] = useState(null);
  const [isDark, setIsDark] = useState(true);

  useEffect(() => {
    if (isDark) {
      document.body.classList.add('dark-mode');
    } else {
      document.body.classList.remove('dark-mode');
    }
  }, [isDark]);

  return (
    <div className="app-root">
      <header className="dashboard-header main-header">
        <div className="header-left">
          <h1>Expense Tracker</h1>
        </div>
        <div className="header-right">
          {user && <span className="welcome-text">Hi, {user.name}</span>}
          <button className="theme-toggle" onClick={() => setIsDark(!isDark)}>
            {isDark ? '☀️ Light' : '🌙 Dark'}
          </button>
          {!user && view !== 'login' && (
             <button className="nav-btn" onClick={() => setView('login')}>Login</button>
          )}
          {!user && view !== 'signup' && (
             <button className="nav-btn" onClick={() => setView('signup')}>Sign Up</button>
          )}
          {user && (
             <button className="nav-btn logout-btn" onClick={() => { setUser(null); setView('login'); }}>Logout</button>
          )}
        </div>
      </header>

      <main className="main-content">
        {view === 'login' && <Login setView={setView} setUser={setUser} />}
        {view === 'signup' && <Signup setView={setView} setUser={setUser} />}
        {view === 'dashboard' && <Dashboard isDark={isDark} userId={user?.userId} />}
      </main>
    </div>
  );
}

export default App;

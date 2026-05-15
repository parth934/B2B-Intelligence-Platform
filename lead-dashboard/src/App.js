import React, { useEffect, useState } from 'react';
import axios from 'axios';

function App() {
  const [leads, setLeads] = useState([]);
  const [loading, setLoading] = useState(true);

    // Add these new states at the top of your App function
  const [newName, setNewName] = useState('');
  const [newIndustry, setNewIndustry] = useState('');

  const handleAddLead = (e) => {
    e.preventDefault();
    const newLead = { name: newName, industry: newIndustry, techStack: "Java", employeeCount: 100, isHiring: true };
    
    axios.post("http://localhost:8080/api/leads", newLead)
      .then(response => {
        setLeads([...leads, response.data]); // Update the list instantly
        setNewName('');
        setNewIndustry('');
      });
  };

  const handleDelete = (id) => {
  axios.delete(`http://localhost:8080/api/leads/${id}`)
    .then(() => {
      setLeads(leads.filter(lead => lead.id !== id)); // Remove from UI
      });
  };
  useEffect(() => {
    // 1. Make sure Spring Boot is running in IntelliJ on port 8080!
    axios.get("http://localhost:8080/api/leads")
      .then(response => {
        setLeads(response.data);
        setLoading(false);
      })
      .catch(error => {
        console.error("Error fetching data:", error);
        setLoading(false);
      });
  }, []);

return (
    <div style={{ 
      padding: '40px', 
      backgroundColor: '#0f172a', 
      color: '#f8fafc', 
      minHeight: '100vh',
      fontFamily: 'Segoe UI, Tahoma, Geneva, Verdana, sans-serif'
    }}>
      <h1 style={{ color: '#38bdf8', marginBottom: '10px' }}>B2B Lead Intelligence</h1>
      <p style={{ color: '#94a3b8', marginBottom: '30px' }}>Real-time scoring from Spring Boot Backend</p>
      
      {/* --- ADD NEW LEAD FORM START --- */}
      <form onSubmit={handleAddLead} style={{ 
        marginBottom: '40px', 
        padding: '20px', 
        backgroundColor: '#1e293b', 
        borderRadius: '12px',
        display: 'flex',
        gap: '10px'
      }}>
        <input 
          style={{ padding: '10px', borderRadius: '6px', border: '1px solid #334155', backgroundColor: '#0f172a', color: 'white', flex: 1 }}
          value={newName} 
          onChange={e => setNewName(e.target.value)} 
          placeholder="Company Name" 
          required 
        />
        <input 
          style={{ padding: '10px', borderRadius: '6px', border: '1px solid #334155', backgroundColor: '#0f172a', color: 'white', flex: 1 }}
          value={newIndustry} 
          onChange={e => setNewIndustry(e.target.value)} 
          placeholder="Industry (e.g. Tech, Finance)" 
          required 
        />
        <button type="submit" style={{ 
          padding: '10px 20px', 
          backgroundColor: '#38bdf8', 
          color: '#0f172a', 
          fontWeight: 'bold', 
          borderRadius: '6px', 
          border: 'none',
          cursor: 'pointer'
        }}>
          + Add Lead
        </button>
      </form>
      {/* --- ADD NEW LEAD FORM END --- */}
      {/* --- Delete LEAD FORM END --- */}

      <button 
      onClick={() => handleDelete(company.id)}
      style={{ backgroundColor: '#ef4444', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', padding: '5px 10px', marginLeft: '10px' }}
    >
      Delete
    </button>

      {loading ? (
        <p>Loading leads...</p>
      ) : (
        <div style={{ display: 'grid', gap: '15px' }}>
          {leads.map((company, index) => (
            <div key={index} style={{ 
              border: '1px solid #1e293b', 
              padding: '20px', 
              borderRadius: '12px', 
              backgroundColor: '#1e293b',
              boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)'
            }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h2 style={{ margin: '0', fontSize: '1.5rem' }}>{company.name}</h2>
                <span style={{ 
                  backgroundColor: company.score > 50 ? '#059669' : '#d97706',
                  padding: '5px 12px',
                  borderRadius: '20px',
                  fontSize: '0.9rem',
                  fontWeight: 'bold'
                }}>
                  Score: {company.score}
                </span>
              </div>
              <p style={{ margin: '10px 0 0', color: '#94a3b8' }}>
                <strong>Industry:</strong> {company.industry} | <strong>Tech:</strong> {company.techStack}
              </p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default App;
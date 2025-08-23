import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const  AdminLogin = ()=> {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      await axios.post('http://localhost:8080/auth/admin-login', {
        username,
        password
      }, {
        withCredentials: true
      });
      navigate('/admin/dashboard');
    } catch (error) {
      console.error('Admin login failed:', error);
      alert('Invalid credentials or unauthorized access');
    }
  };

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      height: '100vh',
      backgroundColor: '#f4f6f8'
    }}>
      <h2>Admin Login</h2>
      <form onSubmit={handleLogin} style={{
        display: 'flex',
        flexDirection: 'column',
        gap: '12px',
        width: '330px',
        padding: '30px',
        
        backgroundColor: '#fff',
        borderRadius: '8px',
        boxShadow: '0 4px 12px rgba(0,0,0,0.1)'
      }}>
        <input
          type="text"
          placeholder="Admin Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
          style={{ padding: '10px', fontSize: '16px', borderRadius: '6px', border: '1px solid #ccc'  , marginRight: '10px'}}
        />
        <input
          type="password"
          placeholder="Admin Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          style={{ padding: '10px', fontSize: '16px', borderRadius: '6px', border: '1px solid #ccc' }}
        />
        <button type="submit" style={{
          padding: '10px',
          backgroundColor: '#007bff',
          color: '#fff',
          border: 'none',
          borderRadius: '6px',
          cursor: 'pointer'
        }}>
          Login
        </button>
      </form>
    </div>
  );
}

export default AdminLogin;
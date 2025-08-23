import React, { useState, useEffect } from 'react';
import axios from 'axios';

const AdminDashboard =()=> {
  const [users, setUsers] = useState([]);
  const [sessions, setSessions] = useState({});
  const [selectedUser, setSelectedUser] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchUsers();
    fetchSessions();
  }, []);

  const fetchUsers = async () => {
    const res = await axios.get('/admin/users', { withCredentials: true });
    setUsers(res.data);
  };

  const fetchSessions = async () => {
    const res = await axios.get('/admin/sessions', { withCredentials: true });
    setSessions(res.data); // { username: [jti1, jti2] }
  };

  const blacklistToken = async (jti) => {
    await axios.post(`/admin/blacklist/${jti}`, {}, { withCredentials: true });
    fetchSessions();
  };

  const blacklistUser = async (username) => {
    await axios.post(`/admin/blacklist/user/${username}`, {}, { withCredentials: true });
    fetchSessions();
  };

  const filteredUsers = users.filter(user =>
    user.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div style={{ padding: '30px', fontFamily: 'Segoe UI, sans-serif' }}>
      <h2 style={{ marginBottom: '20px' }}>🔐 Admin Session Dashboard</h2>

      <input
        type="text"
        placeholder="Search users..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        style={{
          padding: '10px',
          fontSize: '16px',
          borderRadius: '6px',
          border: '1px solid #ccc',
          width: '300px',
          marginBottom: '30px'
        }}
      />

      <div style={{ display: 'flex', gap: '40px' }}>
        <div style={{ flex: 1 }}>
          <h3>👥 Users</h3>
          <ul style={{ listStyle: 'none', padding: 0 }}>
            {filteredUsers.map(user => (
              <li key={user} style={{ marginBottom: '10px' }}>
                <button
                  onClick={() => setSelectedUser(user)}
                  style={{
                    background: selectedUser === user ? '#007bff' : '#f0f0f0',
                    color: selectedUser === user ? '#fff' : '#333',
                    padding: '8px 12px',
                    border: 'none',
                    borderRadius: '4px',
                    cursor: 'pointer',
                    width: '100%',
                    textAlign: 'left'
                  }}
                >
                  {user}
                </button>
              </li>
            ))}
          </ul>
        </div>

        <div style={{ flex: 2 }}>
          <h3>🧾 Sessions</h3>
          {selectedUser && (
            <>
              <p><strong>{selectedUser}</strong></p>
              <button
                onClick={() => blacklistUser(selectedUser)}
                style={{
                  background: '#e74c3c',
                  color: '#fff',
                  padding: '8px 12px',
                  border: 'none',
                  borderRadius: '4px',
                  marginBottom: '10px',
                  cursor: 'pointer'
                }}
              >
                Blacklist All Tokens
              </button>
              <ul style={{ listStyle: 'none', padding: 0 }}>
                {(sessions[selectedUser] || []).map(jti => (
                  <li key={jti} style={{ marginBottom: '8px' }}>
                    <span style={{ fontSize: '14px', wordBreak: 'break-all' }}>{jti}</span>
                    <button
                      onClick={() => blacklistToken(jti)}
                      style={{
                        marginLeft: '10px',
                        background: '#ff9800',
                        color: '#fff',
                        padding: '4px 8px',
                        border: 'none',
                        borderRadius: '4px',
                        cursor: 'pointer'
                      }}
                    >
                      Blacklist
                    </button>
                  </li>
                ))}
              </ul>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default AdminDashboard;
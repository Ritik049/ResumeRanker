// import React, { useState } from 'react';
// import axios from 'axios';

// const LoginPage = () => {
//   const [credentials, setCredentials] = useState({
//     username: '',
//     password: '',
//   });

//   const handleChange = (e) => {
//     setCredentials({ ...credentials, [e.target.name]: e.target.value });
//   };

//   const handleLogin = async (e) => {
//     e.preventDefault();
//     try {
//       const response = await axios.post('/api/login', credentials);
//       const token = response.data.token;
//       localStorage.setItem('jwtToken', token);
//       window.location.href = '/home';
//     } catch (error) {
//       console.error('Login failed:', error);
//       alert('Invalid credentials. Please try again.');
//     }
//   };

//   return (
//     <div className="auth-container">
//       <h2>Login</h2>
//       <form onSubmit={handleLogin}>
//         <input name="username" placeholder="Username" onChange={handleChange} required />
//         <input name="password" type="password" placeholder="Password" onChange={handleChange} required />
//         <button type="submit">Login</button>
//       </form>
//     </div>
//   );
// };

// export default LoginPage;

import React, { useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
const LoginPage = () => {
  const [credentials, setCredentials] = useState({
    username: '',
    password: '',
  });

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
    //   const response = await axios.post(
    //     `${process.env.REACT_APP_API_BASE_URL}/auth/login`,
    //     credentials
    //   );
    //   const token = response.data.jwtToken;
    //   localStorage.setItem('jwtToken', token);
    //   window.location.href = '/home';

    const response = await axios.post(
  `${process.env.REACT_APP_API_BASE_URL}/auth/login`,
  credentials,
  {
    withCredentials: true, // 👈 Required to accept HttpOnly cookie
  }
);

// ✅ No need to extract or store token manually
// The cookie is now stored securely by the browser

window.location.href = '/home'; // Redirect after successful login
    
    } catch (error) {
      console.error('Login failed:', error);
      alert('Invalid credentials. Please try again.');
    }
  };

  return (
    <div className="auth-container">
      <h2>Login</h2>
      <form onSubmit={handleLogin}>
        <input
          name="username"
          placeholder="Username"
          value={credentials.username}
          onChange={handleChange}
          required
        />
        <input
          name="password"
          type="password"
          placeholder="Password"
          value={credentials.password}
          onChange={handleChange}
          required
        />
        <button type="submit">Login</button>
      </form>
       <p style={{ marginTop: '16px', fontSize: '14px' }}>
              New user?{' '}
              <Link to="/register" style={{ color: '#0078d4', textDecoration: 'none' }}>
                Register
              </Link>
            </p>
    </div>
  );
};

export default LoginPage;
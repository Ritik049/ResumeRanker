// import React, { useState } from 'react';
// import { Link } from 'react-router-dom';
// import axios from 'axios';

// const RegistrationPage = () => {
//   const [formData, setFormData] = useState({
//     username: '',
//     password: '',
//     email: '',
//     mobile: '',
//   });

//   const handleChange = (e) => {
//     setFormData({ ...formData, [e.target.name]: e.target.value });
//   };

//   const handleSubmit = async (e) => {
//     e.preventDefault();
//     try {
//       await axios.post('/api/register', formData);
//       localStorage.setItem('isRegistered', 'true');
//       window.location.href = '/login';
//     } catch (error) {
//       console.error('Registration failed:', error);
//       alert('Registration failed. Please try again.');
//     }
//   };

//   return (
//     <div className="auth-container">
//       <h2>Create Account</h2>
//       <form onSubmit={handleSubmit}>
//         <input name="username" placeholder="Username" onChange={handleChange} required />
//         <input name="email" type="email" placeholder="Email" onChange={handleChange} required />
//         <input name="mobile" placeholder="Mobile" onChange={handleChange} required />
//         <input name="password" type="password" placeholder="Password" onChange={handleChange} required />
//         <button type="submit">Register</button>
//       </form>
//       <p style={{ marginTop: '16px', fontSize: '14px' }}>
//         Already registered? <Link to="/login" style={{ color: '#0078d4', textDecoration: 'none' }}>Sign In</Link>
//       </p>
//     </div>
//   );
// };

// export default RegistrationPage;


import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';

const RegistrationPage = () => {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    email: '',
    mobile: '',
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        username: formData.username,
        password: formData.password,
        email: formData.email,
        address: 'India', // static for now
      };
      
      
      // console.log('API Base URL:', process.env.REACT_APP_API_BASE_URL);
      const response = await axios.post(
        `${process.env.REACT_APP_API_BASE_URL}/user/create`,
        payload
      );

    

      alert('✅ Registered successfully!');
      setFormData({
        username: '',
        password: '',
        email: '',
        mobile: '',
      });

      localStorage.setItem('isRegistered', 'true');
      window.location.href = '/login';
    } catch (error) {
      console.error('Registration failed:', error);
      alert('❌ Registration failed. Please try again.');
    }
  };

  return (
    <div className="auth-container">
      <h2>Create Account</h2>
      <form onSubmit={handleSubmit}>
        <input
          name="username"
          placeholder="Username"
          value={formData.username}
          onChange={handleChange}
          required
        />
        <input
          name="email"
          type="email"
          placeholder="Email"
          value={formData.email}
          onChange={handleChange}
          required
        />
        <input
          name="mobile"
          placeholder="Mobile"
          value={formData.mobile}
          onChange={handleChange}
          required
        />
        <input
          name="password"
          type="password"
          placeholder="Password"
          value={formData.password}
          onChange={handleChange}
          required
        />
        <button type="submit">Register</button>
      </form>
      <p style={{ marginTop: '16px', fontSize: '14px' }}>
        Already registered?{' '}
        <Link to="/login" style={{ color: '#0078d4', textDecoration: 'none' }}>
          Sign In
        </Link>
      </p>
    </div>
  );
};

export default RegistrationPage;

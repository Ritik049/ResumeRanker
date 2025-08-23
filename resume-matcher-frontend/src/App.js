import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import RegistrationPage from './RegistrationPage';
import LoginPage from './LoginPage';
import HomePage from './HomePage';
import AdminLogin from './Admin/AdminLogin';
import './App.css';
import AdminDashboard from './Admin/AdminDashboard';

function App() {
  const isRegistered = localStorage.getItem('isRegistered') === 'true';
  const token = localStorage.getItem('jwtToken');

  return (
    <div className="App">
      <Router>
        <Routes>
          <Route
            path="/"
            element={
              !isRegistered ? (
                <Navigate to="/register" />
              ) : !token ? (
                <Navigate to="/login" />
              ) : (
                <Navigate to="/home" />
              )
            }
          />
          <Route path="/register" element={<RegistrationPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/home" element={token ? <HomePage /> : <Navigate to="/login" />} />
          <Route path = "/admin/login" element={<AdminLogin />} />
          <Route path = "/admin/dashboard" element = {<AdminDashboard/>}/>
          {/* <Route path = "/home" element = {<HomePage/>}/> */}
        </Routes>
      </Router>
    </div>
  );
}

export default App;
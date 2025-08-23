// import React, { useState } from 'react';
// import './HomePage.css';

// const HomePage = () => {
//   const [resumeFile, setResumeFile] = useState(null);
//   const [jobDescription, setJobDescription] = useState('');
//   const [analysisResult, setAnalysisResult] = useState('');
//   const [loading, setLoading] = useState(false);

//   const handleFileChange = (e) => setResumeFile(e.target.files[0]);
//   const handleDescriptionChange = (e) => setJobDescription(e.target.value);

//   const handleAnalysis = async () => {
//     if (!resumeFile || !jobDescription.trim()) {
//       alert('Please upload a resume and enter a job description.');
//       return;
//     }

//     setLoading(true);
//     try {
//       const formData = new FormData();
//       formData.append('resume', resumeFile);
//       formData.append('jobDescription', jobDescription);

//       const response = await fetch('/api/analyze-resume', {
//         method: 'POST',
//         body: formData,
//       });

//       const result = await response.json();
//       setAnalysisResult(result.analysis || 'No insights returned.');
//     } catch (error) {
//       console.error('Analysis failed:', error);
//       setAnalysisResult('Error performing analysis. Please try again.');
//     } finally {
//       setLoading(false);
//     }
//   };

//   return (
//     <div className="home-wrapper">
//       <h1 className="title">📄 Resume Ranker</h1>
//       <div className="split-container">
//         {/* Left Panel */}
//         <div className="input-panel">
//           <div className="form-group">
//             <label className="label">Upload Resume</label>
//             <input type="file" accept=".pdf,.doc,.docx" onChange={handleFileChange} />
//           </div>

//           <div className="form-group">
//             <label className="label">Paste Job Description</label>
//             <textarea
//               rows="10"
//               placeholder="Paste job description here..."
//               value={jobDescription}
//               onChange={handleDescriptionChange}
//             />
//           </div>

//           <button className="analyze-btn" onClick={handleAnalysis} disabled={loading}>
//             {loading ? 'Analyzing...' : 'Perform Analysis'}
//           </button>
//         </div>

//         {/* Right Panel */}
//         <div className="result-panel">
//           {analysisResult ? (
//             <div className="result-section">
//               <h3>🔍 Analysis Result</h3>
//               <p>{analysisResult}</p>
//             </div>
//           ) : (
//             <div className="placeholder">
//               <p>No analysis performed yet. Upload your resume and paste a job description to begin.</p>
//             </div>
//           )}
//         </div>
//       </div>
//     </div>
//   );
// };

// export default HomePage;

// import React, { useState } from 'react';
// import axios from 'axios';
// import './HomePage.css';

// const HomePage = () => {
//   const [resumeFile, setResumeFile] = useState(null);
//   const [jobDescription, setJobDescription] = useState('');
//   const [analysisResult, setAnalysisResult] = useState(null);
//   const [loading, setLoading] = useState(false);
//   const [analysisType, setAnalysisType] = useState('smart'); // 'normal' or 'smart'

//   const handleFileChange = (e) => setResumeFile(e.target.files[0]);
//   const handleDescriptionChange = (e) => setJobDescription(e.target.value);
//   const handleTypeChange = (e) => setAnalysisType(e.target.value);

//   const handleDownloadPDF = () => {
//     const content = `
//       Match Score: ${analysisResult.match_score || analysisResult.matchScore}
//       Resume Keywords: ${analysisResult.resume_keywords?.join(', ')}
//       Job Keywords: ${analysisResult.job_keywords?.join(', ')}
//       Missing Skills: ${analysisResult.missing_skills?.join('\n') || analysisResult.missingKeywords?.join(', ')}
//       Feedback: ${analysisResult.feedback?.join('\n') || 'N/A'}
//       Strengths Summary: ${analysisResult.strengths_summary || 'N/A'}
//     `;
//     const blob = new Blob([content], { type: 'application/pdf' });
//     const url = URL.createObjectURL(blob);
//     const link = document.createElement('a');
//     link.href = url;
//     link.download = 'resume_analysis.pdf';
//     link.click();
//   };

//   const handleAnalysis = async () => {
//     if (!resumeFile || !jobDescription.trim()) {
//       alert('Please upload a resume and enter a job description.');
//       return;
//     }

//     setLoading(true);
//     const token = localStorage.getItem('jwtToken');

//     try {
//       // Step 1: Upload resume to extract content
//       const formData = new FormData();
//       formData.append('file', resumeFile);

//       const uploadRes = await axios.post(
//         `${process.env.REACT_APP_API_BASE_URL}/smart/api/resumes/upload`,
//         formData,
//         {
//           headers: {
//             Authorization: `Bearer ${token}`,
//             'Content-Type': 'multipart/form-data',
//           },
//         }
//       );

//       const resumeText = uploadRes.data;
//     //   console.log('Extracted Resume Text:', resumeText);


//       // Step 2: Analyze resume
//       const payload = {
//         resume: resumeText,
//         jobDescription: jobDescription,
//       };

//       const endpoint =
//         analysisType === 'smart'
//           ? `${process.env.REACT_APP_API_BASE_URL}/smart/api/resumes/analyze`
//           : `${process.env.REACT_APP_API_BASE_URL}/api/resumes/analyze`;

//       const analysisRes = await axios.post(endpoint, payload, {
//         headers: {
//           Authorization: `Bearer ${token}`,
//         },
//       });
//       console.log(analysisRes.data);

//       setAnalysisResult(analysisRes.data);
//     } catch (error) {
//       console.error('Analysis failed:', error);
//       setAnalysisResult({ error: 'Error performing analysis. Please try again.' });
//     } finally {
//       setLoading(false);
//     }
//   };

//   return (
//     <div className="home-wrapper">
//       <h1 className="title">📄 Resume Ranker</h1>
//       <div className="split-container">
//         {/* Left Panel */}
//         <div className="input-panel">
//           <div className="form-group">
//             <label className="label">Upload Resume</label>
//             <input type="file" accept=".pdf,.doc,.docx,.txt" onChange={handleFileChange} />
//           </div>

//           <div className="form-group">
//             <label className="label">Paste Job Description</label>
//             <textarea
//               rows="10"
//               placeholder="Paste job description here..."
//               value={jobDescription}
//               onChange={handleDescriptionChange}
//             />
//           </div>

//           <div className="form-group">
//             <label className="label">Select Analysis Type</label>
//             <select value={analysisType} onChange={handleTypeChange}>
//               <option value="smart">Smart Analysis (Gemini)</option>
//               <option value="normal">Normal Analysis</option>
//             </select>
//           </div>

//           <button className="analyze-btn" onClick={handleAnalysis} disabled={loading}>
//             {loading ? 'Analyzing...' : 'Perform Analysis'}
//           </button>
//         </div>

//         {/* Right Panel */}
//         <div className="result-panel">
//           {analysisResult ? (
//             <div className="result-section">
//               <h3>🔍 Analysis Result</h3>
//               {analysisResult.error ? (
//                 <p>{analysisResult.error}</p>
//               ) : (
//                 <>
//                   <p><strong>Match Score:</strong> {analysisResult.match_score || analysisResult.matchScore}</p>
//                   <p><strong>Resume Keywords:</strong> {analysisResult.resume_keywords?.join(', ')}</p>
//                   <p><strong>Job Keywords:</strong> {analysisResult.job_keywords?.join(', ')}</p>
//                   <p><strong>Missing Skills:</strong></p>
//                   <ul>
//                     {(analysisResult.missing_skills || analysisResult.missingKeywords)?.map((skill, idx) => (
//                       <li key={idx}>{skill}</li>
//                     ))}
//                   </ul>
//                   {analysisResult.feedback && (
//                     <>
//                       <p><strong>Feedback:</strong></p>
//                       <ul>
//                         {analysisResult.feedback.map((tip, idx) => (
//                           <li key={idx}>{tip}</li>
//                         ))}
//                       </ul>
//                     </>
//                   )}
//                   {analysisResult.strengths_summary && (
//                     <p><strong>Strengths Summary:</strong> {analysisResult.strengths_summary}</p>
//                   )}
//                   <button className="pdf-btn" onClick={handleDownloadPDF}>Download PDF</button>
//                 </>
//               )}
//             </div>
//           ) : (
//             <div className="placeholder">
//               <p>No analysis performed yet. Upload your resume and paste a job description to begin.</p>
//             </div>
//           )}
//         </div>
//       </div>
//     </div>
//   );
// };

// export default HomePage;

//last update 
// import React, { useState } from 'react';

// import axios from 'axios';
// import './HomePage.css';
// import { jsPDF } from 'jspdf';
// // import handleDownloadPDF from './Functions/handleDownloadPdf';

// const HomePage = () => {
//   const [resumeFile, setResumeFile] = useState(null);
//   const [jobDescription, setJobDescription] = useState('');
//   const [analysisResult, setAnalysisResult] = useState(null);
//   const [loading, setLoading] = useState(false);
//   const [analysisType, setAnalysisType] = useState('smart'); // 'normal' or 'smart'

//   const handleFileChange = (e) => setResumeFile(e.target.files[0]);
//   const handleDescriptionChange = (e) => setJobDescription(e.target.value);
//   const handleTypeChange = (e) => setAnalysisType(e.target.value);

// //   const handleDownloadPDF = () => {
// //     const resumeKeywordsLine =
// //       analysisType === 'smart' && analysisResult.resume_keywords
// //         ? `Resume Keywords: ${analysisResult.resume_keywords.join(', ')}\n`
// //         : '';

// //     const content = `
// // Match Score: ${analysisResult.match_score || analysisResult.matchScore}
// // ${resumeKeywordsLine}
// // Job Keywords: ${(analysisResult.job_keywords||analysisResult.jobKeywords)?.join(', ')}
// // Missing Skills: ${(analysisResult.missing_skills || analysisResult.missingKeywords)?.join(', ')}
// // Feedback: ${analysisResult.feedback?.join('\n') || 'N/A'}
// // Strengths Summary: ${analysisResult.strengths_summary || 'N/A'}
// //     `;

// //     const blob = new Blob([content], { type: 'application/pdf' });
// //     const url = URL.createObjectURL(blob);
// //     const link = document.createElement('a');
// //     link.href = url;
// //     link.download = 'resume_analysis.pdf';
// //     link.click();
// //   };
// const handleDownloadPDF = () => {
//   const doc = new jsPDF();
//   const pageHeight = doc.internal.pageSize.height;
//   let y = 20;

//   const addText = (text, indent = 20, lineHeight = 8, fontStyle = 'normal') => {
//     doc.setFont('helvetica', fontStyle);
//     const lines = doc.splitTextToSize(text, 170);
//     lines.forEach((line) => {
//       if (y + lineHeight > pageHeight - 10) {
//         doc.addPage();
//         y = 20;
//       }
//       doc.text(line, indent, y);
//       y += lineHeight;
//     });
//   };

//   doc.setFontSize(16);
//   addText('Resume Ranker', 20, 10, 'bold'); // 🔥 Bold title
//   y += 10;

//   doc.setFontSize(14);
//   addText('Match Score:', 20, 8, 'bold');
//   addText(`${analysisResult.match_score || analysisResult.matchScore}`);
//   y += 6;

//   if (analysisType === 'smart' && analysisResult.resume_keywords) {
//     addText('Resume Keywords:', 20, 8, 'bold');
//     addText(analysisResult.resume_keywords.join(', '));
//     y += 6;
//   }

//   addText('Job Keywords:', 20, 8, 'bold');
//   addText((analysisResult.job_keywords || analysisResult.jobKeywords)?.join(', '));
//   y += 6;

//   addText('Missing Skills:', 20, 8, 'bold');
//   (analysisResult.missing_skills || analysisResult.missingKeywords)?.forEach((skill) => {
//     addText(`- ${skill}`, 25);
//   });
//   y += 6;

//   if (analysisResult.feedback) {
//     addText('Feedback:', 20, 8, 'bold');
//     analysisResult.feedback.forEach((tip) => {
//       addText(`- ${tip}`, 25);
//     });
//     y += 6;
//   }

//   if (analysisResult.strengths_summary) {
//     addText('Strengths Summary:', 20, 8, 'bold');
//     addText(analysisResult.strengths_summary);
//   }

//   doc.save('resume_analysis.pdf');
// };


//   const handleAnalysis = async () => {
//     if (!resumeFile || !jobDescription.trim()) {
//       alert('Please upload a resume and enter a job description.');
//       return;
//     }

//     setLoading(true);
//     const token = localStorage.getItem('jwtToken');

//     try {
//       const formData = new FormData();
//       formData.append('file', resumeFile);

//       const uploadRes = await axios.post(
//         `${process.env.REACT_APP_API_BASE_URL}/smart/api/resumes/upload`,
//         formData,
//         {
//           headers: {
//             Authorization: `Bearer ${token}`,
//             'Content-Type': 'multipart/form-data',
//           },
//         }
//       );

//       const resumeText = uploadRes.data;

//       const payload = {
//         resume: resumeText,
//         jobDescription: jobDescription,
//       };

//       const endpoint =
//         analysisType === 'smart'
//           ? `${process.env.REACT_APP_API_BASE_URL}/smart/api/resumes/analyze`
//           : `${process.env.REACT_APP_API_BASE_URL}/api/resumes/analyze`;

//       const analysisRes = await axios.post(endpoint, payload, {
//         headers: {
//           Authorization: `Bearer ${token}`,
//         },
//       });

//       console.log('Analysis Response:', analysisRes.data);

//       setAnalysisResult(analysisRes.data);
//     } catch (error) {
//       console.error('Analysis failed:', error);
//       setAnalysisResult({ error: 'Error performing analysis. Please try again.' });
//     } finally {
//       setLoading(false);
//     }
//   };

//   return (
//     <div className="home-wrapper">
//       <h1 className="title">📄 Resume Ranker</h1>
//       <div className="split-container">
//         {/* Left Panel */}
//         <div className="input-panel">
//           <div className="form-group">
//             <label className="label">Upload Resume</label>
//             <input type="file" accept=".pdf,.doc,.docx,.txt" onChange={handleFileChange} />
//           </div>

//           <div className="form-group">
//             <label className="label">Paste Job Description</label>
//             <textarea
//               rows="10"
//               placeholder="Paste job description here..."
//               value={jobDescription}
//               onChange={handleDescriptionChange}
//             />
//           </div>

//           <div className="form-group">
//             <label className="label">Select Analysis Type</label>
//             <select value={analysisType} onChange={handleTypeChange}>
//               <option value="smart">Smart Analysis (Gemini)</option>
//               <option value="normal">Normal Analysis</option>
//             </select>
//           </div>

//           <button className="analyze-btn" onClick={handleAnalysis} disabled={loading}>
//             {loading ? 'Analyzing...' : 'Perform Analysis'}
//           </button>
//         </div>

//         {/* Right Panel */}
//         <div className="result-panel">
//           {analysisResult ? (
//             <div className="result-section">
//               <h3>🔍 Analysis Result</h3>
//               {analysisResult.error ? (
//                 <p>{analysisResult.error}</p>
//               ) : (
//                 <>
//                   <p><strong>Match Score:</strong> {analysisResult.match_score || analysisResult.matchScore}</p>

//                   {analysisType === 'smart' && analysisResult.resume_keywords && (
//                     <p><strong>Resume Keywords:</strong> {analysisResult.resume_keywords.join(', ')}</p>
//                   )}

//                   <p><strong>Job Keywords:</strong> {(analysisResult.job_keywords|| analysisResult.jobKeywords)?.join(', ')}</p>

//                   <p><strong>Missing Skills:</strong></p>
//                   <ul>
//                     {(analysisResult.missing_skills || analysisResult.missingKeywords)?.map((skill, idx) => (
//                       <li key={idx}>{skill}</li>
//                     ))}
//                   </ul>

//                   {analysisResult.feedback && (
//                     <>
//                       <p><strong>Feedback:</strong></p>
//                       <ul>
//                         {analysisResult.feedback.map((tip, idx) => (
//                           <li key={idx}>{tip}</li>
//                         ))}
//                       </ul>
//                     </>
//                   )}

//                   {analysisResult.strengths_summary && (
//                     <p><strong>Strengths Summary:</strong> {analysisResult.strengths_summary}</p>
//                   )}

//                   <button className="pdf-btn" onClick={handleDownloadPDF}>Download PDF</button>
//                 </>
//               )}
//             </div>
//           ) : (
//             <div className="placeholder">
//               <p>No analysis performed yet. Upload your resume and paste a job description to begin.</p>
//             </div>
//           )}
//         </div>
//       </div>
//     </div>
//   );
// };

// export default HomePage;




// import React, { useState } from 'react';
// import axios from 'axios';
// import './HomePage.css';
// import { jsPDF } from 'jspdf';

// const HomePage = () => {
//   const [resumeFile, setResumeFile] = useState(null);
//   const [jobDescription, setJobDescription] = useState('');
//   const [analysisResult, setAnalysisResult] = useState(null);
//   const [loading, setLoading] = useState(false);
//   const [analysisType, setAnalysisType] = useState('smart');

//   const handleFileChange = (e) => setResumeFile(e.target.files[0]);
//   const handleDescriptionChange = (e) => setJobDescription(e.target.value);
//   const handleTypeChange = (e) => setAnalysisType(e.target.value);

//   const handleDownloadPDF = () => {
//     const doc = new jsPDF();
//     const pageHeight = doc.internal.pageSize.height;
//     let y = 20;

//     const addText = (text, indent = 20, lineHeight = 8, fontStyle = 'normal') => {
//       doc.setFont('helvetica', fontStyle);
//       const lines = doc.splitTextToSize(text, 170);
//       lines.forEach((line) => {
//         if (y + lineHeight > pageHeight - 10) {
//           doc.addPage();
//           y = 20;
//         }
//         doc.text(line, indent, y);
//         y += lineHeight;
//       });
//     };

//     doc.setFontSize(16);
//     addText('Resume Ranker', 20, 10, 'bold');
//     y += 10;

//     doc.setFontSize(14);
//     addText('Match Score:', 20, 8, 'bold');
//     addText(`${analysisResult.match_score || analysisResult.matchScore}`);
//     y += 6;

//     if (analysisType === 'smart' && analysisResult.resume_keywords) {
//       addText('Resume Keywords:', 20, 8, 'bold');
//       addText(analysisResult.resume_keywords.join(', '));
//       y += 6;
//     }

//     addText('Job Keywords:', 20, 8, 'bold');
//     addText((analysisResult.job_keywords || analysisResult.jobKeywords)?.join(', '));
//     y += 6;

//     if ((analysisResult.missing_skills || analysisResult.missingKeywords)?.length === 0) {
//       addText('Missing Skills: None 🎯', 20, 8, 'bold');
//     } else {
//       addText('Missing Skills:', 20, 8, 'bold');
//       (analysisResult.missing_skills || analysisResult.missingKeywords)?.forEach((skill) => {
//         addText(`- ${skill}`, 25);
//       });
//     }
//     y += 6;

//     if (analysisResult.feedback) {
//       addText('Feedback:', 20, 8, 'bold');
//       analysisResult.feedback.forEach((tip) => {
//         addText(`- ${tip}`, 25);
//       });
//       y += 6;
//     }

//     if (analysisResult.strengths_summary) {
//       addText('Strengths Summary:', 20, 8, 'bold');
//       addText(analysisResult.strengths_summary);
//     }

//     if (analysisType === 'smart' && analysisResult.raw_scores) {
//       addText('Raw Scores:', 20, 8, 'bold');
//       Object.entries(analysisResult.raw_scores).forEach(([key, value]) => {
//         addText(`- ${key}: ${value}`, 25);
//       });
//     }

//     doc.save('resume_analysis.pdf');
//   };

//   const handleAnalysis = async () => {
//     if (!resumeFile || !jobDescription.trim()) {
//       alert('Please upload a resume and enter a job description.');
//       return;
//     }

//     setLoading(true);
//     const token = localStorage.getItem('jwtToken');

//     try {
//       const formData = new FormData();
//       formData.append('file', resumeFile);

//       const uploadRes = await axios.post(
//         `${process.env.REACT_APP_API_BASE_URL}/smart/api/resumes/upload`,
//         formData,
//         {
//           headers: {
//             Authorization: `Bearer ${token}`,
//             'Content-Type': 'multipart/form-data',
//           },
//         }
//       );

//       const resumeText = uploadRes.data;

//       const payload = {
//         resume: resumeText,
//         jobDescription: jobDescription,
//       };

//       const endpoint =
//         analysisType === 'smart'
//           ? `${process.env.REACT_APP_API_BASE_URL}/smart/api/resumes/analyze`
//           : `${process.env.REACT_APP_API_BASE_URL}/api/resumes/analyze`;

//       const analysisRes = await axios.post(endpoint, payload, {
//         headers: {
//           Authorization: `Bearer ${token}`,
//         },
//       });

//         console.log('Analysis Response:', analysisRes.data);

//       setAnalysisResult(analysisRes.data);
//     } catch (error) {
//       console.error('Analysis failed:', error);
//       setAnalysisResult({ error: 'Error performing analysis. Please try again.' });
//     } finally {
//       setLoading(false);
//     }
//   };

//   return (
//     <div className="home-wrapper">
//       <h1 className="title">📄 Resume Ranker</h1>
//       <div className="split-container">
//         <div className="input-panel">
//           <div className="form-group">
//             <label className="label">Upload Resume</label>
//             <input type="file" accept=".pdf,.doc,.docx,.txt" onChange={handleFileChange} />
//           </div>

//           <div className="form-group">
//             <label className="label">Paste Job Description</label>
//             <textarea
//               rows="10"
//               placeholder="Paste job description here..."
//               value={jobDescription}
//               onChange={handleDescriptionChange}
//             />
//           </div>

//           <div className="form-group">
//             <label className="label">Select Analysis Type</label>
//             <select value={analysisType} onChange={handleTypeChange}>
//               <option value="smart">Smart Analysis (Gemini)</option>
//               <option value="normal">Normal Analysis</option>
//             </select>
//           </div>

//           <button className="analyze-btn" onClick={handleAnalysis} disabled={loading}>
//             {loading ? 'Analyzing...' : 'Perform Analysis'}
//           </button>
//         </div>

//         <div className="result-panel">
//           {analysisResult ? (
//             <div className="result-section">
//               <h3>🔍 Analysis Result</h3>
//               {analysisResult.error ? (
//                 <p>{analysisResult.error}</p>
//               ) : (
//                 <>
//                   <p><strong>Match Score:</strong> {analysisResult.match_score || analysisResult.matchScore}</p>

//                   {analysisType === 'smart' && analysisResult.resume_keywords && (
//                     <p><strong>Resume Keywords:</strong> {analysisResult.resume_keywords.join(', ')}</p>
//                   )}

//                   <p><strong>Job Keywords:</strong> {(analysisResult.job_keywords || analysisResult.jobKeywords)?.join(', ')}</p>

//                   <p><strong>Missing Skills:</strong></p>
//                   {(analysisResult.missing_skills || analysisResult.missingKeywords)?.length === 0 ? (
//                     <p>None 🎯</p>
//                   ) : (
//                     <ul>
//                       {(analysisResult.missing_skills || analysisResult.missingKeywords)?.map((skill, idx) => (
//                         <li key={idx}>{skill}</li>
//                       ))}
//                     </ul>
//                   )}

//                   {analysisResult.feedback && (
//                     <>
//                       <p><strong>Feedback:</strong></p>
//                       <ul>
//                         {analysisResult.feedback.map((tip, idx) => (
//                           <li key={idx}>{tip}</li>
//                         ))}
//                       </ul>
//                     </>
//                   )}

//                   {analysisResult.strengths_summary && (
//                     <p><strong>Strengths Summary:</strong> {analysisResult.strengths_summary}</p>
//                   )}

//                   {analysisType === 'smart' && analysisResult.raw_scores && (
//                     <>
//                       <p><strong>Raw Scores:</strong></p>
//                       <ul>
//                         {Object.entries(analysisResult.raw_scores).map(([key, value], idx) => (
//                           <li key={idx}>{`${key}: ${value}`}</li>
//                         ))}
//                       </ul>
//                     </>
//                   )}

//                   <button className="pdf-btn" onClick={handleDownloadPDF}>Download PDF</button>
//                 </>
//               )}
//             </div>
//           ) : (
//             <div className="placeholder">
//               <p>No analysis performed yet. Upload your resume and paste a job description to begin.</p>
//             </div>
//           )}
//         </div>
//       </div>
//     </div>
//   );
// };

// export default HomePage;




// import React, { useState } from 'react';
// import axios from 'axios';
// import { jsPDF } from 'jspdf';
// import './HomePage.css';

// const HomePage = () => {
//   const [resumeFile, setResumeFile] = useState(null);
//   const [jobDescription, setJobDescription] = useState('');
//   const [analysisResult, setAnalysisResult] = useState(null);
//   const [loading, setLoading] = useState(false);
//   const [analysisType, setAnalysisType] = useState('smart');

//   const token = localStorage.getItem('jwtToken');

//   const handleFileChange = (e) => setResumeFile(e.target.files[0]);
//   const handleDescriptionChange = (e) => setJobDescription(e.target.value);
//   const handleTypeChange = (e) => setAnalysisType(e.target.value);

//   const handleDownloadPDF = () => {
//     const doc = new jsPDF();
//     const pageHeight = doc.internal.pageSize.height;
//     let y = 20;

//     const addText = (text, indent = 20, lineHeight = 8, fontStyle = 'normal') => {
//       doc.setFont('helvetica', fontStyle);
//       const lines = doc.splitTextToSize(text, 170);
//       lines.forEach((line) => {
//         if (y + lineHeight > pageHeight - 10) {
//           doc.addPage();
//           y = 20;
//         }
//         doc.text(line, indent, y);
//         y += lineHeight;
//       });
//     };

//     doc.setFontSize(16);
//     addText('Resume Ranker', 20, 10, 'bold');
//     y += 10;

//     doc.setFontSize(14);
//     addText('Match Score:', 20, 8, 'bold');
//     addText(`${analysisResult.match_score || analysisResult.matchScore}`);
//     y += 6;

//     if (analysisType === 'smart' && analysisResult.resume_keywords) {
//       addText('Resume Keywords:', 20, 8, 'bold');
//       addText(analysisResult.resume_keywords.join(', '));
//       y += 6;
//     }

//     addText('Job Keywords:', 20, 8, 'bold');
//     addText((analysisResult.job_keywords || analysisResult.jobKeywords)?.join(', '));
//     y += 6;

//     const missingSkills = analysisResult.missing_skills || analysisResult.missingKeywords;
//     addText('Missing Skills:', 20, 8, 'bold');
//     if (!missingSkills?.length) {
//       addText('None 🎯', 25);
//     } else {
//       missingSkills.forEach((skill) => addText(`- ${skill}`, 25));
//     }
//     y += 6;

//     if (analysisResult.feedback?.length) {
//       addText('Feedback:', 20, 8, 'bold');
//       analysisResult.feedback.forEach((tip) => addText(`- ${tip}`, 25));
//       y += 6;
//     }

//     if (analysisResult.strengths_summary) {
//       addText('Strengths Summary:', 20, 8, 'bold');
//       addText(analysisResult.strengths_summary);
//     }

//     if (analysisType === 'smart' && analysisResult.raw_scores) {
//       addText('Raw Scores:', 20, 8, 'bold');
//       Object.entries(analysisResult.raw_scores).forEach(([key, value]) => {
//         addText(`- ${key}: ${value}`, 25);
//       });
//     }

//     doc.save('resume_analysis.pdf');
//   };

//   const handleAnalysis = async () => {
//     if (!resumeFile || !jobDescription.trim()) {
//       alert('Please upload a resume and enter a job description.');
//       return;
//     }

//     setLoading(true);

//     try {
//       const formData = new FormData();
//       formData.append('file', resumeFile);

//       const uploadRes = await axios.post(
//         `${process.env.REACT_APP_API_BASE_URL}/smart/api/resumes/upload`,
//         formData,
//         {
//           headers: {
//             Authorization: `Bearer ${token}`,
//             'Content-Type': 'multipart/form-data',
//           },
//         }
//       );

//       const resumeText = uploadRes.data;

//       const payload = {
//         resume: resumeText,
//         jobDescription,
//       };

//       const endpoint =
//         analysisType === 'smart'
//           ? `${process.env.REACT_APP_API_BASE_URL}/smart/api/resumes/analyze`
//           : `${process.env.REACT_APP_API_BASE_URL}/api/resumes/analyze`;

//       const analysisRes = await axios.post(endpoint, payload, {
//         headers: { Authorization: `Bearer ${token}` },
//       });

//       console.log('Analysis Response:', analysisRes.data);
//       setAnalysisResult(analysisRes.data);
//     } catch (error) {
//       console.error('Analysis failed:', error);
//       setAnalysisResult({ error: 'Error performing analysis. Please try again.' });
//     } finally {
//       setLoading(false);
//     }
//   };



//   return (
//     <div className="home-wrapper">
//       <h1 className="title">📄 Resume Ranker</h1>
//       <div className="split-container">
//         <div className="input-panel">
//           <div className="form-group">
//             <label className="label">Upload Resume</label>
//             <input type="file" accept=".pdf,.doc,.docx,.txt" onChange={handleFileChange} />
//           </div>

//           <div className="form-group">
//             <label className="label">Paste Job Description</label>
//             <textarea
//               rows="10"
//               placeholder="Paste job description here..."
//               value={jobDescription}
//               onChange={handleDescriptionChange}
//             />
//           </div>

//           <div className="form-group">
//             <label className="label">Select Analysis Type</label>
//             <select value={analysisType} onChange={handleTypeChange}>
//               <option value="smart">Smart Analysis (Gemini)</option>
//               <option value="normal">Normal Analysis</option>
//             </select>
//           </div>

//           <button className="analyze-btn" onClick={handleAnalysis} disabled={loading}>
//             {loading ? 'Analyzing...' : 'Perform Analysis'}
//           </button>
//         </div>

//         <div className="result-panel">
//           <div className="result-section">
//             <h3>🔍 Analysis Result</h3>
//             {renderResult()}
//           </div>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default HomePage;



import React, { useState } from 'react';
import axios from 'axios';
import { jsPDF } from 'jspdf';
import './HomePage.css';

const HomePage = () => {
  const [resumeFile, setResumeFile] = useState(null);
  const [jobDescription, setJobDescription] = useState('');
  const [analysisResult, setAnalysisResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [analysisType, setAnalysisType] = useState('smart');

  const token = localStorage.getItem('jwtToken');

  const handleFileChange = (e) => setResumeFile(e.target.files[0]);
  const handleDescriptionChange = (e) => setJobDescription(e.target.value);
  const handleTypeChange = (e) => 
    {setAnalysisType(e.target.value);
    setAnalysisResult(null); // Reset analysis result when type changes
    }

    const handleLogout = async () => {
  try {
    await axios.post(`${process.env.REACT_APP_API_BASE_URL}/auth/logout`, {}, {
      withCredentials: true
    });
    // Optional: redirect to login or clear state
    window.location.href = '/login'; // or use navigate('/login') if using React Router
  } catch (error) {
    console.error('Logout failed:', error);
  }
};

  const handleDownloadPDF = () => {
    const doc = new jsPDF();
    const pageHeight = doc.internal.pageSize.height;
    let y = 20;

    const addText = (text, indent = 20, lineHeight = 8, fontStyle = 'normal') => {
      doc.setFont('helvetica', fontStyle);
      const lines = doc.splitTextToSize(text, 170);
      lines.forEach((line) => {
        if (y + lineHeight > pageHeight - 10) {
          doc.addPage();
          y = 20;
        }
        doc.text(line, indent, y);
        y += lineHeight;
      });
    };

    doc.setFontSize(16);
    addText('Resume Ranker', 20, 10, 'bold');
    y += 10;

    doc.setFontSize(14);
    addText('Match Score:', 20, 8, 'bold');
    addText(`${analysisResult.match_score || analysisResult.matchScore}`);
    y += 6;

    if (analysisType === 'smart' && analysisResult.resume_keywords) {
      addText('Resume Keywords:', 20, 8, 'bold');
      addText(analysisResult.resume_keywords.join(', '));
      y += 6;
    }

    addText('Job Keywords:', 20, 8, 'bold');
    addText((analysisResult.job_keywords || analysisResult.jobKeywords)?.join(', '));
    y += 6;

    const missingSkills = analysisResult.missing_skills || analysisResult.missingKeywords;
    addText('Missing Skills:', 20, 8, 'bold');
    if (!missingSkills?.length) {
      addText('None 🎯', 25);
    } else {
      missingSkills.forEach((skill) => addText(`- ${skill}`, 25));
    }
    y += 6;

    if (analysisResult.feedback?.length) {
      addText('Feedback:', 20, 8, 'bold');
      analysisResult.feedback.forEach((tip) => addText(`- ${tip}`, 25));
      y += 6;
    }

    if (analysisResult.strengths_summary) {
      addText('Strengths Summary:', 20, 8, 'bold');
      addText(analysisResult.strengths_summary);
    }

    if (analysisType === 'smart' && analysisResult.raw_scores) {
      addText('Raw Scores:', 20, 8, 'bold');
      Object.entries(analysisResult.raw_scores).forEach(([key, value]) => {
        addText(`- ${key}: ${value}`, 25);
      });
    }

    doc.save('resume_analysis.pdf');
  };

  const handleAnalysis = async () => {
    if (!resumeFile || !jobDescription.trim()) {
      alert('Please upload a resume and enter a job description.');
      return;
    }

    setLoading(true);

    try {
      const formData = new FormData();
      formData.append('file', resumeFile);

    //   const uploadRes = await axios.post(
    //     `${process.env.REACT_APP_API_BASE_URL}/smart/api/resumes/upload`,
    //     formData,
    //     {
    //       headers: {
    //         Authorization: `Bearer ${token}`,
    //         'Content-Type': 'multipart/form-data',
    //       },
    //     }
    //   );

    const uploadRes = await axios.post(
  `${process.env.REACT_APP_API_BASE_URL}/smart/api/resumes/upload`,
  formData,
  {
    withCredentials: true, // 👈 Required to send HttpOnly cookie
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  }
);

      const resumeText = uploadRes.data;

      const payload = {
        resume: resumeText,
        jobDescription,
      };

      const endpoint =
        analysisType === 'smart'
          ? `${process.env.REACT_APP_API_BASE_URL}/smart/api/resumes/analyze`
          : `${process.env.REACT_APP_API_BASE_URL}/api/resumes/analyze`;

    //   const analysisRes = await axios.post(endpoint, payload, {
    //     headers: { Authorization: `Bearer ${token}` },
    //   });

    const analysisRes = await axios.post(endpoint, payload, {
  withCredentials: true, // 👈 Required to send HttpOnly cookie
});

      let resultData = analysisRes.data;

    //   if (analysisType === 'smart' && typeof resultData === 'string') {
    //     try {
    //       const cleaned = resultData
    //         .replace(/^```json/, '')
    //         .replace(/^```/, '')
    //         .replace(/```$/, '')
    //         .trim();

    //       resultData = JSON.parse(cleaned);
    //     } catch (err) {
    //       console.error('Failed to parse smart analysis JSON:', err);
    //       setAnalysisResult({ error: 'Smart analysis failed to parse. Please try again.' });
    //       return;
    //     }
    //   }

   //   let resultData = analysisRes.data;

if (analysisType === 'smart' && typeof resultData === 'string') {
  try {
    // Remove Markdown formatting and extract only the JSON block
    const start = resultData.indexOf('{');
    const end = resultData.lastIndexOf('}');
    const jsonString = resultData.slice(start, end + 1).trim();

    resultData = JSON.parse(jsonString);
  } catch (err) {
    console.error('Failed to parse smart analysis JSON:', err);
    setAnalysisResult({ error: 'Smart analysis failed to parse. Please try again.' });
    return;
  }
}


      setAnalysisResult(resultData);
    } catch (error) {
      console.error('Analysis failed:', error);
      setAnalysisResult({ error: 'Error performing analysis. Please try again.' });
    } finally {
      setLoading(false);
    }
  };

  const renderResult = () => {
    if (!analysisResult) {
      return <p>No analysis performed yet. Upload your resume and paste a job description to begin.</p>;
    }

    if (analysisResult.error) {
      return <p>{analysisResult.error}</p>;
    }

    const resultData = analysisResult;

    if (analysisType === 'smart') {
      const {
        match_score,
        resume_keywords,
        job_keywords,
        missing_skills,
        feedback,
        strengths_summary,
        raw_scores,
        score_breakdown,
      } = resultData;

      console.log('Smart Analysis Result:', resultData);

      return (
        <>
          <p><strong>Match Score:</strong> {match_score}%</p>

          {score_breakdown && (
            <>
              <p><strong>Score Breakdown:</strong></p>
              <ul>
                {Object.entries(score_breakdown).map(([key, value], idx) => (
                  <li key={idx}>{`${key.replace(/_/g, ' ')}: ${value}%`}</li>
                ))}
              </ul>
            </>
          )}

          {/* {resume_keywords?.length > 0 && (
            <>
              <p><strong>Resume Keywords:</strong></p>
              <ul>{resume_keywords.map((kw, idx) => <li key={idx}>{kw}</li>)}</ul>
            </>
          )} */}

          {job_keywords?.length > 0 && (
            <>
              <p><strong>Job Keywords:</strong></p>
              <ul>{job_keywords.map((kw, idx) => <li key={idx}>{kw}</li>)}</ul>
            </>
          )}

          <p><strong>Missing Skills:</strong></p>
          {missing_skills?.length === 0 ? (
            <p>None 🎯</p>
          ) : (
            <ul>{missing_skills.map((skill, idx) => <li key={idx}>{skill}</li>)}</ul>
          )}

          {feedback && (
            <>
              <p><strong>Feedback:</strong></p>
              {Array.isArray(feedback) ? (
                <ul>{feedback.map((tip, idx) => <li key={idx}>{tip}</li>)}</ul>
              ) : (
                <p>{feedback}</p>
              )}
            </>
          )}

          {strengths_summary && (
            <p><strong>Strengths Summary:</strong> {strengths_summary}</p>
          )}

          {raw_scores && (
            <>
              <p><strong>Raw Scores:</strong></p>
              <ul>{Object.entries(raw_scores).map(([key, value], idx) => (
                <li key={idx}>{`${key}: ${value}`}</li>
              ))}</ul>
            </>
          )}

          <button className="pdf-btn" onClick={handleDownloadPDF}>Download PDF</button>
        </>
      );
      };
  
      // Normal Analysis fallback
      const {
        matchScore,
        jobKeywords,
        missingKeywords,
        suggestions,
      } = resultData;
  
      return (
        <>
          <p><strong>Match Score:</strong> {(matchScore*100).toFixed(2)}%</p>
  
          {jobKeywords?.length > 0 && (
            <>
              <p><strong>Job Keywords:</strong></p>
              <ul>{jobKeywords.map((kw, idx) => <li key={idx}>{kw}</li>)}</ul>
            </>
          )}
  
          <p><strong>Missing Keywords:</strong></p>
          {missingKeywords?.length === 0 ? (
            <p>None 🎯</p>
          ) : (
            <ul>{missingKeywords.map((kw, idx) => <li key={idx}>{kw}</li>)}</ul>
          )}
  
          {suggestions?.length > 0 && (
            <>
              <p><strong>Suggestions:</strong></p>
              <ul>{suggestions.map((tip, idx) => <li key={idx}>{tip}</li>)}</ul>
            </>
          )}
  
          <button className="pdf-btn" onClick={handleDownloadPDF}>Download PDF</button>
        </>
      );
    };
  
    return (
      <div className="home-wrapper">
        <h1 className="title">📄 Resume Ranker</h1>
        <button
  onClick={handleLogout}
  style={{
    padding: '10px 20px',
    backgroundColor: '#e74c3c',
    color: '#fff',
    border: 'none',
    borderRadius: '5px',
    cursor: 'pointer',
    margin: '20px',
    width: '100px'
  }}
>
  Logout
</button>
        <div className="split-container">
          <div className="input-panel">
            <div className="form-group">
              <label className="label">Upload Resume</label>
              <input type="file" accept=".pdf,.doc,.docx,.txt" onChange={handleFileChange} />
            </div>
  
            <div className="form-group">
              <label className="label">Paste Job Description</label>
              <textarea
                rows="10"
                placeholder="Paste job description here..."
                value={jobDescription}
                onChange={handleDescriptionChange}
              />
            </div>
  
            <div className="form-group">
              <label className="label">Select Analysis Type</label>
              {/* <select value={analysisType} onChange={handleTypeChange}>
                <option value="smart">Smart Analysis (Gemini)</option>
                <option value="normal">Normal Analysis</option>
              </select> */}
              <select
  value={analysisType}
  onChange={handleTypeChange}
  style={{
    padding: '10px 12px',
    fontSize: '16px',
    borderRadius: '6px',
    border: '1px solid #ccc',
    backgroundColor: '#f9f9f9',
    color: '#333',
    outline: 'none',
    boxShadow: '0 2px 4px rgba(0,0,0,0.05)',
    marginBottom: '20px',
    cursor: 'pointer',
    transition: 'border-color 0.3s ease'
  }}
  onFocus={(e) => (e.target.style.borderColor = '#007bff')}
  onBlur={(e) => (e.target.style.borderColor = '#ccc')}
>
  <option value="smart">Smart Analysis (Gemini)</option>
  <option value="normal">Normal Analysis</option>
</select>
            </div>
  
            <button className="analyze-btn" onClick={handleAnalysis} disabled={loading}>
              {loading ? 'Analyzing...' : 'Perform Analysis'}
            </button>
          </div>
  
          <div className="result-panel">
            <div className="result-section">
              <h3>🔍 Analysis Result</h3>
              {renderResult()}
            </div>
          </div>
        </div>
      </div>
    );
  };
  
  export default HomePage;
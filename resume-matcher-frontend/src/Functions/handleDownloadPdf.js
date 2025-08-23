import { jsPDF } from 'jspdf';

const handleDownloadPDF = () => {
  const doc = new jsPDF();
  const pageHeight = doc.internal.pageSize.height;
  let y = 20;

  const addText = (text, indent = 20, lineHeight = 8) => {
    const lines = doc.splitTextToSize(text, 170); // wrap at 170mm width
    lines.forEach((line) => {
      if (y + lineHeight > pageHeight - 10) {
        doc.addPage();
        y = 20;
      }
      doc.text(line, indent, y);
      y += lineHeight;
    });
  };

  doc.setFontSize(14);
  addText('Resume Analysis Report', 20, 10);
  y += 10;

  addText(`Match Score: ${analysisResult.match_score || analysisResult.matchScore}`);

  if (analysisType === 'smart' && analysisResult.resume_keywords) {
    addText(`Resume Keywords: ${analysisResult.resume_keywords.join(', ')}`);
  }

  addText(`Job Keywords: ${(analysisResult.job_keywords || analysisResult.jobKeywords)?.join(', ')}`);

  addText('Missing Skills:');
  (analysisResult.missing_skills || analysisResult.missingKeywords)?.forEach((skill) => {
    addText(`- ${skill}`, 25);
  });

  if (analysisResult.feedback) {
    addText('Feedback:');
    analysisResult.feedback.forEach((tip) => {
      addText(`- ${tip}`, 25);
    });
  }

  if (analysisResult.strengths_summary) {
    addText(`Strengths Summary: ${analysisResult.strengths_summary}`);
  }

  doc.save('resume_analysis.pdf');
};

export default handleDownloadPDF;
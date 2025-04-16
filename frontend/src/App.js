import React, { useState } from "react";
import UploadForm from "./components/UploadForm";
import AnalysisResult from "./components/AnalysisResult";

function App() {
  const [analysis, setAnalysis] = useState(null);

  return (
    <div className="min-h-screen bg-gray-50 px-6 py-10">
      <h1 className="text-3xl font-bold text-center text-indigo-800 mb-8">
        Legacy Optimizer - Java Analyzer
      </h1>

      <UploadForm setAnalysis={setAnalysis} />
      <AnalysisResult analysis={analysis} />
    </div>
  );
}

export default App;

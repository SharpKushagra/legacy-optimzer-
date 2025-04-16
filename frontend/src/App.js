// Step 1: App layout with Tailwind and Java File Upload to Spring Boot backend

import React, { useState } from "react";

function App() {
  const [file, setFile] = useState(null);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleUpload = async () => {
    if (!file) return;
    setLoading(true);
    setResult(null);
    setError(null);

    const formData = new FormData();
    formData.append("file", file);

    try {
      const res = await fetch("http://localhost:8080/api/analyze/java", {
        method: "POST",
        body: formData,
      });
      const data = await res.json();
      setResult(data);
    } catch (err) {
      setError("Something went wrong.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 p-6">
      <div className="max-w-3xl mx-auto bg-white shadow-md rounded-xl p-6">
        <h1 className="text-3xl font-bold text-center text-blue-600 mb-4">
          Legacy Optimizer
        </h1>
        <div className="mb-4">
          <input
            type="file"
            accept=".java"
            onChange={handleFileChange}
            className="w-full px-4 py-2 border rounded"
          />
        </div>
        <button
          onClick={handleUpload}
          className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition"
        >
          Analyze Java File
        </button>

        {loading && <p className="mt-4 text-blue-500">Analyzing file...</p>}
        {error && <p className="mt-4 text-red-500">{error}</p>}

        {result && (
          <div className="mt-6">
            <h2 className="text-xl font-semibold mb-2">Analysis Result:</h2>
            <div className="space-y-2">
              <p><strong>Filename:</strong> {result.filename}</p>
              <p><strong>Package:</strong> {result.package}</p>
              <p><strong>Total Lines:</strong> {result.totalLines}</p>
              <p><strong>Technical Debt Score:</strong> {result.technicalDebtScore}</p>

              <div>
                <strong>Classes:</strong>
                <ul className="list-disc pl-6">
                  {result.classes.map((cls, i) => <li key={i}>{cls}</li>)}
                </ul>
              </div>

              <div>
                <strong>Methods:</strong>
                <ul className="list-disc pl-6">
                  {result.methods.map((mtd, i) => <li key={i}>{mtd}</li>)}
                </ul>
              </div>

              <div>
                <strong>Imports:</strong>
                <ul className="list-disc pl-6">
                  {result.imports.map((imp, i) => <li key={i}>{imp}</li>)}
                </ul>
              </div>

              <div>
                <strong>Cyclomatic Complexity:</strong>
                <ul className="list-disc pl-6">
                  {Object.entries(result.cyclomaticComplexity).map(([method, score], i) => (
                    <li key={i}>{method}: {score}</li>
                  ))}
                </ul>
              </div>

              <div>
                <strong>Duplicate Methods:</strong>
                <ul className="list-disc pl-6">
                  {result.duplicateMethods.map((dm, i) => <li key={i}>{dm}</li>)}
                </ul>
              </div>

            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;

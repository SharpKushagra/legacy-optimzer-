// src/components/AnalysisResult.jsx
import React from "react";

export default function AnalysisResult({ analysis }) {
  if (!analysis) return null;

  return (
    <div className="bg-white shadow-md rounded p-6 mt-6">
      <h2 className="text-xl font-semibold mb-4">Analysis Result</h2>
      
      <p><strong>Filename:</strong> {analysis.filename}</p>
      <p><strong>Package:</strong> {analysis.package}</p>
      <p><strong>Total Lines:</strong> {analysis.totalLines}</p>
      
      <div className="mt-4">
        <strong>Imports:</strong>
        <ul className="list-disc list-inside">
          {analysis.imports.map((imp, i) => <li key={i}>{imp}</li>)}
        </ul>
      </div>

      <div className="mt-4">
        <strong>Classes:</strong>
        <ul className="list-disc list-inside">
          {analysis.classes.map((cls, i) => <li key={i}>{cls}</li>)}
        </ul>
      </div>

      <div className="mt-4">
        <strong>Methods:</strong>
        <ul className="list-disc list-inside">
          {analysis.methods.map((method, i) => <li key={i}>{method}</li>)}
        </ul>
      </div>

      <div className="mt-4">
        <strong>Cyclomatic Complexity:</strong>
        <ul className="list-disc list-inside">
          {Object.entries(analysis.cyclomaticComplexity).map(([method, value], i) => (
            <li key={i}>{method}: {value}</li>
          ))}
        </ul>
      </div>

      <p className="mt-4"><strong>Technical Debt Score:</strong> {analysis.technicalDebtScore}</p>

      <div className="mt-4">
        <strong>Duplicate Methods:</strong>
        <ul className="list-disc list-inside">
          {analysis.duplicateMethods.map((dup, i) => <li key={i}>{dup}</li>)}
        </ul>
      </div>
    </div>
  );
}

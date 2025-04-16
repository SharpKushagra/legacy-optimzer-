// src/components/ResultCard.jsx
import React from "react";

export default function ResultCard({ data }) {
  if (!data) return null;

  return (
    <div className="bg-white shadow-lg rounded-xl p-6 w-full max-w-3xl">
      <h2 className="text-2xl font-bold text-indigo-600 mb-4">Analysis Report</h2>
      <div className="space-y-2">
        <p><span className="font-semibold">Filename:</span> {data.filename}</p>
        <p><span className="font-semibold">Package:</span> {data.package}</p>
        <p><span className="font-semibold">Total Lines:</span> {data.totalLines}</p>

        <div>
          <span className="font-semibold">Classes:</span>
          <ul className="list-disc list-inside ml-4">
            {data.classes.map((cls, idx) => <li key={idx}>{cls}</li>)}
          </ul>
        </div>

        <div>
          <span className="font-semibold">Methods:</span>
          <ul className="list-disc list-inside ml-4">
            {data.methods.map((m, idx) => <li key={idx}>{m}</li>)}
          </ul>
        </div>

        <div>
          <span className="font-semibold">Imports:</span>
          <ul className="list-disc list-inside ml-4">
            {data.imports.map((imp, idx) => <li key={idx}>{imp}</li>)}
          </ul>
        </div>

        <div>
          <span className="font-semibold">Cyclomatic Complexity:</span>
          <ul className="list-disc list-inside ml-4">
            {Object.entries(data.cyclomaticComplexity).map(([method, score]) => (
              <li key={method}>{method}: {score}</li>
            ))}
          </ul>
        </div>

        <div>
          <span className="font-semibold">Duplicate Methods:</span>
          <ul className="list-disc list-inside ml-4">
            {data.duplicateMethods.length > 0
              ? data.duplicateMethods.map((dup, idx) => <li key={idx}>{dup}</li>)
              : <li>None</li>}
          </ul>
        </div>

        <p><span className="font-semibold">Technical Debt Score:</span> {data.technicalDebtScore}</p>
      </div>
    </div>
  );
}

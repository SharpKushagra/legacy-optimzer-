import React, { useState } from "react";
import axios from "axios";

export default function UploadForm({ setAnalysis }) {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file) return alert("Please upload a .java file");

    const formData = new FormData();
    formData.append("file", file);
    setLoading(true);

    try {
      const res = await axios.post("http://localhost:8080/api/analyze/java", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      setAnalysis(res.data);
      setFile(null); // Optional: Reset after success
    } catch (err) {
      const message = err.response?.data?.error || "Failed to analyze file. Check server.";
      alert(message);
      console.error("Error:", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-4 mb-6">
      <input
        type="file"
        accept=".java"
        onChange={(e) => setFile(e.target.files[0])}
        className="file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-indigo-100 file:text-indigo-700 hover:file:bg-indigo-200"
      />
      <button
        type="submit"
        className="bg-indigo-600 text-white px-6 py-2 rounded hover:bg-indigo-700 disabled:opacity-50"
        disabled={loading}
      >
        {loading ? "Analyzing..." : "Upload & Analyze"}
      </button>
    </form>
  );
}

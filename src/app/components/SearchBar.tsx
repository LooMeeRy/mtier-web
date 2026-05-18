"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

export default function SearchBar() {
  const [username, setUsername] = useState("");
  const router = useRouter();

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (username.trim()) {
      router.push(`/player/${username.trim()}`);
      setUsername("");
    }
  };

  return (
    <form onSubmit={handleSearch} className="relative group w-full max-w-sm">
      <div className="absolute -inset-0.5 bg-gradient-to-r from-orange-500/20 to-orange-600/20 rounded-xl blur opacity-25 group-hover:opacity-50 transition duration-500"></div>
      <div className="relative flex items-center bg-[#121214]/80 border border-zinc-800 rounded-xl px-4 py-2 backdrop-blur-md">
        <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4 text-zinc-600 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <input
          type="text"
          placeholder="Lookup player..."
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          className="flex-1 bg-transparent outline-none text-xs font-black text-zinc-100 placeholder:text-zinc-800 tracking-widest"
        />
        <button type="submit" className="hidden">Search</button>
      </div>
    </form>
  );
}

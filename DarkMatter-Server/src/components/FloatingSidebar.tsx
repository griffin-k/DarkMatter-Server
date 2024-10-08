import React from 'react';

const Sidebar: React.FC = () => {
    return (
        <div className="relative w-screen h-screen">
            <nav className="z-20 flex flex-col items-center justify-center gap-6 border-t border-gray-200 bg-white/50 p-4 shadow-lg backdrop-blur-lg dark:border-slate-600/60 dark:bg-slate-800/50 fixed top-1/2 -translate-y-1/2 left-6 min-h-auto min-w-[64px] rounded-lg">
                
                <a href="#home" className="flex aspect-square min-h-[32px] w-16 flex-col items-center justify-center gap-1 rounded-md p-2 bg-indigo-50 text-indigo-600 dark:bg-sky-900 dark:text-sky-50">
                    {/* HeroIcon - Home */}
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth="1.5" stroke="currentColor" className="w-6 h-6 shrink-0">
                        <path strokeLinecap="round" strokeLinejoin="round" d="M3 9l9-6 9 6v9a2 2 0 01-2 2h-4a2 2 0 01-2-2v-3a2 2 0 00-2-2H9a2 2 0 00-2 2v3a2 2 0 01-2 2H3a2 2 0 01-2-2V9z" />
                    </svg>
                    <small className="text-center text-xs font-medium">Home</small>
                </a>

                <a href="#analytics" className="flex aspect-square min-h-[32px] w-16 flex-col items-center justify-center gap-1 rounded-md p-2 text-gray-700 hover:bg-gray-100 dark:text-gray-400 dark:hover:bg-slate-800">
                    {/* HeroIcon - Chart Bar */}
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth="1.5" stroke="currentColor" className="w-6 h-6 shrink-0">
                        <path strokeLinecap="round" strokeLinejoin="round" d="M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 013 19.875v-6.75zM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25c-.621 0-1.125-.504-1.125-1.125V8.625zM15.75 3.125c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v16.25c0 .621-.504 1.125-1.125 1.125h-2.25c-.621 0-1.125-.504-1.125-1.125V3.125z" />
                    </svg>
                    <small className="text-center text-xs font-medium">Analytics</small>
                </a>

                <a href="#devices" className="flex aspect-square min-h-[32px] w-16 flex-col items-center justify-center gap-1 rounded-md p-2 text-gray-700 hover:bg-gray-100 dark:text-gray-400 dark:hover:bg-slate-800">
                    {/* HeroIcon - Device */}
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth="1.5" stroke="currentColor" className="w-6 h-6 shrink-0">
                        <path strokeLinecap="round" strokeLinejoin="round" d="M3 5a2 2 0 00-2 2v10a2 2 0 002 2h18a2 2 0 002-2V7a2 2 0 00-2-2H3z" />
                    </svg>
                    <small className="text-center text-xs font-medium">Devices</small>
                </a>

                <a href="#profile" className="flex aspect-square min-h-[32px] w-16 flex-col items-center justify-center gap-1 rounded-md p-2 text-gray-700 hover:bg-gray-100 dark:text-gray-400 dark:hover:bg-slate-800">
                    {/* HeroIcon - User */}
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth="1.5" stroke="currentColor" className="w-6 h-6 shrink-0">
                        <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z" />
                    </svg>
                    <small className="text-center text-xs font-medium">Profile</small>
                </a>
            </nav>
        </div>
    );
};

export default Sidebar;

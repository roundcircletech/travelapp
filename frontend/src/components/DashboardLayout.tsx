import React from 'react';
import WorkflowList from './WorkflowList';

interface DashboardLayoutProps {
    children: React.ReactNode;
    onSelectWorkflow?: (id: string) => void;
    activeWorkflowId?: string;
}

const DashboardLayout: React.FC<DashboardLayoutProps> = ({ children, onSelectWorkflow, activeWorkflowId }) => {
    return (
        <div className="flex h-screen bg-gray-50 font-sans text-gray-900">
            {/* Sidebar */}
            <aside className="w-64 bg-white border-r border-gray-200 hidden md:flex flex-col">
                <div className="p-6 border-b border-gray-100">
                    <div className="flex items-center gap-2 text-brand-primary font-bold text-xl">
                        <svg className="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                        Wyz Travel Agent
                    </div>
                </div>

                <nav className="flex-1 overflow-y-auto">
                    {/* Active Workflows Section */}
                    {onSelectWorkflow && (
                        <WorkflowList
                            onSelectWorkflow={onSelectWorkflow}
                            activeWorkflowId={activeWorkflowId}
                        />
                    )}
                </nav>

                <div className="p-4 border-t border-gray-100">
                    <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-full bg-brand-primary text-white flex items-center justify-center font-bold">
                            A
                        </div>
                        <div className="text-sm">
                            <p className="font-medium">Advait</p>
                            <p className="text-gray-500 text-xs">Senior Agent</p>
                        </div>
                    </div>
                </div>
            </aside>

            {/* Main Content */}
            <main className="flex-1 overflow-auto">
                {children}
            </main>
        </div>
    );
};

export default DashboardLayout;

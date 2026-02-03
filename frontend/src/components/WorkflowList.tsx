import React, { useEffect, useState } from 'react';

interface Workflow {
    id: string;
    customerName: string;
    finished: boolean;
    steps: any[];
}

interface WorkflowListProps {
    onSelectWorkflow: (id: string) => void;
    activeWorkflowId?: string;
}

const WorkflowList: React.FC<WorkflowListProps> = ({ onSelectWorkflow, activeWorkflowId }) => {
    const [workflows, setWorkflows] = useState<Workflow[]>([]);

    useEffect(() => {
        fetchWorkflows();
        const interval = setInterval(fetchWorkflows, 5000); // Poll for new bookings
        return () => clearInterval(interval);
    }, []);

    const fetchWorkflows = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/workflows');
            const data = await response.json();
            // Sort by ID descending (newest first)
            // Assuming ID is somewhat time-based or appended. 
            // Better: if backend had createdAt. For now reverse list.
            setWorkflows(data.reverse());
        } catch (error) {
            console.error("Failed to fetch workflows", error);
        }
    };

    return (
        <div className="space-y-2 p-2">
            <h3 className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-3 px-2">
                Recent Bookings
            </h3>
            {workflows.map((wf) => {
                // Heuristic for Booking Type Theme
                const name = wf.customerName.toLowerCase();
                let themeBg = 'bg-indigo-50 text-indigo-900';
                let themeBorder = 'border-indigo-400';
                let icon = '✈️';

                if (name.includes('honeymoon') || name.includes('romantic')) {
                    themeBg = 'bg-pink-50 text-pink-900';
                    themeBorder = 'border-pink-400';
                    icon = '💍';
                } else if (name.includes('business') || name.includes('work') || name.includes('conference')) {
                    themeBg = 'bg-slate-50 text-slate-900';
                    themeBorder = 'border-slate-500';
                    icon = '💼';
                } else if (name.includes('family') || name.includes('kids')) {
                    themeBg = 'bg-orange-50 text-orange-900';
                    themeBorder = 'border-orange-400';
                    icon = '👨‍👩‍👧‍👦';
                } else if (name.includes('adventure') || name.includes('hike') || name.includes('ski')) {
                    themeBg = 'bg-emerald-50 text-emerald-900';
                    themeBorder = 'border-emerald-500';
                    icon = '🏔️';
                }

                // If Confirmed, override EVERYTHING to Green/Success
                if (wf.finished) {
                    themeBg = 'bg-green-50 text-green-900';
                    themeBorder = 'border-green-500';
                }

                const finalBorder = `border-l-4 ${themeBorder}`;

                return (
                    <div
                        key={wf.id}
                        onClick={() => onSelectWorkflow(wf.id)}
                        className={`
                            group flex items-center justify-between px-3 py-3 rounded-r-lg cursor-pointer transition-all shadow-sm mb-2
                            ${finalBorder} ${themeBg}
                            ${activeWorkflowId === wf.id ? 'ring-2 ring-indigo-500/20' : 'hover:shadow-md opacity-90 hover:opacity-100'}
                        `}
                    >
                        <div className="flex items-center gap-3 overflow-hidden">
                            <span className="text-lg">{icon}</span>
                            <div className="flex flex-col min-w-0">
                                <span className={`font-semibold text-sm truncate ${activeWorkflowId === wf.id ? 'text-gray-900' : 'text-gray-700'}`}>
                                    {wf.customerName}
                                </span>
                                <span className="text-[10px] text-gray-400 uppercase tracking-wide">
                                    {wf.finished ? 'Confirmed' : 'Draft'}
                                </span>
                            </div>
                        </div>

                        {wf.finished && (
                            <div className="w-2 h-2 rounded-full bg-green-500 shadow-sm" title="Confirmed" />
                        )}
                    </div>
                );
            })}

            {workflows.length === 0 && (
                <div className="text-xs text-gray-400 px-3 italic">
                    No bookings found
                </div>
            )}
        </div>
    );
};

export default WorkflowList;

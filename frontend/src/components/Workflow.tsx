import React, { useState, useEffect } from 'react';
import SmartCreateModal from './SmartCreateModal';
import StepDetailsForm from './StepDetailsForm';
import { DndContext, closestCenter, KeyboardSensor, PointerSensor, useSensor, useSensors } from '@dnd-kit/core';
import type { DragEndEvent } from '@dnd-kit/core';
import { arrayMove, SortableContext, sortableKeyboardCoordinates, verticalListSortingStrategy } from '@dnd-kit/sortable';
import SortableItem from './SortableItem';

interface Step {
    id: string;
    name: string;
    description: string;
    status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';
    isCompleted: boolean;
    metadata: Record<string, any>;
    warning?: string;
    alternative?: string;
}

interface Workflow {
    id: string;
    customerName: string;
    steps: Step[];
    finished: boolean;
}

interface WorkflowProps {
    activeWorkflowId?: string;
    onWorkflowChange?: (id: string) => void;
}

const Workflow: React.FC<WorkflowProps> = ({ activeWorkflowId, onWorkflowChange }) => {
    const [workflow, setWorkflow] = useState<Workflow | null>(null);
    const [isSmartModalOpen, setIsSmartModalOpen] = useState(false);
    const [selectedStep, setSelectedStep] = useState<Step | null>(null);

    const sensors = useSensors(
        useSensor(PointerSensor, {
            activationConstraint: {
                distance: 8, // Requires 8px movement before drag starts, allowing clicks
            },
        }),
        useSensor(KeyboardSensor, {
            coordinateGetter: sortableKeyboardCoordinates,
        })
    );

    // Fetch from API with polling
    useEffect(() => {
        if (activeWorkflowId) {
            fetchWorkflows();
            const interval = setInterval(fetchWorkflows, 5000);
            return () => clearInterval(interval);
        } else {
            setWorkflow(null); // Clear workflow if no ID
        }
    }, [activeWorkflowId]); // Re-fetch when ID changes

    const fetchWorkflows = async () => {
        if (selectedStep || !activeWorkflowId) return; // Don't poll while editing a step

        try {
            const response = await fetch(`http://localhost:8080/api/workflows/${activeWorkflowId}`);
            if (response.ok) {
                const data = await response.json();
                setWorkflow(data);
            }
        } catch (error) {
            console.error('Error fetching workflows:', error);
        }
    };

    const handleStepClick = (step: Step) => {
        setSelectedStep(step);
    };

    const handleSaveStepDetails = async (stepId: string, metadata: any) => {
        if (!workflow) return;

        let nextStepIndex = -1;
        const newSteps = workflow.steps.map((step, index) => {
            if (step.id === stepId) {
                // Mark current as completed
                nextStepIndex = index + 1;
                return {
                    ...step,
                    metadata,
                    status: 'COMPLETED' as const,
                    isCompleted: true
                };
            }
            return step;
        });

        // Auto-advance: Mark next PENDING step as IN_PROGRESS
        if (nextStepIndex > 0 && nextStepIndex < newSteps.length) {
            if (newSteps[nextStepIndex].status === 'PENDING') {
                newSteps[nextStepIndex] = {
                    ...newSteps[nextStepIndex],
                    status: 'IN_PROGRESS'
                };
            }
        }

        updateWorkflowState(newSteps);
        setSelectedStep(null);
    };

    const handleDragEnd = (event: DragEndEvent) => {
        const { active, over } = event;

        if (workflow && over && active.id !== over.id) {
            const oldIndex = workflow.steps.findIndex((step) => step.id === active.id);
            const newIndex = workflow.steps.findIndex((step) => step.id === over.id);

            const newSteps = arrayMove(workflow.steps, oldIndex, newIndex);
            setWorkflow({ ...workflow, steps: newSteps });
            updateWorkflowState(newSteps);
        }
    };

    const updateWorkflowState = async (newSteps: Step[]) => {
        if (!workflow) return;

        const updatedWorkflow = { ...workflow, steps: newSteps };
        setWorkflow(updatedWorkflow);

        try {
            await fetch(`http://localhost:8080/api/workflows/${workflow.id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(updatedWorkflow)
            });
        } catch (error) {
            console.error('Error updating workflow:', error);
        }
    };

    if (!activeWorkflowId || !workflow) {
        return (
            <div className="flex flex-col items-center justify-center h-full p-8 text-center text-gray-500">
                <SmartCreateModal
                    isOpen={isSmartModalOpen}
                    onClose={() => setIsSmartModalOpen(false)}
                    onCreated={(newId) => {
                        if (onWorkflowChange) {
                            onWorkflowChange(newId);
                        }
                        setIsSmartModalOpen(false);
                    }}
                />
                <div className="max-w-md">
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">Welcome to TravelAgent</h2>
                    <p className="mb-8">Select a booking from the history sidebar or create a new one to get started.</p>
                    <button
                        onClick={() => setIsSmartModalOpen(true)}
                        className="px-6 py-3 bg-brand-primary text-white font-bold rounded-lg shadow-lg hover:bg-indigo-700 transition-all transform hover:scale-105"
                    >
                        + Create New Booking
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="max-w-4xl mx-auto p-8">
            <div className="flex justify-between items-center mb-8">
                <div>
                    <h1 className="text-3xl font-bold mb-2">Booking Workflow</h1>
                    <p className="text-gray-600">Managing: {workflow.customerName}</p>
                </div>
                <button
                    onClick={() => setIsSmartModalOpen(true)}
                    className="px-4 py-2 bg-indigo-50 text-brand-primary font-semibold rounded-lg hover:bg-indigo-100 transition-colors"
                >
                    + New Booking
                </button>
            </div>

            <SmartCreateModal
                isOpen={isSmartModalOpen}
                onClose={() => setIsSmartModalOpen(false)}
                onCreated={async (newId) => {
                    if (onWorkflowChange) {
                        onWorkflowChange(newId);
                    }
                    setIsSmartModalOpen(false);
                }}
            />

            {selectedStep && (
                <StepDetailsForm
                    step={selectedStep}
                    onSave={handleSaveStepDetails}
                    onClose={() => setSelectedStep(null)}
                />
            )}

            <DndContext
                sensors={sensors}
                collisionDetection={closestCenter}
                onDragEnd={handleDragEnd}
            >
                <SortableContext
                    items={workflow.steps.map(s => s.id)}
                    strategy={verticalListSortingStrategy}
                >
                    <div className="space-y-4">
                        {workflow.steps.map((step, index) => (
                            <SortableItem key={step.id} id={step.id}>
                                <div
                                    className={`flex items-center p-6 bg-white rounded-xl shadow-sm border transition-all duration-200 cursor-pointer hover:shadow-md
                                        ${step.status === 'COMPLETED' ? 'border-green-500 bg-green-50' :
                                            step.status === 'IN_PROGRESS' ? 'border-blue-500 border-l-4' : 'border-gray-200 opacity-70'}`}
                                    onClick={() => handleStepClick(step)}
                                >
                                    <div className={`
                                        flex items-center justify-center w-10 h-10 rounded-full mr-5 font-bold font-mono
                                        ${step.status === 'COMPLETED' ? 'bg-green-500 text-white' :
                                            step.status === 'IN_PROGRESS' ? 'bg-blue-500 text-white' : 'bg-gray-200 text-gray-500'}
                                    `}>
                                        {index + 1}
                                    </div>

                                    <div className="flex-1">
                                        <div className="flex items-center gap-2">
                                            <h3 className="font-semibold text-lg">{step.name}</h3>
                                            {/* ADVISORY WARNING ICON */}
                                            {step.warning && (
                                                <div className="px-2 py-0.5 bg-red-100 text-red-600 text-xs font-bold rounded-full flex items-center animate-pulse" title="Travel Advisory Alert">
                                                    ⚠️ Alert
                                                </div>
                                            )}
                                        </div>
                                        <div className="text-sm text-gray-500">
                                            {step.metadata && (step.metadata.flightNumber || step.metadata.hotelName) ? (
                                                <span className="font-mono text-indigo-600">
                                                    {step.metadata.flightNumber ? `Flight: ${step.metadata.flightNumber}` : ''}
                                                    {step.metadata.hotelName ? `Hotel: ${step.metadata.hotelName}` : ''}
                                                </span>
                                            ) : (
                                                step.description
                                            )}
                                        </div>
                                    </div>

                                    <div className="text-sm font-medium">
                                        {step.status === 'COMPLETED' && <span className="text-green-600">Done</span>}
                                        {step.status === 'IN_PROGRESS' && <span className="text-blue-600">In Progress</span>}
                                        {step.status === 'PENDING' && <span className="text-gray-400">Pending</span>}
                                    </div>
                                </div>
                            </SortableItem>
                        ))}
                    </div>
                </SortableContext>
            </DndContext>

            <div className="mt-8 flex justify-between items-center bg-gray-50 p-4 rounded-xl border border-gray-200">
                <div className="flex items-center space-x-2">
                    <span className="text-sm font-semibold text-gray-500 uppercase tracking-wider">Booking Status:</span>
                    <span className={`px-3 py-1 rounded-full text-sm font-bold ${workflow.finished
                        ? 'bg-green-100 text-green-700'
                        : 'bg-yellow-100 text-yellow-700'
                        }`}>
                        {workflow.finished ? 'CONFIRMED' : 'DRAFT'}
                    </span>
                </div>

                <button
                    disabled={!workflow.steps.every(s => s.status === 'COMPLETED') || workflow.finished}
                    className={`px-6 py-3 font-bold rounded-lg shadow transition-all
                        ${!workflow.steps.every(s => s.status === 'COMPLETED') || workflow.finished
                            ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                            : 'bg-brand-primary text-white hover:bg-indigo-700 hover:scale-105 shadow-indigo-200'
                        }
                    `}
                    onClick={async () => {
                        const updatedWorkflow = { ...workflow, finished: true };
                        setWorkflow(updatedWorkflow);

                        try {
                            await fetch(`http://localhost:8080/api/workflows/${workflow.id}`, {
                                method: 'PUT',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify(updatedWorkflow)
                            });
                        } catch (error) {
                            console.error('Error finalizing workflow:', error);
                        }
                    }}
                >
                    {workflow.finished ? 'Booking Finalized' : 'Finalize Booking'}
                </button>
            </div>
        </div>
    );
};

export default Workflow;

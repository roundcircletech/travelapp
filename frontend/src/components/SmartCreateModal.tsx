import React, { useState } from 'react';

interface SmartCreateModalProps {
    isOpen: boolean;
    onClose: () => void;
    onCreated: (newWorkflowId: string) => void;
}

const SmartCreateModal: React.FC<SmartCreateModalProps> = ({ isOpen, onClose, onCreated }) => {
    const [text, setText] = useState('');
    const [loading, setLoading] = useState(false);

    if (!isOpen) return null;

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        try {
            const response = await fetch('http://localhost:8080/api/workflows/parse', {
                method: 'POST',
                headers: { 'Content-Type': 'text/plain' }, // Send raw text
                body: text
            });
            const data = await response.json();
            onCreated(data.id);
            onClose();
            setText('');
        } catch (error) {
            console.error('Failed to create smart workflow', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 backdrop-blur-sm">
            <div className="bg-white rounded-2xl w-full max-w-lg p-8 shadow-2xl transform transition-all">
                <h2 className="text-2xl font-bold mb-4 text-gray-800">New Smart Booking</h2>
                <p className="text-gray-500 mb-6">Describe the trip in plain English, and we'll build the plan.</p>

                <form onSubmit={handleSubmit}>
                    <textarea
                        className="w-full h-32 p-4 border border-gray-200 rounded-xl bg-gray-50 focus:bg-white focus:ring-2 focus:ring-brand-primary/20 focus:border-brand-primary transition-all resize-none text-gray-700 placeholder-gray-400"
                        placeholder="Example: I want a 7-day luxury honeymon in Bali. We love adventure sports like Scuba Diving and prefer 5-star villas. Budget is around $5000."
                        value={text}
                        onChange={(e) => setText(e.target.value)}
                        required
                    />

                    <div className="flex justify-end space-x-3 mt-6">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-5 py-2.5 text-gray-600 font-medium hover:bg-gray-100 rounded-lg transition-colors"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={loading}
                            className="px-6 py-2.5 bg-brand-primary text-white font-bold rounded-lg hover:bg-indigo-700 disabled:opacity-50 transition-all shadow-lg shadow-indigo-200"
                        >
                            {loading ? 'Analyzing...' : 'Create Itinerary'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default SmartCreateModal;

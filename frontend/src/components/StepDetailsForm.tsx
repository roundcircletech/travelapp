import React, { useState, useEffect } from 'react';

interface StepDetailsFormProps {
    step: any;
    onSave: (stepId: string, metadata: any) => void;
    onClose: () => void;
}

const StepDetailsForm: React.FC<StepDetailsFormProps> = ({ step, onSave, onClose }) => {
    const [metadata, setMetadata] = useState<any>(step.metadata || {});

    // Ensure we don't lose existing data
    useEffect(() => {
        setMetadata(step.metadata || {});
    }, [step]);

    const handleChange = (field: string, value: any) => {
        setMetadata((prev: any) => ({ ...prev, [field]: value }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSave(step.id, metadata);
    };

    // --- FORM SECTIONS ---

    const renderFlightFields = () => (
        <div className="space-y-4 animate-in fade-in slide-in-from-bottom-2 duration-300">
            <div className="bg-indigo-50 p-4 rounded-lg border border-indigo-100">
                <h3 className="text-indigo-800 font-semibold mb-2 flex items-center">
                    ✈️ Flight Details
                </h3>
                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Airline</label>
                        <input
                            type="text"
                            placeholder="e.g. Emirates"
                            className="w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm p-2 border"
                            value={metadata.airline || ''}
                            onChange={(e) => handleChange('airline', e.target.value)}
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Flight No.</label>
                        <input
                            type="text"
                            placeholder="e.g. EK202"
                            className="w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm p-2 border"
                            value={metadata.flightNumber || ''}
                            onChange={(e) => handleChange('flightNumber', e.target.value)}
                        />
                    </div>
                </div>
                <div className="grid grid-cols-2 gap-4 mt-3">
                    <div>
                        <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Departure</label>
                        <input
                            type="datetime-local"
                            className="w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm p-2 border"
                            value={metadata.departureTime || ''}
                            onChange={(e) => handleChange('departureTime', e.target.value)}
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Class</label>
                        <select
                            className="w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm p-2 border bg-white"
                            value={metadata.cabinClass || 'Economy'}
                            onChange={(e) => handleChange('cabinClass', e.target.value)}
                        >
                            <option value="Economy">Economy</option>
                            <option value="Premium Economy">Premium Economy</option>
                            <option value="Business">Business</option>
                            <option value="First">First Class</option>
                        </select>
                    </div>
                </div>
            </div>
        </div>
    );

    const renderHotelFields = () => (
        <div className="space-y-4 animate-in fade-in slide-in-from-bottom-2 duration-300">
            <div className="bg-emerald-50 p-4 rounded-lg border border-emerald-100">
                <h3 className="text-emerald-800 font-semibold mb-2 flex items-center">
                    🏨 Hotel Accommodation
                </h3>
                <div>
                    <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Hotel Name</label>
                    <input
                        type="text"
                        placeholder="e.g. The Ritz-Carlton"
                        className="w-full rounded-md border-gray-300 shadow-sm focus:border-emerald-500 focus:ring-emerald-500 sm:text-sm p-2 border"
                        value={metadata.hotelName || ''}
                        onChange={(e) => handleChange('hotelName', e.target.value)}
                    />
                </div>
                <div className="grid grid-cols-2 gap-4 mt-3">
                    <div>
                        <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Check-in</label>
                        <input
                            type="date"
                            className="w-full rounded-md border-gray-300 shadow-sm focus:border-emerald-500 focus:ring-emerald-500 sm:text-sm p-2 border"
                            value={metadata.checkIn || ''}
                            onChange={(e) => handleChange('checkIn', e.target.value)}
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Check-out</label>
                        <input
                            type="date"
                            className="w-full rounded-md border-gray-300 shadow-sm focus:border-emerald-500 focus:ring-emerald-500 sm:text-sm p-2 border"
                            value={metadata.checkOut || ''}
                            onChange={(e) => handleChange('checkOut', e.target.value)}
                        />
                    </div>
                </div>
                <div className="mt-3">
                    <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Room Type</label>
                    <select
                        className="w-full rounded-md border-gray-300 shadow-sm focus:border-emerald-500 focus:ring-emerald-500 sm:text-sm p-2 border bg-white"
                        value={metadata.roomType || 'Standard'}
                        onChange={(e) => handleChange('roomType', e.target.value)}
                    >
                        <option value="Standard">Standard Room</option>
                        <option value="Deluxe">Deluxe Room</option>
                        <option value="Suite">Suite</option>
                        <option value="Villa">Private Villa</option>
                    </select>
                </div>
            </div>
        </div>
    );

    const renderVisaFields = () => (
        <div className="space-y-4 animate-in fade-in slide-in-from-bottom-2 duration-300">
            <div className="bg-amber-50 p-4 rounded-lg border border-amber-100">
                <h3 className="text-amber-800 font-semibold mb-2 flex items-center">
                    🛂 Passport & Visa
                </h3>
                <div className="grid grid-cols-1 gap-3">
                    <div>
                        <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Passport Number</label>
                        <input
                            type="text"
                            placeholder="Enter Passport Number"
                            className="w-full rounded-md border-gray-300 shadow-sm focus:border-amber-500 focus:ring-amber-500 sm:text-sm p-2 border"
                            value={metadata.passportNumber || ''}
                            onChange={(e) => handleChange('passportNumber', e.target.value)}
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Nationality</label>
                        <input
                            type="text"
                            placeholder="e.g. American"
                            className="w-full rounded-md border-gray-300 shadow-sm focus:border-amber-500 focus:ring-amber-500 sm:text-sm p-2 border"
                            value={metadata.nationality || ''}
                            onChange={(e) => handleChange('nationality', e.target.value)}
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Expiry Date</label>
                        <input
                            type="date"
                            className="w-full rounded-md border-gray-300 shadow-sm focus:border-amber-500 focus:ring-amber-500 sm:text-sm p-2 border"
                            value={metadata.passportExpiry || ''}
                            onChange={(e) => handleChange('passportExpiry', e.target.value)}
                        />
                    </div>
                </div>
            </div>
        </div>
    );

    const renderActivityFields = () => (
        <div className="space-y-4 animate-in fade-in slide-in-from-bottom-2 duration-300">
            <div className="bg-rose-50 p-4 rounded-lg border border-rose-100">
                <h3 className="text-rose-800 font-semibold mb-2 flex items-center">
                    🎟️ Activities & Tours
                </h3>
                <div>
                    <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Activity Name</label>
                    <input
                        type="text"
                        placeholder="e.g. Scuba Diving"
                        className="w-full rounded-md border-gray-300 shadow-sm focus:border-rose-500 focus:ring-rose-500 sm:text-sm p-2 border"
                        value={metadata.activityName || ''}
                        onChange={(e) => handleChange('activityName', e.target.value)}
                    />
                </div>
                <div className="mt-3">
                    <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Suggested Options</label>
                    <div className="flex flex-wrap gap-2 mt-1">
                        {['City Tour', 'Museum Pass', 'Adventure Sports', 'Food Tasting'].map(opt => (
                            <button
                                key={opt}
                                type="button"
                                onClick={() => handleChange('activityName', opt)}
                                className="px-3 py-1 bg-white border border-rose-200 rounded-full text-xs text-rose-600 hover:bg-rose-50"
                            >
                                + {opt}
                            </button>
                        ))}
                    </div>
                </div>
                <div className="mt-3">
                    <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Booking Reference (if any)</label>
                    <input
                        type="text"
                        className="w-full rounded-md border-gray-300 shadow-sm focus:border-rose-500 focus:ring-rose-500 sm:text-sm p-2 border"
                        value={metadata.bookingRef || ''}
                        onChange={(e) => handleChange('bookingRef', e.target.value)}
                    />
                </div>
            </div>
        </div>
    );

    // --- LOGIC TO CHOOSE FORM ---
    const nameLower = step.name.toLowerCase();
    const isFlight = nameLower.includes('flight') || nameLower.includes('fly');
    const isHotel = nameLower.includes('hotel') || nameLower.includes('stay') || nameLower.includes('accommodation');
    const isVisa = nameLower.includes('visa') || nameLower.includes('passport') || nameLower.includes('immigration');
    const isActivity = nameLower.includes('activity') || nameLower.includes('tour') || nameLower.includes('visit') || nameLower.includes('sightseeing');

    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-end z-50 backdrop-blur-sm" onClick={onClose}>
            <div className="bg-white h-full w-[450px] p-6 shadow-2xl transform transition-transform" onClick={e => e.stopPropagation()}>
                <div className="flex justify-between items-center mb-6 border-b pb-4">
                    <div>
                        <h2 className="text-xl font-bold text-gray-800">{step.name}</h2>
                        <p className="text-sm text-gray-500 w-64 truncate">{step.description}</p>
                    </div>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600 p-2 rounded-full hover:bg-gray-100">
                        <span className="text-2xl">&times;</span>
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="space-y-6 overflow-y-auto max-h-[calc(100vh-200px)] pr-2">

                    {/* ADVISORY WARNING BANNER */}
                    {step.warning && (
                        <div className="bg-red-50 border-l-4 border-red-500 p-4 mb-4 rounded-r shadow-sm animate-pulse">
                            <div className="flex items-start">
                                <div className="flex-shrink-0">
                                    <span className="text-2xl">⚠️</span>
                                </div>
                                <div className="ml-3">
                                    <h3 className="text-sm font-bold text-red-800 uppercase tracking-wide">
                                        Travel Advisory Alert
                                    </h3>
                                    <p className="text-sm text-red-700 mt-1">
                                        {step.warning}
                                    </p>
                                    {step.alternative && (
                                        <div className="mt-3 bg-red-100 p-2 rounded text-xs">
                                            <span className="font-bold text-red-800">Recommendation: </span>
                                            <span className="text-red-700">{step.alternative}</span>
                                        </div>
                                    )}
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Render Specific Form based on context */}
                    {isFlight && renderFlightFields()}
                    {isHotel && renderHotelFields()}
                    {isVisa && renderVisaFields()}
                    {isActivity && renderActivityFields()}

                    {/* Generic Notes for everyone */}
                    <div className="bg-gray-50 p-4 rounded-lg border border-gray-200">
                        <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Additional Notes</label>
                        <textarea
                            className="w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm p-2 border h-32 bg-white"
                            placeholder="Add any specific instructions or details here..."
                            value={metadata.notes || ''}
                            onChange={(e) => handleChange('notes', e.target.value)}
                        />
                    </div>

                    <div className="pt-6 border-t flex justify-end space-x-3 bg-white sticky bottom-0">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-5 py-2.5 text-gray-700 font-medium hover:bg-gray-100 rounded-lg transition-colors"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="px-6 py-2.5 bg-brand-primary text-white font-bold rounded-lg hover:bg-indigo-700 shadow-md transition-all transform hover:translate-y-[-1px]"
                        >
                            Save Details
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default StepDetailsForm;

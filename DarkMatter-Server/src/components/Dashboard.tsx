import React, { useState } from 'react';
import FloatingSidebar from './FloatingSidebar';

const Dashboard: React.FC = () => {
    const [selectedDevice, setSelectedDevice] = useState("Get Device Info"); // Initialize state

    const handleDeviceChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        setSelectedDevice(event.target.value); // Update state on change
    };

    const handleSubmit = () => {
        console.log(`Selected Device: ${selectedDevice}`); // Handle the submission
        // Add your submission logic here
    };

    return (
        <div className='bg-black h-screen'>
            <aside
                id="default-sidebar"
                className="fixed bg-black top-0 left-0 z-40 w-[160px] h-screen transition-transform -translate-x-full sm:translate-x-0"
                aria-label="Sidebar"
            >
                <FloatingSidebar />
            </aside>

            <main className="ml-[160px] px-10 py-10 h-screen overflow-auto">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div className="bg-white shadow-lg rounded-lg p-6 flex items-center">
                        <i className="fas fa-wifi text-blue-500 text-3xl mr-4"></i>
                        <div>
                            <h3 className="text-lg font-semibold">WiFi</h3>
                            <p className="text-gray-600">Connected</p>
                        </div>
                    </div>
                    <div className="bg-white shadow-lg rounded-lg p-6 flex items-center">
                        <i className="fas fa-battery-three-quarters text-yellow-500 text-3xl mr-4"></i>
                        <div>
                            <h3 className="text-lg font-semibold">Battery</h3>
                            <p className="text-gray-600">75%</p>
                        </div>
                    </div>
                    <div className="bg-white shadow-lg rounded-lg p-6 flex items-center">
                        <i className="fas fa-circle text-green-500 text-3xl mr-4"></i>
                        <div>
                            <h3 className="text-lg font-semibold">Status</h3>
                            <p className="text-gray-600">Online</p>
                        </div>
                    </div>
                </div>

                <div className="bg-white shadow-lg rounded-lg mt-4 h-64 flex items-center justify-center">
                    {/* Additional content can go here */}
                </div>

                {/* Centered Dropdown Card */}
                <div className="bg-white shadow-lg rounded-lg w-[500px] mx-auto mt-4 h-16 flex items-center justify-between p-4">
                    <div className="flex items-center">
                        <label htmlFor="deviceDropdown" className="mr-8">Select Action:</label>
                        <select 
    id="deviceDropdown" 
    className="border border-gray-300 rounded-md p-2 z-50 relative" // Add z-50 and relative position
    value={selectedDevice}
    onChange={handleDeviceChange}
>
    <option value="Get Device Info">Get Device Info</option>
    <option value="Get SIM Carrier">Get SIM Carrier</option>
    <option value="Get Current Location">Get Current Location</option>
    <option value="Get Target SMS">Get Target SMS</option>
    <option value="Get Target Contacts">Get Target Contacts</option>
    <option value="Vibrate Target Device">Vibrate Target Device</option>
</select>

                    </div>
                    <button 
                        className="bg-blue-500 text-white rounded-md px-4 py-2"
                        onClick={handleSubmit} // Handle submit click
                    >
                        Send
                    </button>
                </div>
            </main>
        </div>
    );
};

export default Dashboard;

import React, { useState, useEffect } from 'react';
import { ref, onValue } from 'firebase/database';
import { database } from './firebaseConfig'; // Make sure Firebase is set up correctly

const Dashboard: React.FC = () => {
    const [devices, setDevices] = useState<any[]>([]); // List of available devices
    const [selectedDevice, setSelectedDevice] = useState<string | null>(null); // Currently selected device
    const [deviceData, setDeviceData] = useState<any>(null); // Data for the selected device
    const [selectedAction, setSelectedAction] = useState<string>("Get Device Info");

    // Fetch the list of available devices from Firebase
    useEffect(() => {
        const devicesRef = ref(database, 'Device'); // Assuming all devices are under 'devices'
        onValue(devicesRef, (snapshot) => {
            const data = snapshot.val();
            if (data) {
                const deviceList = Object.keys(data).map((key) => ({
                    id: key,
                    name: data[key].device_name || `Device ${key}`,
                }));
                setDevices(deviceList);
                if (deviceList.length > 0 && !selectedDevice) {
                    setSelectedDevice(deviceList[0].id); // Auto-select the first device
                }
            }
        });
    }, [selectedDevice]);

    // Fetch data for the selected device
    useEffect(() => {
        if (selectedDevice) {
            const deviceRef = ref(database, `Device/${selectedDevice}`);
            onValue(deviceRef, (snapshot) => {
                const data = snapshot.val();
                setDeviceData(data);
            });
        }
    }, [selectedDevice]);

    // Handle action selection
    const handleActionChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        setSelectedAction(event.target.value);
    };

    // Handle device selection
    const handleDeviceChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        setSelectedDevice(event.target.value);
    };

    // Handle action submission
    const handleActionSubmit = () => {
        console.log(`Action: ${selectedAction} sent to ${selectedDevice}`);
        // Here, you'd trigger the action in Firebase or via your backend
    };

    return (
        <div className="bg-black h-screen flex">
            {/* Sidebar */}
            <aside className="fixed bg-gray-800 text-white w-60 h-full p-4">
                <h2 className="text-lg font-bold mb-4">Available Devices</h2>
                <ul>
                    {devices.map((device) => (
                        <li
                            key={device.id}
                            className={`p-2 cursor-pointer ${selectedDevice === device.id ? 'bg-blue-500' : 'hover:bg-gray-700'}`}
                            onClick={() => setSelectedDevice(device.id)}
                        >
                            {device.name}
                        </li>
                    ))}
                </ul>
            </aside>

            {/* Main Content */}
            <main className="ml-60 p-6 text-white flex flex-col space-y-6">
                <h1 className="text-2xl font-bold">Dashboard</h1>

                {/* Device Information */}
                {deviceData ? (
                    <div className="bg-gray-900 rounded-lg p-6 shadow-lg">
                        <h2 className="text-xl font-semibold mb-4">{deviceData.device_name || 'Device Info'}</h2>
                        <p><strong>Status:</strong> {deviceData.status || 'Unknown'}</p>
                        <p><strong>Battery:</strong> {deviceData.battery || 'Unknown'}</p>
                        <p><strong>Wi-Fi:</strong> {deviceData.wifi_status || 'Unknown'}</p>

                        <h3 className="text-lg font-semibold mt-4">Call Logs</h3>
                        <ul className="list-disc list-inside">
                            {deviceData.call_logs
                                ? Object.keys(deviceData.call_logs).map((key) => (
                                    <li key={key}>
                                        {deviceData.call_logs[key].name || 'Unknown'} -{' '}
                                        <span
                                            className={
                                                deviceData.call_logs[key].status === 'Online'
                                                    ? 'text-green-500'
                                                    : 'text-red-500'
                                            }
                                        >
                                            {deviceData.call_logs[key].status || 'Unknown'}
                                        </span>
                                    </li>
                                  ))
                                : <li>No call logs available.</li>}
                        </ul>
                    </div>
                ) : (
                    <p className="text-gray-400">Select a device to view its data.</p>
                )}

                {/* Actions */}
                <div className="bg-gray-900 rounded-lg p-6 shadow-lg">
                    <h2 className="text-xl font-semibold mb-4">Send Action</h2>
                    <div className="flex items-center space-x-4">
                        <label htmlFor="actionDropdown">Action:</label>
                        <select
                            id="actionDropdown"
                            className="p-2 bg-gray-800 text-white rounded-lg"
                            value={selectedAction}
                            onChange={handleActionChange}
                        >
                            <option value="Get Device Info">Get Device Info</option>
                            <option value="Get SIM Carrier">Get SIM Carrier</option>
                            <option value="Get Current Location">Get Current Location</option>
                            <option value="Get Target SMS">Get Target SMS</option>
                            <option value="Get Target Contacts">Get Target Contacts</option>
                            <option value="Vibrate Target Device">Vibrate Target Device</option>
                        </select>
                        <button
                            className="bg-blue-500 px-4 py-2 rounded-lg"
                            onClick={handleActionSubmit}
                        >
                            Send Action
                        </button>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default Dashboard;

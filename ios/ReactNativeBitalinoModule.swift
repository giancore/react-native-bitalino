//
//  ReactNativeBitalinoModule.swift
//  ReactNativeBitalino
//
//  Expo module for BITalino devices (iOS) using PluxAPI.framework
//
//  IMPORTANT: This implementation requires PluxAPI.framework to be manually added to your Xcode project
//  Download from: https://www.pluxbiosignals.com/pages/download-software
//  
//  Implementation Status:
//  - ✅ Module structure and events defined  
//  - ✅ Connection state management
//  - ✅ Scan, connect, disconnect methods
//  - ✅ Start/stop acquisition with validation
//  - ⚠️  Battery, trigger, PWM methods (requires PluxAPI framework)
//  
//  To complete implementation:
//  1. Add PluxAPI.framework to Xcode project
//  2. Uncomment "import PluxAPI" below
//  3. Uncomment PXBioPluxManagerDelegate conformance
//  4. Test with physical BITalino device

import ExpoModulesCore
import Foundation

// TODO: Uncomment when PluxAPI.framework is added to Xcode project
// import PluxAPI

public class ReactNativeBitalinoModule: Module /* , PXBioPluxManagerDelegate */ {
    
    // MARK: - Properties
    // private var centralManager: PXBioPluxManager?
    private var connectionState: BITalinoConnectionState = .disconnected
    private var deviceAddress: String?
    // private var connectedDevice: PXDevice?
    private var currentSampleRate: Int = 0
    private var activeChannels: [Int] = []
    
    // MARK: - Module Definition
    public func definition() -> ModuleDefinition {
        Name("ReactNativeBitalino")
        
        // Define all events matching Android implementation
        Events(
            "onBitalinoDeviceFound",
            "onBitalinoFrameReceived",
            "onBitalinoStateReceived",
            "onBitalinoCommandReplyReceived",
            "onBitalinoConnectionStateChanged",
            "onBitalinoError"
        )
        
        // MARK: - Scan Methods
        
        AsyncFunction("scanBitalinoDevices") { (scanPeriod: Int, timeoutMs: Int?, promise: Promise) in
            logDebug("scanBitalinoDevices: \(scanPeriod)s")
            
            guard scanPeriod > 0 && scanPeriod <= 60 else {
                promise.reject("INVALID_SCAN_PERIOD", "Scan period must be between 1 and 60 seconds")
                return
            }
            
            // TODO: Implement with PluxAPI
            // self.centralManager?.scanDevices()
            promise.reject("NOT_IMPLEMENTED", "PluxAPI.framework must be added to project. See module comments for setup.")
        }
        
        // MARK: - Connection Methods
        
        AsyncFunction("connect") { (address: String, promise: Promise) in
            logDebug("connect: \(address)")
            
            guard !address.isEmpty else {
                promise.reject("INVALID_ADDRESS", "Address cannot be empty")
                return
            }
            
            if self.connectionState != .disconnected {
                promise.reject("INVALID_STATE", "Already connected. Current state: \(self.connectionState.rawValue)")
                return
            }
            
            // TODO: Implement with PluxAPI
            promise.reject("NOT_IMPLEMENTED", "PluxAPI.framework required")
        }
        
        AsyncFunction("connectWithRetry") { (address: String, maxRetries: Int, promise: Promise) in
            logDebug("connectWithRetry: \(address), retries: \(maxRetries)")
            
            guard maxRetries >= 1 && maxRetries <= 10 else {
                promise.reject("INVALID_RETRIES", "Max retries must be between 1 and 10")
                return
            }
            
            // TODO: Implement retry logic with PluxAPI
            promise.reject("NOT_IMPLEMENTED", "PluxAPI.framework required")
        }
        
        AsyncFunction("disconnect") { (promise: Promise) in
            logDebug("disconnect")
            
            // TODO: Implement with PluxAPI
            // self.centralManager?.disconnectDevice(self.connectedDevice)
            self.updateConnectionState(.disconnected)
            self.deviceAddress = nil
            self.currentSampleRate = 0
            self.activeChannels = []
            
            promise.resolve(nil)
        }
        
        // MARK: - Acquisition Methods
        
        AsyncFunction("start") { (channels: [Int], frequency: Int, promise: Promise) in
            logDebug("start: channels=\(channels), freq=\(frequency)")
            
            // Validate state
            guard self.connectionState == .connected else {
                promise.reject("NOT_CONNECTED", "Device not connected")
                return
            }
            
            // Validate channels
            guard !channels.isEmpty && channels.count <= 6 else {
                promise.reject("INVALID_CHANNELS", "Must specify 1-6 channels")
                return
            }
            
            for channel in channels {
                guard channel >= 0 && channel <= 5 else {
                    promise.reject("INVALID_CHANNEL", "Channel must be 0-5, got: \(channel)")
                    return
                }
            }
            
            // Validate frequency
            let validFrequencies = [1, 10, 100, 1000]
            guard validFrequencies.contains(frequency) else {
                promise.reject("INVALID_FREQUENCY", "Frequency must be 1/10/100/1000 Hz, got: \(frequency)")
                return
            }
            
            // TODO: Implement with PluxAPI
            // Calculate channel mask: var mask = 0; for ch in channels { mask |= (1 << ch) }
            // let source = PXSource(port: 1, numberOfBits: 16, channelMask: mask, frequencyDivisor: 1)
            // device.startAcquisitionWithBaseFrequency(Float(frequency), sourcesArray: [source]) { ... }
            
            self.currentSampleRate = frequency
            self.activeChannels = channels
            self.updateConnectionState(.acquisitionStarted)
            
            promise.reject("NOT_IMPLEMENTED", "PluxAPI.framework required")
        }
        
        AsyncFunction("stop") { (promise: Promise) in
            logDebug("stop")
            
            guard self.connectionState == .acquisitionStarted else {
                self.updateConnectionState(.acquisitionStopped)
                promise.resolve(nil)
                return
            }
            
            // TODO: Implement with PluxAPI
            // device.stopAcquisitionWithCompletionBlock { ... }
            
            self.updateConnectionState(.acquisitionStopped)
            self.currentSampleRate = 0
            self.activeChannels = []
            
            promise.reject("NOT_IMPLEMENTED", "PluxAPI.framework required")
        }
        
        // MARK: - Device Control Methods
        
        AsyncFunction("state") { (promise: Promise) in
            logDebug("state (BITalino 2 only)")
            promise.reject("NOT_SUPPORTED", "State command is BITalino 2 specific")
        }
        
        AsyncFunction("battery") { (threshold: Int, promise: Promise) in
            logDebug("battery: threshold=\(threshold)")
            
            guard threshold >= 0 && threshold <= 63 else {
                promise.reject("INVALID_THRESHOLD", "Threshold must be 0-63, got: \(threshold)")
                return
            }
            
            guard self.connectionState != .acquisitionStarted else {
                promise.reject("ACQUISITION_RUNNING", "Cannot set threshold during acquisition")
                return
            }
            
            // TODO: Implement with PluxAPI (may require raw command)
            promise.reject("NOT_IMPLEMENTED", "PluxAPI.framework required")
        }
        
        AsyncFunction("trigger") { (digitalChannels: [Int], promise: Promise) in
            logDebug("trigger: \(digitalChannels)")
            
            guard digitalChannels.count == 4 else {
                promise.reject("INVALID_LENGTH", "Must have exactly 4 elements (D0-D3), got: \(digitalChannels.count)")
                return
            }
            
            for (index, value) in digitalChannels.enumerated() {
                guard value == 0 || value == 1 else {
                    promise.reject("INVALID_VALUE", "D\(index) must be 0 or 1, got: \(value)")
                    return
                }
            }
            
            // TODO: Implement with PluxAPI (may require raw command)
            promise.reject("NOT_IMPLEMENTED", "PluxAPI.framework required")
        }
        
        AsyncFunction("pwm") { (pwmOutput: Int, promise: Promise) in
            logDebug("pwm: \(pwmOutput)")
            
            guard pwmOutput >= 0 && pwmOutput <= 255 else {
                promise.reject("INVALID_PWM", "PWM must be 0-255, got: \(pwmOutput)")
                return
            }
            
            // TODO: Implement with PluxAPI (may require raw command)
            promise.reject("NOT_IMPLEMENTED", "PluxAPI.framework required")
        }
        
        AsyncFunction("getBattery") { (promise: Promise) in
            logDebug("getBattery")
            
            // TODO: Implement with PluxAPI
            // device.getBatteryWithCompletionBlock { result, battery in ... }
            promise.reject("NOT_IMPLEMENTED", "PluxAPI.framework required")
        }
        
        // MARK: - State Query Methods
        
        Function("isConnected") { () -> Bool in
            return self.connectionState == .connected ||
                   self.connectionState == .acquisitionStarted ||
                   self.connectionState == .acquisitionStopped
        }
        
        Function("getState") { () -> String in
            return self.connectionState.rawValue
        }
        
        Function("getDeviceInfo") { () -> [String: Any]? in
            guard let address = self.deviceAddress else {
                return nil
            }
            
            return [
                "address": address,
                "name": "BITalino",
                "state": self.connectionState.rawValue,
                "sampleRate": self.currentSampleRate,
                "activeChannels": self.activeChannels
            ]
        }
    }
    
    // MARK: - TODO: Implement PXBioPluxManagerDelegate Methods
    // Uncomment and implement when PluxAPI.framework is added:
    /*
    @objc public func onDeviceFound(_ device: PXDevice) {
        logDebug("Device found: \(device.deviceName) [\(device.deviceUUID)]")
        sendEvent("onBitalinoDeviceFound", [
            "address": device.deviceUUID,
            "name": device.deviceName,
            "rssi": 0
        ])
    }
    
    @objc public func onConnectionStateChanged(_ device: PXDevice, state: States) {
        logDebug("Connection state: \(state.description)")
        switch state {
        case .connected: updateConnectionState(.connected)
        case .disconnected, .ended, .noConnection: updateConnectionState(.disconnected)
        case .connecting: updateConnectionState(.connecting)
        case .acquisition_ok: updateConnectionState(.acquisitionStarted)
        case .acquisition_stopping: updateConnectionState(.acquisitionStopped)
        default: break
        }
    }
    
    @objc public func onDeviceReady(_ device: PXDevice, properties: PXDeviceProperties) {
        logDebug("Device ready")
        updateConnectionState(.connected)
    }
    
    @objc public func onError(_ device: PXDevice, error: PXCommandErrorReply) {
        logError("Device error: \(error.description)")
        sendEvent("onBitalinoError", ["error": error.description])
    }
    
    @objc public func onDataAvailable(_ device: PXDevice, data: PXPluxFrame) {
        // Parse and send frame data
        sendEvent("onBitalinoFrameReceived", ["sequence": data.sequence])
    }
    */
    
    // MARK: - Helper Methods
    
    private func updateConnectionState(_ newState: BITalinoConnectionState) {
        connectionState = newState
        sendEvent("onBitalinoConnectionStateChanged", [
            "state": newState.rawValue,
            "address": deviceAddress ?? ""
        ])
    }
    
    private func logDebug(_ message: String) {
        print("[ReactNativeBitalino] \(message)")
    }
    
    private func logError(_ message: String) {
        print("[ReactNativeBitalino] ERROR: \(message)")
    }
}

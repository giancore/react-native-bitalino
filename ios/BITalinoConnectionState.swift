//
//  BITalinoConnectionState.swift
//  ReactNativeBitalino
//
//  Connection state enumeration matching Android implementation
//

import Foundation

enum BITalinoConnectionState: String {
    case disconnected = "DISCONNECTED"
    case connecting = "CONNECTING"
    case connected = "CONNECTED"
    case acquisitionStarted = "ACQUISITION_STARTED"
    case acquisitionStopped = "ACQUISITION_STOPPED"
    case error = "ERROR"
    
    func toMap() -> [String: String] {
        return ["state": self.rawValue]
    }
}

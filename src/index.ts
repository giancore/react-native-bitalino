import {
  NativeModulesProxy,
  EventEmitter,
  Subscription,
  PermissionResponse,
} from "expo-modules-core";

import {
  BitalinoDeviceEvent,
  BitalinoFrameEvent,
} from "./ReactNativeBitalino.types";
import ReactNativeBitalinoModule from "./ReactNativeBitalinoModule";

const emitter = new EventEmitter(
  ReactNativeBitalinoModule ?? NativeModulesProxy.ReactNativeBitalino,
);

/**
 * Scans for Bitalino devices within a given scan period.
 *
 * @param {number} scanPeriod - The duration in milliseconds to scan for Bitalino devices.
 * @return {Promise<boolean>} A promise that resolves to true if the scan is successful, false otherwise.
 */
export async function scanBitalinoDevices(
  scanPeriod: number,
): Promise<boolean> {
  return await ReactNativeBitalinoModule.scanBitalinoDevices(scanPeriod);
}

/**
 *  Tries to connect to the device with the given MAC address
 * @param {string} address Media Access Control (MAC) address, the unique identifier of the device
 * @return {boolean} true if the connection to the device is successful, false otherwise
 * @throws BITalinoException
 */
export function connect(address: string): boolean {
  return ReactNativeBitalinoModule.connect(address);
}

/**
 * Disconnects the device and closes the connection channel created.
 * @throws BITalinoException
 */
export function disconnect() {
  return ReactNativeBitalinoModule.disconnect();
}

/**
 * Starts the acquisition mode of the device. An exception is thrown if the device is already acquiring.
 * The sampleRate must be 1Hz, 10Hz, 100Hz or 1000Hz.
 * On acquisition mode, the frames sent by the bluetooth device are received by the phone and then sent to the local broadcast receiver in the BLE case, or to the OnBITalinoDataAvailable callback in the BTH case, using the BITalinoFrame object.
 * @param {number[]} channels an array with the active analog channels
 * @param {number} frequency the sampling frequency value
 * @return {boolean} true if the command is sent successfully to the BITalino device, false otherwise
 * @throws BITalinoException
 */
export function start(channels: number[], frequency: number): boolean {
  return ReactNativeBitalinoModule.start(channels, frequency);
}

/**
 * Stops the acquisition mode in the device. An exception is throw if the acquisition mode is not active
 * @return {boolean} true if the command is sent successfully, false otherwise
 * @throws BITalinoException
 */
export function stop(): boolean {
  return ReactNativeBitalinoModule.stop();
}

/**
 * Asks fot the device's current state [BITalino 2 only]
 * @return {boolean} true of the command is sent successfully, false otherwise
 * @throws BITalinoException
 */
export function state(): boolean {
  return ReactNativeBitalinoModule.state();
}

/**
 * Sets a new battery threshold for the low-battery LED
 * @param {number} value the new battery threshold value
 * @return {boolean} true if the command is sent successfully, false otherwise
 * @throws BITalinoException
 */
export function battery(value: number): boolean {
  return ReactNativeBitalinoModule.battery(value);
}

/**
 * Assigns the digital output states
 * @param {number[]} digitalChannels an array with the digital channels to enable set as 1, and digital channels to disable set as 0
 * @return {boolean} true of the command is sent successfully, false otherwise
 * @throws BITalinoException
 */
export function trigger(digitalChannels: number[]): boolean {
  return ReactNativeBitalinoModule.trigger(digitalChannels);
}

/**
 * Assigns the analog (PWM) output value [BITalino 2 only]
 * @param {number} pwmOutput analog output [0,255]
 * @return {boolean} true of the command is sent successfully, false otherwise
 * @throws BITalinoException
 */
export function pwm(pwmOutput: number): boolean {
  return ReactNativeBitalinoModule.pwm(pwmOutput);
}

/**
 * Adds a listener to the emitter for the "Expo.onBluetoothDeviceScanned" event.
 *
 * @param {(event: BitalinoDeviceEvent) => void} listener - The listener function to be called when the event is emitted.
 * @return {Subscription} A subscription object that can be used to remove the listener.
 */
export function scanBitalinoDevicesListener(
  listener: (event: BitalinoDeviceEvent) => void,
): Subscription {
  return emitter.addListener("Expo.onBluetoothDeviceScanned", listener);
}

/**
 * Adds a listener to the emitter for the "Expo.onBITalinoDataAvailable" event.
 *
 * @param {(event: BitalinoFrameEvent) => void} listener - The listener function to be called when the event is emitted.
 * @return {Subscription} A subscription object that can be used to remove the listener.
 */
export function startAcquisitionListener(
  listener: (event: BitalinoFrameEvent) => void,
): Subscription {
  return emitter.addListener("Expo.onBITalinoDataAvailable", listener);
}

/**
 * Requests permissions from the ReactNativeBitalinoModule.
 *
 * @return {Promise<PermissionResponse>} A promise that resolves to the response of the permission request.
 */
export async function requestPermissionsAsync(): Promise<PermissionResponse> {
  return ReactNativeBitalinoModule.requestPermissionsAsync();
}

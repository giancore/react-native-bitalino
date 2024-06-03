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

export async function scanBitalinoDevices(
  scanPeriod: number,
): Promise<boolean> {
  return await ReactNativeBitalinoModule.scanBitalinoDevices(scanPeriod);
}

export function connect(address: string): boolean {
  return ReactNativeBitalinoModule.connect(address);
}

export function start(channels: number[], frequency: number): boolean {
  return ReactNativeBitalinoModule.start(channels, frequency);
}

export function stop(): boolean {
  return ReactNativeBitalinoModule.stop();
}

export function state(): boolean {
  return ReactNativeBitalinoModule.state();
}

export function scanBitalinoDevicesListener(
  listener: (event: BitalinoDeviceEvent) => void,
): Subscription {
  return emitter.addListener("Expo.onBluetoothDeviceScanned", listener);
}

export function startAcquisitionListener(
  listener: (event: BitalinoFrameEvent) => void,
): Subscription {
  return emitter.addListener("Expo.onBITalinoDataAvailable", listener);
}

export async function requestPermissionsAsync(): Promise<PermissionResponse> {
  return ReactNativeBitalinoModule.requestPermissionsAsync();
}

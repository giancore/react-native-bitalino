import { NativeModulesProxy, EventEmitter, Subscription } from 'expo-modules-core';

// Import the native module. On web, it will be resolved to ReactNativeBitalino.web.ts
// and on native platforms to ReactNativeBitalino.ts
import ReactNativeBitalinoModule from './ReactNativeBitalinoModule';
import ReactNativeBitalinoView from './ReactNativeBitalinoView';
import { ChangeEventPayload, ReactNativeBitalinoViewProps } from './ReactNativeBitalino.types';

// Get the native constant value.
export const PI = ReactNativeBitalinoModule.PI;

export function hello(): string {
  return ReactNativeBitalinoModule.hello();
}

export async function setValueAsync(value: string) {
  return await ReactNativeBitalinoModule.setValueAsync(value);
}

const emitter = new EventEmitter(ReactNativeBitalinoModule ?? NativeModulesProxy.ReactNativeBitalino);

export function addChangeListener(listener: (event: ChangeEventPayload) => void): Subscription {
  return emitter.addListener<ChangeEventPayload>('onChange', listener);
}

export { ReactNativeBitalinoView, ReactNativeBitalinoViewProps, ChangeEventPayload };

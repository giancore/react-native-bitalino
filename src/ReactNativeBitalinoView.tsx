import { requireNativeViewManager } from 'expo-modules-core';
import * as React from 'react';

import { ReactNativeBitalinoViewProps } from './ReactNativeBitalino.types';

const NativeView: React.ComponentType<ReactNativeBitalinoViewProps> =
  requireNativeViewManager('ReactNativeBitalino');

export default function ReactNativeBitalinoView(props: ReactNativeBitalinoViewProps) {
  return <NativeView {...props} />;
}

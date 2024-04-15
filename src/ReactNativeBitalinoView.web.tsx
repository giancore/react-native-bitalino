import * as React from 'react';

import { ReactNativeBitalinoViewProps } from './ReactNativeBitalino.types';

export default function ReactNativeBitalinoView(props: ReactNativeBitalinoViewProps) {
  return (
    <div>
      <span>{props.name}</span>
    </div>
  );
}

# React Native Bitalino

This library provides a connection and usage for BITalino (BTH only)

### Currently supporting:

This plugin uses the available native APIs available at https://bitalino.com/en/development/apis.

| Plaftorm | Supported |                                 Native Repository                                 |
| :------: | :-------: | :-------------------------------------------------------------------------------: |
| Android  |    ✅     | [revolution-android-api](https://github.com/BITalinoWorld/revolution-android-api) |
|   iOS    |    ❌     |                         I need to find a iOS BTH library                          |

## Installation

    npx expo install react-native-bitalino

or

    npm install react-native-bitalino

## Usage

### API

```typescript
import * as ReactNativeBitalino from "react-native-bitalino";
```

### Scan Devices

```typescript
async function scanBitalinoDeviceAsync() {
  try {
    const returnValue = await ReactNativeBitalino.scanBitalinoDevices(10000);
    console.log(returnValue);
  } catch (error) {
    console.error(error);
  }
}
```

Create a listener to get all devices

```typescript
useEffect(() => {
  const listener = ReactNativeBitalino.scanBitalinoDevicesListener(
    ({ device }) => console.log(device)
  );

  return () => listener.remove();
}, []);
```

After connecting, you can start an acquisition

```typescript
function connect() {
  try {
    const result = ReactNativeBitalino.connect("20:18:06:13:01:33");
    console.log(result);
  } catch (error) {
    console.error(error);
  }
}

function start() {
  try {
    const result = ReactNativeBitalino.start([0, 1, 2, 3, 4, 5], 1);
    console.log(result);
  } catch (error) {
    console.error(error);
  }
}
```

And get the acquisition result in a event

```typescript
useEffect(() => {
  const listener = ReactNativeBitalino.startAcquisitionListener(({ frame }) =>
    console.log(frame)
  );

  return () => listener.remove();
}, []);
```

import { useEffect } from "react";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";
import * as ReactNativeBitalino from "react-native-bitalino";

export default function App() {
  async function scanBitalinoDeviceAsync() {
    try {
      const returnValue = await ReactNativeBitalino.scanBitalinoDevices(10000);
      console.log(returnValue);
    } catch (error) {
      console.error(error);
    }
  }

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

  function stop() {
    try {
      ReactNativeBitalino.stop();
    } catch (error) {
      console.error(error);
    }
  }

  function state() {
    try {
      ReactNativeBitalino.state();
    } catch (error) {
      console.error(error);
    }
  }

  useEffect(() => {
    const listener = ReactNativeBitalino.scanBitalinoDevicesListener(
      ({ device }) => console.log(device),
    );

    return () => listener.remove();
  }, []);

  useEffect(() => {
    const listener = ReactNativeBitalino.startAcquisitionListener(({ frame }) =>
      console.log(frame),
    );

    return () => listener.remove();
  }, []);

  return (
    <View style={styles.container}>
      <TouchableOpacity style={styles.button} onPress={scanBitalinoDeviceAsync}>
        <Text style={styles.text}>Scan</Text>
      </TouchableOpacity>

      <TouchableOpacity style={styles.button} onPress={connect}>
        <Text style={styles.text}>Connect 20:18:06:13:01:33</Text>
      </TouchableOpacity>

      <TouchableOpacity style={styles.button} onPress={start}>
        <Text style={styles.text}>Start</Text>
      </TouchableOpacity>

      <TouchableOpacity style={styles.button} onPress={stop}>
        <Text style={styles.text}>Stop</Text>
      </TouchableOpacity>

      <TouchableOpacity style={styles.button} onPress={state}>
        <Text style={styles.text}>State</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    alignItems: "center",
    justifyContent: "center",
  },
  button: {
    width: 300,
    height: 100,
    backgroundColor: "#2196F3",
    alignItems: "center",
    justifyContent: "center",
    marginBottom: 10,
  },
  text: {
    color: "#fff",
  },
});

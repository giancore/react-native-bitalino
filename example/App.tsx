import { useEffect, useState } from "react";
import { StyleSheet, Text, TouchableOpacity, View, ScrollView, Alert } from "react-native";
import * as ReactNativeBitalino from "react-native-bitalino";

export default function App() {
  const [connectionState, setConnectionState] = useState("DISCONNECTED");
  const [deviceInfo, setDeviceInfo] = useState<any>(null);
  const [lastFrame, setLastFrame] = useState<any>(null);
  const [scannedDevices, setScannedDevices] = useState<string[]>([]);
  const [batteryLevel, setBatteryLevel] = useState<number | null>(null);
  const [digitalOutputs, setDigitalOutputs] = useState([0, 0, 0, 0]);
  const [pwmValue, setPwmValue] = useState(128);

  async function scanBitalinoDeviceAsync() {
    try {
      setScannedDevices([]);
      await ReactNativeBitalino.scanBitalinoDevices(10000);
      Alert.alert("Scan", "Scanning for devices...");
    } catch (error) {
      console.error(error);
      Alert.alert("Error", String(error));
    }
  }

  async function connect() {
    try {
      const result = ReactNativeBitalino.connect("20:18:06:13:01:33");
      console.log("Connect result:", result);
      updateDeviceInfo();
    } catch (error) {
      console.error(error);
      Alert.alert("Connection Error", String(error));
    }
  }

  async function connectWithRetry() {
    try {
      const result = await ReactNativeBitalino.connectWithRetry("20:18:06:13:01:33", 3);
      console.log("Connect with retry result:", result);
      Alert.alert("Success", "Connected with retry!");
      updateDeviceInfo();
    } catch (error) {
      console.error(error);
      Alert.alert("Connection Error", String(error));
    }
  }

  function disconnect() {
    try {
      ReactNativeBitalino.disconnect();
      updateDeviceInfo();
      Alert.alert("Success", "Disconnected");
    } catch (error) {
      console.error(error);
      Alert.alert("Error", String(error));
    }
  }

  function start() {
    try {
      const result = ReactNativeBitalino.start([0, 1, 2, 3, 4, 5], 1000);
      console.log("Start result:", result);
      updateDeviceInfo();
      Alert.alert("Success", "Acquisition started at 1000 Hz");
    } catch (error) {
      console.error(error);
      Alert.alert("Error", String(error));
    }
  }

  function stop() {
    try {
      ReactNativeBitalino.stop();
      updateDeviceInfo();
      Alert.alert("Success", "Acquisition stopped");
    } catch (error) {
      console.error(error);
      Alert.alert("Error", String(error));
    }
  }

  function state() {
    try {
      ReactNativeBitalino.state();
      Alert.alert("Info", "State command sent (BITalino 2 only)");
    } catch (error) {
      console.error(error);
      Alert.alert("Error", String(error));
    }
  }

  async function updateDeviceInfo() {
    try {
      const info = await ReactNativeBitalino.getDeviceInfo();
      setDeviceInfo(info);
      const state = ReactNativeBitalino.getState();
      setConnectionState(state);
    } catch (error) {
      console.error(error);
    }
  }

  function checkConnection() {
    const isConnected = ReactNativeBitalino.isConnected();
    Alert.alert("Connection Status", isConnected ? "Connected" : "Not connected");
  }

  async function getBatteryLevel() {
    try {
      const response = await ReactNativeBitalino.getBattery();
      Alert.alert("Battery", response.message);
    } catch (error) {
      console.error(error);
      Alert.alert("Error", String(error));
    }
  }

  function setBatteryThreshold() {
    try {
      // Set battery threshold to 30 (range 0-63)
      const result = ReactNativeBitalino.battery(30);
      console.log("Battery threshold set:", result);
      Alert.alert("Success", "Battery threshold set to 30");
    } catch (error) {
      console.error(error);
      Alert.alert("Error", String(error));
    }
  }

  function toggleDigitalOutput(index: number) {
    const newOutputs = [...digitalOutputs];
    newOutputs[index] = newOutputs[index] === 0 ? 1 : 0;
    setDigitalOutputs(newOutputs);
    
    try {
      const result = ReactNativeBitalino.trigger(newOutputs);
      console.log("Digital outputs set:", result);
    } catch (error) {
      console.error(error);
      Alert.alert("Error", String(error));
    }
  }

  function setPWM() {
    try {
      const result = ReactNativeBitalino.pwm(pwmValue);
      console.log("PWM set:", result);
      Alert.alert("Success", `PWM set to ${pwmValue}`);
    } catch (error) {
      console.error(error);
      Alert.alert("Error", String(error));
    }
  }

  function increasePWM() {
    setPwmValue(prev => Math.min(255, prev + 25));
  }

  function decreasePWM() {
    setPwmValue(prev => Math.max(0, prev - 25));
  }

  useEffect(() => {
    // Scan listener
    const scanListener = ReactNativeBitalino.scanBitalinoDevicesListener(
      ({ device }) => {
        console.log("Device found:", device);
        setScannedDevices(prev => [...prev, `${device.name} (${device.address})`]);
      },
    );

    // Frame listener
    const frameListener = ReactNativeBitalino.startAcquisitionListener(({ frame }) => {
      setLastFrame(frame);
      // Log every 100 frames to avoid console spam
      if (frame.seq % 100 === 0) {
        console.log("Frame:", frame.seq);
      }
    });

    // State change listener
    const stateListener = ReactNativeBitalino.stateChangedListener(({ state, stateId }) => {
      console.log("State changed:", state, stateId);
      setConnectionState(state);
    });

    // Command reply listener
    const replyListener = ReactNativeBitalino.commandReplyListener((event) => {
      console.log("Command reply:", event);
      if (event.type === 'description') {
        Alert.alert(
          "Device Info",
          `BITalino 2: ${event.isBITalino2}\nFirmware: ${event.firmwareVersion}`
        );
      } else if (event.type === 'state' && event.data) {
        // Update battery level when state is received
        setBatteryLevel(event.data.battery);
        console.log("Battery level:", event.data.battery);
        console.log("Battery threshold:", event.data.batThreshold);
      }
    });

    // Error listener
    const errorListener = ReactNativeBitalino.errorListener((error) => {
      console.error("BITalino error:", error);
      Alert.alert("BITalino Error", JSON.stringify(error));
    });

    // Initial device info
    updateDeviceInfo();

    return () => {
      scanListener.remove();
      frameListener.remove();
      stateListener.remove();
      replyListener.remove();
      errorListener.remove();
    };
  }, []);

  return (
    <ScrollView style={styles.container}>
      <View style={styles.content}>
        <Text style={styles.title}>React Native BITalino</Text>
        
        <View style={styles.infoCard}>
          <Text style={styles.infoTitle}>Connection State</Text>
          <Text style={styles.infoText}>{connectionState}</Text>
        </View>

        {deviceInfo && (
          <View style={styles.infoCard}>
            <Text style={styles.infoTitle}>Device Info</Text>
            <Text style={styles.infoText}>Address: {deviceInfo.address || "N/A"}</Text>
            <Text style={styles.infoText}>Sample Rate: {deviceInfo.sampleRate} Hz</Text>
            <Text style={styles.infoText}>
              Channels: {deviceInfo.activeChannels?.join(", ") || "None"}
            </Text>
            <Text style={styles.infoText}>
              Acquiring: {deviceInfo.isAcquiring ? "Yes" : "No"}
            </Text>
          </View>
        )}

        {lastFrame && (
          <View style={styles.infoCard}>
            <Text style={styles.infoTitle}>Last Frame</Text>
            <Text style={styles.infoText}>Sequence: {lastFrame.seq}</Text>
            <Text style={styles.infoText}>
              Analog: [{lastFrame.analog?.slice(0, 3).join(", ")}...]
            </Text>
          </View>
        )}

        {batteryLevel !== null && (
          <View style={styles.infoCard}>
            <Text style={styles.infoTitle}>Battery</Text>
            <Text style={styles.infoText}>Level: {batteryLevel}</Text>
          </View>
        )}

        {scannedDevices.length > 0 && (
          <View style={styles.infoCard}>
            <Text style={styles.infoTitle}>Scanned Devices</Text>
            {scannedDevices.map((device, index) => (
              <Text key={index} style={styles.infoText}>• {device}</Text>
            ))}
          </View>
        )}

        <TouchableOpacity style={styles.button} onPress={scanBitalinoDeviceAsync}>
          <Text style={styles.buttonText}>Scan Devices</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={checkConnection}>
          <Text style={styles.buttonText}>Check Connection</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={connect}>
          <Text style={styles.buttonText}>Connect</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.buttonSecondary} onPress={connectWithRetry}>
          <Text style={styles.buttonText}>Connect with Retry</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={start}>
          <Text style={styles.buttonText}>Start Acquisition</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={stop}>
          <Text style={styles.buttonText}>Stop Acquisition</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={state}>
          <Text style={styles.buttonText}>Get State</Text>
        </TouchableOpacity>

        <Text style={styles.sectionTitle}>Battery Control (BITalino 2)</Text>

        <TouchableOpacity style={styles.button} onPress={getBatteryLevel}>
          <Text style={styles.buttonText}>Get Battery Level</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={setBatteryThreshold}>
          <Text style={styles.buttonText}>Set Battery Threshold (30)</Text>
        </TouchableOpacity>

        <Text style={styles.sectionTitle}>Digital Outputs</Text>

        <View style={styles.digitalOutputsContainer}>
          {digitalOutputs.map((value, index) => (
            <TouchableOpacity
              key={index}
              style={[
                styles.digitalOutputButton,
                value === 1 && styles.digitalOutputButtonActive
              ]}
              onPress={() => toggleDigitalOutput(index)}
            >
              <Text style={styles.digitalOutputText}>O{index + 1}: {value === 1 ? "ON" : "OFF"}</Text>
            </TouchableOpacity>
          ))}
        </View>

        <Text style={styles.sectionTitle}>PWM Control (BITalino 2)</Text>

        <View style={styles.pwmContainer}>
          <TouchableOpacity style={styles.pwmButton} onPress={decreasePWM}>
            <Text style={styles.buttonText}>-</Text>
          </TouchableOpacity>
          
          <View style={styles.pwmValueContainer}>
            <Text style={styles.pwmValueText}>{pwmValue}</Text>
            <Text style={styles.pwmVoltageText}>
              ~{((3.3 * (pwmValue + 1)) / 256).toFixed(2)}V
            </Text>
          </View>
          
          <TouchableOpacity style={styles.pwmButton} onPress={increasePWM}>
            <Text style={styles.buttonText}>+</Text>
          </TouchableOpacity>
        </View>

        <TouchableOpacity style={styles.buttonSecondary} onPress={setPWM}>
          <Text style={styles.buttonText}>Set PWM Value</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.buttonDanger} onPress={disconnect}>
          <Text style={styles.buttonText}>Disconnect</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#f5f5f5",
  },
  content: {
    padding: 20,
    paddingTop: 60,
  },
  title: {
    fontSize: 24,
    fontWeight: "bold",
    textAlign: "center",
    marginBottom: 20,
    color: "#333",
  },
  infoCard: {
    backgroundColor: "#fff",
    padding: 15,
    borderRadius: 10,
    marginBottom: 15,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  infoTitle: {
    fontSize: 16,
    fontWeight: "bold",
    marginBottom: 8,
    color: "#2196F3",
  },
  infoText: {
    fontSize: 14,
    color: "#666",
    marginBottom: 4,
  },
  button: {
    backgroundColor: "#2196F3",
    padding: 15,
    borderRadius: 10,
    marginBottom: 10,
    alignItems: "center",
  },
  buttonSecondary: {
    backgroundColor: "#4CAF50",
    padding: 15,
    borderRadius: 10,
    marginBottom: 10,
    alignItems: "center",
  },
  buttonDanger: {
    backgroundColor: "#f44336",
    padding: 15,
    borderRadius: 10,
    marginBottom: 10,
    alignItems: "center",
  },
  buttonText: {
    color: "#fff",
    fontSize: 16,
    fontWeight: "600",
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: "bold",
    marginTop: 20,
    marginBottom: 10,
    color: "#333",
  },
  digitalOutputsContainer: {
    flexDirection: "row",
    flexWrap: "wrap",
    justifyContent: "space-between",
    marginBottom: 15,
  },
  digitalOutputButton: {
    backgroundColor: "#ccc",
    padding: 12,
    borderRadius: 8,
    marginBottom: 10,
    width: "48%",
    alignItems: "center",
  },
  digitalOutputButtonActive: {
    backgroundColor: "#4CAF50",
  },
  digitalOutputText: {
    color: "#fff",
    fontSize: 14,
    fontWeight: "600",
  },
  pwmContainer: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    marginBottom: 15,
  },
  pwmButton: {
    backgroundColor: "#2196F3",
    padding: 15,
    borderRadius: 10,
    width: 60,
    alignItems: "center",
  },
  pwmValueContainer: {
    backgroundColor: "#fff",
    padding: 15,
    borderRadius: 10,
    marginHorizontal: 15,
    minWidth: 120,
    alignItems: "center",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  pwmValueText: {
    fontSize: 24,
    fontWeight: "bold",
    color: "#333",
  },
  pwmVoltageText: {
    fontSize: 14,
    color: "#666",
    marginTop: 4,
  },
});
    alignItems: "center",
    justifyContent: "center",
    marginBottom: 10,
  },
  text: {
    color: "#fff",
  },
});

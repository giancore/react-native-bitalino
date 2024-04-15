import { StyleSheet, Text, View } from 'react-native';

import * as ReactNativeBitalino from 'react-native-bitalino';

export default function App() {
  return (
    <View style={styles.container}>
      <Text>{ReactNativeBitalino.hello()}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});

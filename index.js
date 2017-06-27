import { NativeModules, NativeEventEmitter } from "react-native";

const { RNNordicDfu } = NativeModules;

function rejectPromise(message) {
  return new Promise((resolve, reject) => {
    reject(new Error("NordicDFU.startDFU: " + message));
  });
}

function startDFU({ deviceAddress, deviceName = null, filePath }) {
  if (deviceAddress == undefined) {
    return rejectPromise("No deviceAddress defined");
  }
  if (filePath == undefined) {
    return rejectPromise("No filePath defined");
  }
  const upperDeviceAddress = deviceAddress.toUpperCase();
  return RNNordicDfu.startDFU(upperDeviceAddress, deviceName, filePath);
}
const DFUEmitter = new NativeEventEmitter(RNNordicDfu);
const NordicDFU = { startDFU };

export { NordicDFU, DFUEmitter };

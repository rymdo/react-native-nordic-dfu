import { NativeModules, NativeEventEmitter } from "react-native";

const { RNNordicDfu } = NativeModules;
const NordicDFU = RNNordicDfu;

const DFUEmitter = new NativeEventEmitter(RNNordicDfu);

export { NordicDFU, DFUEmitter };

/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from "react";
import { AppRegistry, StyleSheet, Text, View, Image } from "react-native";
import NordicDFU from "react-native-nordic-dfu";
import RNFetchBlob from "react-native-fetch-blob";
const FB = RNFetchBlob.config({
  fileCache: true,
  appendExt: "zip"
});
export default class NordicDFUExample extends Component {
  constructor(props) {
    super(props);
    this.state = { imagefile: "start" };
  }
  componentDidMount() {
    FB.fetch("GET", "http://localhost:1234/app-zip").then(res => {
      console.log("file saved to", res.path());
      this.setState({ imagefile: res.path() });
    });
  }
  render() {
    console.log("FILE:" + this.state.imagefile);
    NordicDFU.startDFU("SS", console.log);
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Welcome to React Native Broder!
        </Text>
        {/*<Image
          style={{ width: 50, height: 50 }}
          source={{ uri: "file://" + this.state.imagefile }}
        />*/}
        <Text style={styles.instructions}>
          To get started, edit index.android.js
        </Text>
        <Text style={styles.instructions}>
          Double tap R on your keyboard to reload,{"\n"}
          Shake or press menu button for dev menu
        </Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#F5FCFF"
  },
  welcome: {
    fontSize: 20,
    textAlign: "center",
    margin: 10
  },
  instructions: {
    textAlign: "center",
    color: "#333333",
    marginBottom: 5
  }
});

AppRegistry.registerComponent("NordicDFUExample", () => NordicDFUExample);

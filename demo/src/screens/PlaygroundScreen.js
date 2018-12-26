import React, {Component} from 'react';
import _ from 'lodash';
import {StyleSheet, View as RNView, Text as RNText} from 'react-native';
import {Colors, Constants, View, Text, Button, Modal} from 'react-native-ui-lib'; //eslint-disable-line

const Profiler = React.unstable_Profiler;

export default class PlaygroundScreen extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  measurements = [];

  componentDidMount() {
    this.interval = setInterval(() => {
      this.clearMeasurements();
    }, 1000);
  }

  clearMeasurements = () => {
    this.measurements.push(this.state.uilib.baseDuration / this.state.react.baseDuration);
    this.setState({uilib: undefined, react: undefined});
    if (this.measurements.length >= 10) {
      clearInterval(this.interval);
    }
  };

  logMeasurement = async (id, phase, actualDuration, baseDuration) => {
    if (!this.state[id]) {
      // see output during DEV
      console.log({id, phase, actualDuration, baseDuration});
      this.setState({[id]: {phase, actualDuration, baseDuration}});
    }
  };

  renderUilib() {
    return (
      <Profiler id={'uilib'} onRender={this.logMeasurement}>
        <View flex center _style={styles.container}>
          {/* <Text>UILIB VIEW</Text> */}
        </View>
      </Profiler>
    );
  }

  renderReactNative() {
    return (
      <Profiler id={'react'} onRender={this.logMeasurement}>
        <RNView>{/* <RNText>RN VIEW</RNText> */}</RNView>
      </Profiler>
    );
  }

  renderDifference() {
    const {uilib, react} = this.state;
    if (uilib && react) {
      return (
        <View flex center>
          <Text text30>{this.measurements.length}</Text>
          <Text text30>{_.sum(this.measurements) / this.measurements.length}</Text>
        </View>
      );
    }
  }

  render() {
    return (
      <View flex>
        {this.renderReactNative()}
        {this.renderUilib()}
        {this.renderDifference()}
        <View flex center>
          <Button label="Clear" onPress={this.clearMeasurements} />
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.dark80,
  },
});

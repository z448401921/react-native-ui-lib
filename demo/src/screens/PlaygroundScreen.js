import React, {Component} from 'react';
import {StyleSheet, ScrollView, Clipboard} from 'react-native';
import {Colors, View, Text} from 'react-native-ui-lib'; //eslint-disable-line
import CountryNativeData from '../data/countrydata';

export default class PlaygroundScreen extends Component {
  static id = 'unicorn.Playground';

  constructor(props) {
    super(props);
    this.state = {
      countryNames: undefined,
    };
  }

  componentDidMount() {
    CountryNativeData.getCountryByLocale('en', (result) => {
      Clipboard.setString(result);
      this.setState({
        countryNames: result,
      });
    });
  }

  render() {
    return (
      <ScrollView flex center style={styles.container}>
        <Text>{JSON.stringify(this.state.countryNames)}</Text>
      </ScrollView>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.dark80,
  },
});

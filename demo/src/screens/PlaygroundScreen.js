import React, {Component} from 'react';
import {StyleSheet, ScrollView, Clipboard} from 'react-native';
import {Colors, View, Text} from 'react-native-ui-lib'; //eslint-disable-line
import CountryNativeData from '../data/countrydata';

export default class PlaygroundScreen extends Component {

  constructor(props) {
    super(props);
    this.state = {
      countryNames: undefined,
    };
  }

  componentDidMount() {
    setTimeout(() => {
      CountryNativeData.updateLanguage('en');
      this.getCountries();
    }, 5000);
  }

  getCountries = async () => {
    const countries = await CountryNativeData.loadCountriesDialCode();
    const countries1 = await CountryNativeData.loadCountriesCurrency();
    console.log('countries', countries);
    Clipboard.setString(countries1);
  };

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

import logo from './logo.svg';
import './App.css';

import {createAppContainer} from 'react-navigation';
import {createBottomTabNavigator} from 'react-navigation-tabs';
import HomeScreen from './screens/HomeScreen';
import SessionsScreen from './screens/SessionsScreen';
import SettingsScreen from './screens/SettingsScreen';

const TabNavigator = createBottomTabNavigator({
  Home: {
    screen: HomeScreen,
  },
  Chat: {
    screen: SessionsScreen,
  },
  Settings: {
    screen: SettingsScreen,
  },
});
export default createAppContainer(TabNavigator);

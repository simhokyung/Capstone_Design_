import React, { useRef, useEffect } from 'react';
import { createMaterialTopTabNavigator, MaterialTopTabBarProps } from '@react-navigation/material-top-tabs';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { PlatformPressable } from '@react-navigation/elements';
import { View, Animated, Dimensions, StyleSheet, Text, Image } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { OutdoorScreen } from '../screens/OutdoorScreen';
import { IndoorScreen } from '../screens/IndoorScreen';
import { HomeScreen } from '../screens/HomeScreen';
import { NotificationScreen } from '../screens/NotificationScreen';
import { AccountScreen } from '../screens/AccountScreen';

const Tab = createMaterialTopTabNavigator();
const { width: screenWidth } = Dimensions.get('window');

export type ParamList = {
  Outdoor: undefined;
  Indoor: undefined;
  Home: undefined;
  Notification: undefined;
  Account: undefined;
};

function CustomTabBar({ state, descriptors, navigation }: MaterialTopTabBarProps) {
  const insets = useSafeAreaInsets();
  const tabCount = state.routes.length;
  const tabWidth = screenWidth / tabCount;
  const translateX = useRef(new Animated.Value(state.index * tabWidth)).current;

  useEffect(() => {
    Animated.spring(translateX, {
      toValue: state.index * tabWidth,
      useNativeDriver: true,
    }).start();
  }, [state.index, tabWidth, translateX]);

  return (
    <View style={[
      styles.tabBarContainer,
      { paddingBottom: insets.bottom }
    ]}>
      <Animated.View
        style={[
          styles.indicator,
          {
            width: tabWidth,
            transform: [{ translateX }],
            bottom: insets.bottom,
          },
        ]}
      />
      {state.routes.map((route, index) => {
        const { options } = descriptors[route.key];
        const label = typeof options.tabBarLabel === 'string' ? options.tabBarLabel : route.name;
        const focused = state.index === index;
        const color = focused ? '#01B3EA' : '#AAAAAA';
        let iconName: string;
        switch (route.name) {
          case 'Outdoor': iconName = focused ? 'cloud' : 'cloud-outline'; break;
          case 'Indoor': iconName = focused ? 'home' : 'home-outline'; break;
          case 'Home': iconName = focused ? 'grid' : 'grid-outline'; break;
          case 'Notification': iconName = focused ? 'notifications' : 'notifications-outline'; break;
          case 'Account': iconName = focused ? 'person' : 'person-outline'; break;
          default: iconName = 'ellipse';
        }
        return (
          <PlatformPressable
            key={route.key}
            onPress={() => navigation.navigate(route.name)}
            style={[styles.tabButton, { width: tabWidth }]} 
            android_ripple={{ color: 'transparent' }}
            accessibilityRole="button"
            accessibilityState={focused ? { selected: true } : {}}
          >
            <Ionicons name={iconName} size={24} color={color} />
            <Text style={[styles.label, { color }]}>{label}</Text>
          </PlatformPressable>
        );
      })}
    </View>
  );
}

export default function TabNavigator() {
  return (
    <View style={styles.container}>
      <View style={styles.logoContainer}>
        <Image
          source={require('../assets/everyair_logo_color.png')}
          style={styles.logo_everyair}
        />
      </View>
      <Tab.Navigator
        initialRouteName="Home"
        backBehavior="none"
        tabBarPosition="bottom"
        tabBar={props => <CustomTabBar {...props} />}
        screenOptions={{
          swipeEnabled: true,
          animationEnabled: true,
        }}
        style={styles.navigatorContainer}
      >
        <Tab.Screen name="Outdoor" component={OutdoorScreen} options={{ tabBarLabel: '실외' }} />
        <Tab.Screen name="Indoor" component={IndoorScreen} options={{ tabBarLabel: '실내' }} />
        <Tab.Screen name="Home" component={HomeScreen} options={{ tabBarLabel: '홈' }} />
        <Tab.Screen name="Notification" component={NotificationScreen} options={{ tabBarLabel: '알림' }} />
        <Tab.Screen name="Account" component={AccountScreen} options={{ tabBarLabel: '계정' }} />
      </Tab.Navigator>
    </View>
    
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F6FAFF',
  },
  logoContainer: {
    flex: 1, 
    justifyContent: 'flex-end',
  },
  navigatorContainer: {
    flex: 10,
  },
  logo_everyair: {
    width: 160,
    height: 47,
    resizeMode: 'contain',
  },
  tabBarContainer: {
    flexDirection: 'row',
    elevation: 2,
    backgroundColor: '#fff',
  },
  indicator: {
    position: 'absolute',
    height: 4,
    backgroundColor: '#01B3EA',
    borderRadius: 2,
  },
  tabButton: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 8,
  },
  label: {
    fontSize: 12,
    fontWeight: 700,
  },
});
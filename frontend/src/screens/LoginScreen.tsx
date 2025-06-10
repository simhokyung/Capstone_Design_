import React, { useRef, useState, useEffect } from 'react';
import { View, Image, Pressable, Text, StyleSheet, FlatList, Dimensions, Animated } from 'react-native';
import SlideModal_login from '../components/SlideModal_login';
import SlideModal_signup from '../components/SlideModal_signup';

const { width: SCREEN_WIDTH} = Dimensions.get('window');
const AUTO_SCROLL_INTERVAL = 2000;

type Slide = { key: string; image: any };
const slides = [
  {
    key: '1',
    image: require('../assets/present_set_1.png')
  },
  {
    key: '2',
    image: require('../assets/present_set_2.png')
  },
  {
    key: '3',
    image: require('../assets/present_set_3.png')
  },
]

type LoginScreenProps = {
  setIsSignedIn: React.Dispatch<React.SetStateAction<boolean>>;
};

export const LoginScreen = ({ setIsSignedIn }: LoginScreenProps) => {
  const [currentIndex, setCurrentIndex] = useState<number>(0);
  const flatListRef = useRef<FlatList<Slide>>(null);
  const progress = useRef(new Animated.Value(0)).current;
  const [modalVisible_login, setModalVisible_login] = useState(false);
  const [modalVisible_signup, setModalVisible_signup] = useState(false);

  useEffect(() => {
    progress.setValue(0);
    Animated.timing(progress, {
      toValue: 1,
      duration: AUTO_SCROLL_INTERVAL,
      useNativeDriver: false,
    }).start();

    const timer = setInterval(() => {
      const nextIndex = (currentIndex + 1) % slides.length;
      flatListRef.current?.scrollToIndex({ index: nextIndex, animated: true });
    }, AUTO_SCROLL_INTERVAL);
    return () => clearInterval(timer);
  }, [currentIndex]);

  const progressWidth = progress.interpolate({
    inputRange: [0, 1],
    outputRange: ['0%', '100%'],
  });

  return (
    <View style={styles.container}>
      <View style={styles.indicatorContainer}>
        {slides.map((_, idx) => {
          if (idx === currentIndex) {
            return (
              <View key={idx} style={styles.indicator_base}>
                <Animated.View
                  style={[styles.indicator_loading, { width: progressWidth }]}
                />
              </View>
            );
          } else if (idx < currentIndex) {
            return (
              <View key={idx} style={[styles.indicator_base, styles.indicator_active]}>
              </View>
            );
          } else {
            return (
              <View key={idx} style={[styles.indicator_base, styles.indicator_inactive]}>
              </View>
            );
          }
        })}
      </View>

      <View style={styles.presentContainer}>
        <FlatList
          ref={flatListRef}
          style={styles.present}
          data={slides}
          horizontal
          pagingEnabled={true}
          showsHorizontalScrollIndicator={false}
          keyExtractor={item => item.key}
          renderItem={({ item }) => (
            <Image source={item.image} style={styles.present_image} />
          )}
          onViewableItemsChanged={({ viewableItems }) => {
            if (viewableItems.length > 0 && viewableItems[0].index !== null) {
              setCurrentIndex(viewableItems[0].index);
            }
          }}
          viewabilityConfig={{ viewAreaCoveragePercentThreshold: 50 }}
        />
      </View>

      <View style={styles.selectContainer}>
        
        <Pressable
          style={styles.signup_everyair}
          android_ripple={{
          color: 'rgba(0,0,0,0.1)',
          radius: 170,
          }}
          onPress={() => setModalVisible_signup(true)}
        >
          <Image
            source={require('../assets/everyair_logo_mono.png')}
            style={styles.signup_everyair_image}
          />
          <Text style={styles.signup_everyair_label}>EveryAir ID로 시작하기</Text>
        </Pressable>
        <SlideModal_signup isVisible={modalVisible_signup} onClose={() => setModalVisible_signup(false)}>
        </SlideModal_signup>
        <View  style={styles.login}>
          <Text style={styles.login_label}>기존 계정이 있으신가요?</Text>
          <Pressable
            style={styles.login_button}
            android_ripple={{
            color: 'rgba(0,0,0,0.1)',
            radius: 40,
            }}
            onPress={() => setModalVisible_login(true)}
          >
            <Text style={styles.login_button_label}>로그인</Text>
          </Pressable>
          <SlideModal_login  setIsSignedIn={setIsSignedIn}  isVisible={modalVisible_login} onClose={() => setModalVisible_login(false)}>
          </SlideModal_login>
        </View>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#E5F1FF',
  },
  indicatorContainer: {
    flex: 1,
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
  },
  presentContainer: {
    flex: 7,
  },
  selectContainer: {
    flex: 3,
  },
  indicator_base: {
    width: 105,
    height: 6,
    backgroundColor: '#CCCCCC',
    borderRadius: 4,
    marginTop: 40,
    marginHorizontal: 5,
    elevation: 2,
    overflow: 'hidden',
  },
  indicator_loading: {
    height: 6,
    backgroundColor: '#FFFFFF',
  },
  indicator_active: { 
    backgroundColor: '#FFFFFF' 
  },
  indicator_inactive: {
    backgroundColor: '#CCCCCC',
  },
  present: {

  },
  present_image: {
    width: SCREEN_WIDTH - 32,
    height: 500,
    resizeMode: 'contain',
    marginTop: 20,
    marginLeft: 24,
    marginRight: 8,
  },
  signup_google: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    
    backgroundColor: '#FFFFFF',
    marginHorizontal: 16,
    marginVertical: 7,
    borderRadius: 10,
    paddingVertical: 14,
    elevation: 4,
  },
  signup_google_image: {
    width: 25,
    height: 25,
    marginRight: 8,
    resizeMode: 'contain',
  },
  signup_google_label: {
    fontSize: 20,
    fontWeight: 600,
    lineHeight: 25,
    color: '#000000',
  },
  signup_everyair: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    
    backgroundColor: '#01B3EA',
    marginHorizontal: 16,
    marginTop: 60,
    marginVertical: 20,
    borderRadius: 10,
    paddingVertical: 14,
    elevation: 4,
  },
  signup_everyair_image: {
    width: 90,
    height: 25,
    marginRight: 8,
    resizeMode: 'contain',
  },
  signup_everyair_label: {
    fontSize: 20,
    fontWeight: 600,
    lineHeight: 25,
    color: '#FFFFFF',
  },
  login: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginVertical: 2,
  },
  login_label: {
    fontSize: 18,
    fontWeight: 700,
    lineHeight: 20,
  },
  login_button: {
    padding: 10,
  },
  login_button_label: {
    fontSize: 18,
    fontWeight: 700,
    lineHeight: 20,
    paddingVertical: 1,
    borderBottomWidth: 2,
  },
});

export default LoginScreen;
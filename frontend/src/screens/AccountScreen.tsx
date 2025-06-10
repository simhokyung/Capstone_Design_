import React, { useState } from 'react';
import { View, Image, Pressable, Text, StyleSheet, ScrollView } from 'react-native';
import SlideModal_change from '../components/SlideModal_change';
import axios from 'axios';

export const AccountScreen = () => {
  const [activeOptions, setActiveOptions] = useState<boolean[]>([true, false, true, false]);

  const toggleTab = (index: number) => {
    setActiveOptions(prev => {
      const next = prev.map((flag, i) => i === index ? !flag : flag);
      if (index === 1 && next[1] === true) {
        next[0] = true;
      }
      if (index === 3 && next[3] === true) {
        next[2] = true;
      }
      if (index === 0 && next[0] === false) {
        next[1] = false;
      }
      if (index === 2 && next[2] === false) {
        next[3] = false;
      }
      return next;
    });
  };

  const [modalVisible_change, setModalVisible_change] = useState(false);
  const [userData, setUserData] = useState({
    name: '',
    email: '',
    date: '',
  });

  const API_BASE = 'http://18.191.176.79:8080';

  const fetchData = async () => {
    const userID = 1;
    try {
      const response = await axios.get(`${API_BASE}/users/${userID}`);
      setUserData(response.data.data)
      setActiveOptions(response.data.option)
    } catch (e) {
    console.error('에러:', e);
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.accountContainer}>
        <View style={styles.account}>
          <View style={styles.profile}>
            <Image
              source={require('../assets/account.png')}
              style={styles.profile_image}
            />
            <Text style={styles.profile_name}>{userData.name}</Text>
          </View>
          <View style={styles.email}>
            <Text style={styles.label}>이메일</Text>
            <View style={styles.textBox}>
              <ScrollView
                horizontal
                showsHorizontalScrollIndicator={false}
              >
                <Text style={styles.value}>
                 {userData.name}
                </Text>
              </ScrollView>
            </View>
          </View>
          <View style={styles.registrationDate}>
            <Text style={styles.label}>가입 일자</Text>
              <View style={styles.textBox}>
                <Text style={styles.value}>
                  {userData.date}
                </Text>
              </View>
            </View>
          <View style={styles.notification}>
            <Text style={styles.label}>
              알림 설정
            </Text>
            <View style={styles.button_container}>
              <Pressable
                style={[styles.button_base, activeOptions[0] === true ? styles.button_active : styles.button_inactive]}
                android_ripple={{
                  color: 'rgba(0,0,0,0.1)',
                  radius: 85,
                }}
                onPress={() => toggleTab(0)}
              >
                <Text style={[styles.button_label_base, activeOptions[0] === true ? styles.button_label_active : styles.button_label_inactive]}>
                  알림{'\n'}{activeOptions[0] === true ? 'ON' : 'OFF'}
                </Text>
              </Pressable>
              <Pressable
                style={[styles.button_base, activeOptions[1] === true ? styles.button_active : styles.button_inactive]}
                android_ripple={{
                  color: 'rgba(0,0,0,0.1)',
                  radius: 85,
                }}
                onPress={() => toggleTab(1)}
              >
                <Text style={[styles.button_label_base, activeOptions[1] === true ? styles.button_label_active : styles.button_label_inactive]}>
                  야간 알림{'\n'}{activeOptions[1] === true ? 'ON' : 'OFF'}
                </Text>
              </Pressable>
            </View>
          </View>
          <View style={styles.alert}>
            <Text style={styles.label}>
              경고 설정
            </Text>
            <View style={styles.button_container}>
              <Pressable
                style={[styles.button_base, activeOptions[2] === true ? styles.button_active : styles.button_inactive]}
                android_ripple={{
                  color: 'rgba(0,0,0,0.1)',
                  radius: 85,
                }}
                onPress={() => toggleTab(2)}
              >
                <Text style={[styles.button_label_base, activeOptions[2] === true ? styles.button_label_active : styles.button_label_inactive]}>
                  경고{'\n'}{activeOptions[2] === true ? 'ON' : 'OFF'}
                </Text>
              </Pressable>
              <Pressable
                style={[styles.button_base, activeOptions[3] === true ? styles.button_active : styles.button_inactive]}
                android_ripple={{
                  color: 'rgba(0,0,0,0.1)',
                  radius: 85,
                }}
                onPress={() => toggleTab(3)}
              >
                <Text style={[styles.button_label_base, activeOptions[3] === true ? styles.button_label_active : styles.button_label_inactive]}>
                  야간 경고{'\n'}{activeOptions[3] === true ? 'ON' : 'OFF'}
                </Text>
              </Pressable>
            </View>
          </View>
          <View style={styles.others}>
            <Pressable
              style={styles.logout}
              android_ripple={{
                color: 'rgba(0,0,0,0.1)',
                radius: 159,
              }}
            >
              <Text style={styles.logout_label}>
                로그아웃
              </Text>
            </Pressable>
            <View style={styles.changeDelete}>
              <Pressable
                style={styles.change}
                android_ripple={{
                  color: 'rgba(0,0,0,0.1)',
                  radius: 77,
                }}
                onPress={() => setModalVisible_change(true)}
              >
                <Text style={styles.change_label}>
                  비밀번호 변경
                </Text>
              </Pressable>
              <SlideModal_change isVisible={modalVisible_change} onClose={() => setModalVisible_change(false)}>
              </SlideModal_change>
              <Pressable
                style={styles.delete}
                android_ripple={{
                  color: 'rgba(0,0,0,0.1)',
                  radius: 77,
                }}
              >
                <Text style={styles.delete_label}>
                  회원 탈퇴
                </Text>
              </Pressable>
            </View>
          </View>
        </View>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F6FAFF',
  },
  accountContainer: {
    flex: 7,
    justifyContent: 'flex-end',
  },
  account: {
    height: '97%',
    backgroundColor: '#FFFFFF',
    borderTopLeftRadius: 30,
    borderTopRightRadius: 30,
    elevation: 10,
  },
  profile: {
    flex: 2,
    flexDirection: 'row',
    alignItems:'center',
  },
  email: {
    flex: 2,
  },
  registrationDate: {
    flex: 2,
  },
  notification: {
    flex: 2.5,
  },
  alert: {
    flex: 2.5,
  },
  others: {
    flex: 4,
    alignItems: 'center',
  },
  profile_image: {
    height: 50,
    width: 50,
    resizeMode: 'contain',
    marginTop: 8,
    marginHorizontal: 10,
  },
  profile_name: {
    fontSize: 26,
    fontWeight : 500,
  },
  label: {
    fontSize: 20,
    fontWeight : 500,
    marginTop: '1%',
    marginHorizontal: '5%',
  },
  textBox: {
    backgroundColor: '#E9E9E9',
    width: '90%',
    height: '40%',
    marginVertical: '3%',
    marginHorizontal: '5%',
    borderRadius: 100,
    justifyContent: 'center',
  },
  value: {
    fontSize: 19,
    fontWeight : 400,
    width: 500,
    marginHorizontal: 17,
  },
  button_container: {
    flexDirection: 'row',
    height: '70%',
    justifyContent: 'center',
    alignItems: 'center',
  },
  button_base: {
    width: '44%',
    height: '90%',
    borderRadius: 10,
    backgroundColor: '#D9D9D9',
    marginHorizontal: '1%',
    justifyContent: 'center',
    alignItems: 'center',
    elevation: 4,
  },
  button_active: {
    backgroundColor: '#01B3EA',
  },
  button_inactive: {
    backgroundColor: '#D9D9D9',
  },
  button_label_base: {
    fontSize: 20,
    fontWeight: 500,
    textAlign: 'center',
    color: '#000000',
  },
  button_label_active: {
    color: '#FFFFFF',
  },
  button_label_inactive: {
    color: '#000000',
  },
  logout: {
    width: '90%',
    height: '23%',
    borderRadius: 100,
    backgroundColor: '#D9D9D9',
    marginTop: '10%',
    justifyContent: 'center',
    alignItems: 'center',
    elevation: 4,
  },
  logout_label: {
    fontSize: 20,
    fontWeight: 500,
    textAlign: 'center',
    color: '#000000',
  },
  changeDelete: {
    flexDirection: 'row',
    marginTop: '5%',
    height: '23%',
  },
  change: {
    width: '44%',
    height: '100%',
    borderRadius: 100,
    backgroundColor: '#D9D9D9',
    marginHorizontal: '1%',
    justifyContent: 'center',
    alignItems: 'center',
    elevation: 4,
  },
  change_label: {
    fontSize: 20,
    fontWeight: 500,
    textAlign: 'center',
    color: '#000000',
  },
  delete: {
    width: '44%',
    height: '100%',
    borderRadius: 100,
    backgroundColor: '#D9D9D9',
    marginHorizontal: '1%',
    justifyContent: 'center',
    alignItems: 'center',
    elevation: 4,
  },
  delete_label: {
    fontSize: 20,
    fontWeight: 500,
    textAlign: 'center',
    color: '#000000',
  },
  support: {
    marginTop: '5%',
  },
  support_label: {
    fontSize: 18,
    fontWeight: 700,
    lineHeight: 20,
    paddingVertical: 1,
    borderBottomWidth: 2,
  },
});

export default AccountScreen;
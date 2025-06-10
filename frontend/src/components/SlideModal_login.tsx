import React, { useState, useRef } from 'react';
import { View, Text, Pressable, StyleSheet, TextInput, Alert } from 'react-native';
import Modal from 'react-native-modal';
import Ionicons from 'react-native-vector-icons/Ionicons';
import SlideModal_change from '../components/SlideModal_change';
import SlideModal_signup from '../components/SlideModal_signup';
import axios from 'axios';

const API_BASE = 'http://18.191.176.79:8080';

type SlideModalProps = {
  isVisible: boolean;
  onClose: () => void;
  children?: React.ReactNode;
  setIsSignedIn: React.Dispatch<React.SetStateAction<boolean>>;
};

export default function SlideModal_login({ isVisible, onClose, children, setIsSignedIn }: SlideModalProps) {
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");

  const [modalVisible_change, setModalVisible_change] = useState<boolean>(false);
  const [modalVisible_signup, setModalVisible_signup] = useState<boolean>(false);

  const emailRef = useRef<TextInput>(null);
  const passwordRef = useRef<TextInput>(null);

  const handleLogin = async () => {
    if (!email) {
      Alert.alert('오류', '이메일을 입력해주세요.');
      return;
    }
    if (!password) {
      Alert.alert('오류', '비밀번호를 입력해주세요.');
      return;
    }

    try {
      const response = await axios.post(`${API_BASE}/auth/login`, {
        email,
        password,
      });

      if (response.status === 200) {
        Alert.alert('완료', '로그인 성공');
        setIsSignedIn(true);
        onClose();
      } else {
        Alert.alert('오류', '로그인에 실패했습니다.');
      }
    } catch (error: any) {
      console.error('Login error:', error);
      Alert.alert('오류', error.response?.data?.message || '서버 오류가 발생했습니다.');
    }
  };

  return (
    <Modal
      isVisible={isVisible}
      onBackdropPress={onClose}
      onBackButtonPress={onClose}
      animationIn="slideInUp"
      animationOut="slideOutDown"
      backdropOpacity={0.5}
      style={styles.modalWrapper}
    >
      <View style={styles.modalContent}>
        <View style={styles.body}>
          <Text style={styles.label}>로그인</Text>
          <View style={styles.content}>
            <View style={styles.inputBox}>
              <Ionicons name="mail-outline" size={25} color="#AAAAAA" />
              <TextInput
                ref={emailRef}
                placeholder="E-Mail(example@company.com)"
                placeholderTextColor="#AAAAAA"
                value={email}
                onChangeText={setEmail}
                keyboardType="email-address"
                returnKeyType="next"
                onSubmitEditing={() => passwordRef.current?.focus()}
                autoCapitalize="none"
                style={styles.input}
              />
            </View>
            <View style={styles.inputBox}>
              <Ionicons name="lock-closed-outline" size={25} color="#AAAAAA" />
              <TextInput
                ref={passwordRef}
                placeholder="Password"
                placeholderTextColor="#AAAAAA"
                value={password}
                onChangeText={setPassword}
                returnKeyType="done"
                secureTextEntry
                onSubmitEditing={handleLogin}
                style={styles.input}
              />
            </View>
          </View>
        </View>

        <Pressable
          style={styles.confirmButton}
          android_ripple={{ color: 'rgba(0,0,0,0.1)', radius: 40 }}
          onPress={handleLogin}
        >
          <Text style={styles.confirmText}>로그인하기</Text>
        </Pressable>

        <View style={styles.others}>
          <Pressable
            style={styles.textButton}
            android_ripple={{ color: 'rgba(0,0,0,0.1)', radius: 40 }}
            onPress={() => setModalVisible_change(true)}
          >
            <Text style={styles.textButton_label}>비밀번호 재설정</Text>
          </Pressable>
          <SlideModal_change
            isVisible={modalVisible_change}
            onClose={() => setModalVisible_change(false)}
          />

          <View style={styles.verticalLine} />

          <Pressable
            style={styles.textButton}
            android_ripple={{ color: 'rgba(0,0,0,0.1)', radius: 40 }}
            onPress={() => setModalVisible_signup(true)}
          >
            <Text style={styles.textButton_label}>회원가입</Text>
          </Pressable>
          <SlideModal_signup
            isVisible={modalVisible_signup}
            onClose={() => setModalVisible_signup(false)}
          />
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  modalWrapper: {
    justifyContent: 'flex-start',
    margin: 0,
  },
  modalContent: {
    backgroundColor: '#FFFFFF',
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    marginTop: 40,
    height: 800,
  },
  body: {},
  label: {
    fontSize: 30,
    fontWeight: '700',
    marginVertical: 16,
    marginHorizontal: 32,
  },
  content: {
    alignItems: 'center',
  },
  inputBox: {
    width: '85%',
    borderBottomWidth: 1.5,
    marginTop: 12,
    flexDirection: 'row',
    alignItems: 'center',
  },
  input: {
    color: '#000000',
    fontSize: 18,
    fontWeight: '400',
    borderWidth: 0,
    height: 48,
    width: '90%',
    textAlignVertical: 'center',
    paddingLeft: 7,
  },
  confirmButton: {
    alignSelf: 'center',
    backgroundColor: '#01B3EA',
    width: '90%',
    height: 45,
    borderRadius: 100,
    justifyContent: 'center',
    alignItems: 'center',
    elevation: 4,
    marginTop: 30,
  },
  confirmText: {
    fontSize: 20,
    fontWeight: '500',
    color: '#FFFFFF',
  },
  others: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 15,
  },
  textButton: {
    marginHorizontal: 2,
    paddingVertical: 10,
    paddingHorizontal: 5,
  },
  textButton_label: {
    fontSize: 14,
    fontWeight: '500',
    borderBottomWidth: 2,
    borderBottomColor: '#000000',
  },
  verticalLine: {
    height: 20,
    width: 1,
    borderLeftWidth: 2,
    borderColor: '#000000',
    borderRadius: 100,
  },
});

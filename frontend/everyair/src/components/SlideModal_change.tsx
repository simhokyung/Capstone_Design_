import React, { useState, useRef } from 'react';
import { View, Text, Pressable, StyleSheet, TextInput, Alert } from 'react-native';
import Modal from 'react-native-modal';
import { KeyboardAwareScrollView } from 'react-native-keyboard-aware-scroll-view';
import axios from 'axios';

const API_BASE = 'http://18.191.176.79:8080';

type SlideModalProps = {
  isVisible: boolean;
  onClose: () => void;
  children?: React.ReactNode;
};

export default function SlideModal_change({ isVisible, onClose, children }: SlideModalProps) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [passwordCheck, setPasswordCheck] = useState("");

  const emailRef = useRef<TextInput>(null);
  const passwordRef = useRef<TextInput>(null);
  const passwordCheckRef = useRef<TextInput>(null);

  const handlePasswordReset = async () => {
    if (!email) {
      Alert.alert('오류', '이메일을 입력해주세요.');
      return;
    }
    if (!password || !passwordCheck) {
      Alert.alert('오류', '비밀번호를 입력해주세요.');
      return;
    }
    if (password !== passwordCheck) {
      Alert.alert('오류', '비밀번호가 일치하지 않습니다.');
      return;
    }

    try {
      const response = await axios.post(`${API_BASE}/auth/reset-password`, {
        email,
        newPassword: password,
      });

      if (response.status === 200) {
        Alert.alert('완료', '비밀번호가 성공적으로 변경되었습니다.');
        onClose();
      } else {
        Alert.alert('오류', '비밀번호 변경에 실패했습니다.');
      }
    } catch (error: any) {
      console.error('Password reset error:', error);
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
        <KeyboardAwareScrollView
          style={styles.scroll}
          enableOnAndroid={true}
          extraScrollHeight={200}
          keyboardOpeningTime={0}
        >
          <View style={styles.forScroll}>
            <View style={styles.body}>
              <Text style={styles.label}>비밀번호 재설정</Text>
              <View style={styles.content}>
                <Text style={styles.input_label}>이메일</Text>
                <View style={styles.inputBox}>
                  <TextInput
                    ref={emailRef}
                    placeholder="example@company.com"
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

                <Text style={styles.input_label}>재설정 비밀번호</Text>
                <View style={styles.inputBox}>
                  <TextInput
                    ref={passwordRef}
                    value={password}
                    onChangeText={setPassword}
                    returnKeyType="next"
                    onSubmitEditing={() => passwordCheckRef.current?.focus()}
                    secureTextEntry
                    style={styles.input}
                  />
                </View>

                <Text style={styles.input_label}>재설정 비밀번호 확인</Text>
                <View style={styles.inputBox}>
                  <TextInput
                    ref={passwordCheckRef}
                    value={passwordCheck}
                    onChangeText={setPasswordCheck}
                    returnKeyType="done"
                    onSubmitEditing={handlePasswordReset}
                    secureTextEntry
                    style={styles.input}
                  />
                </View>
              </View>
            </View>

            <Pressable
              style={styles.confirmButton}
              android_ripple={{ color: 'rgba(0,0,0,0.1)', radius: 40 }}
              onPress={handlePasswordReset}
            >
              <Text style={styles.confirmButton_label}>비밀번호 재설정하기</Text>
            </Pressable>
          </View>
        </KeyboardAwareScrollView>
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
  scroll: {
    flex: 1,
  },
  forScroll: {},
  body: {},
  label: {
    fontSize: 36,
    fontWeight: '700',
    marginVertical: 16,
    marginHorizontal: 32,
  },
  content: {
    alignItems: 'center',
  },
  input_label: {
    alignSelf: 'flex-start',
    marginLeft: '5%',
    marginTop: 10,
    fontSize: 21,
    fontWeight: '600',
  },
  inputBox: {
    backgroundColor: '#E9E9E9',
    width: '90%',
    height: 35,
    borderRadius: 100,
    marginTop: 12,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  input: {
    color: '#000000',
    fontSize: 21,
    fontWeight: '800',
    height: 48,
    width: '92%',
    textAlignVertical: 'center',
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
  confirmButton_label: {
    fontSize: 20,
    fontWeight: '600',
    color: '#FFFFFF',
  },
});

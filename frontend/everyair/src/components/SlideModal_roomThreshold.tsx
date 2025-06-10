import React, { useState, useEffect } from 'react';
import { View, Text, Pressable, StyleSheet, TextInput } from 'react-native';
import Modal from 'react-native-modal';
import Slider from '@react-native-community/slider';
import CheckBox from '@react-native-community/checkbox';

type SlideModalProps = {
  isVisible: boolean;
  onSave: (data: any) => void;
  onClose: () => void;
  initialValues: number[];
  children?: React.ReactNode;
};
  
const info_data = [
  {
    key: 'PM2.5',
    title: '초미세먼지',
    data: '미세먼지는 지름 10μm 이하의 입자상 물질로, 도로 주행, 산업 공정, 황사 등 다양한 원인에서 배출되는 1차 오염물질입니다. 지속적으로 흡입할 경우 기도 내 점액 분비를 촉진하여 호흡 곤란을 유발하고, 만성 호흡기·심혈관 질환의 발병 위험을 높일 수 있습니다.',
    unit: 'µg/m³',
    threshold: [0, 5, 15, 25, 35],
  }, 
  {
    key: 'PM10',
    title: '미세먼지',
    data: '초미세먼지는 지름 2.5μm 이하의 작은 입자로, 화석연료 연소나 발전소 배출 가스가 응결·응집하여 형성되는 2차 오염물질입니다. 입자 크기가 작아 폐포와 혈류까지 침투해 염증 반응을 일으키며, 심·뇌혈관계 질환을 악화시키고 면역 기능을 저하시킬 수 있습니다.',
    unit: 'µg/m³',
    threshold: [0, 15, 30, 50, 70],
  }, 
  {
    key: 'CO2',
    title: '이산화탄소',
    data:'이산화탄소는 화석연료 연소와 생물의 호흡 과정에서 발생하는 주요 온실가스로, 대기 중 농도가 상승할수록 지구 온난화를 가속화합니다. 밀폐된 실내 환경에서 과도하게 축적되면 두통·집중력 저하·졸음 등을 유발해 작업 능률과 쾌적도를 떨어뜨릴 수 있습니다.',
    unit: 'ppm',
    threshold: [0, 400, 1000, 2000, 3000],
  }, 
  {
    key: 'VOC',
    title: '휘발성유기화합물',
    data:'휘발성유기화합물은 페인트·접착제·세정제 등에서 쉽게 기체로 전환되는 탄화수소 계열의 화합물군입니다. 단기간 고농도로 노출되면 눈·코·목의 자극과 두통을 일으키며, 장기간 반복 노출 시 중추신경계 손상과 천식·알레르기 같은 호흡기 질환 위험을 증가시킬 수 있습니다.',
    unit: 'ppm',
    threshold: [0, 100, 400, 1000, 2000],
  }
]

export default function SlideModal_userThreshold({ isVisible, onSave, onClose, initialValues, children }: SlideModalProps) {
  const [thresholds, setThresholds] = useState<number[]>(initialValues);  // 초기 임계치
  const updateThresholds = (idx: number, newValue: number) => {
    setThresholds(prev =>
    prev.map((item, i) => i === idx ? newValue : item)
  );};
  const [checked, setChecked] = useState<boolean[]>([true, true, true, true]);
  const updateChecked = (idx: number) => {
    setChecked(prev =>
    prev.map((item, i) => i === idx ? !item : item)
  );};
  const activeChecked = (idx: number) => {
    setChecked(prev =>
    prev.map((item, i) => i === idx ? true : item)
  );};
  const [hasAllergy, setHasAllergy] = useState(false);
  const [hasAsthma, setHasAsthma] = useState(false);
  useEffect(() => {
    if(hasAllergy){
      activeChecked(0);
      activeChecked(1);
      updateThresholds(0, 10);
      updateThresholds(1, 25);
    }
    if(hasAsthma){
      activeChecked(0);
      activeChecked(1);
      updateThresholds(0, 5);
      updateThresholds(1, 15);
    }

  }, [hasAllergy, hasAsthma]);

  useEffect(() => {
    if (isVisible) {
      setThresholds(initialValues);
    }
  }, [isVisible, initialValues]);

  const handleSave = () => {
    onSave(thresholds);
    onClose();
  }
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
          <Text style={styles.label}>
            방별 관리 기준치 수정
          </Text>
          <View style={styles.content}>
            <View style={styles.check}>
              <Pressable
                style={styles.check_box}
                onPress={() => setHasAllergy(!hasAllergy)}
              >
                <CheckBox
                  value={hasAllergy}
                  onValueChange={() => setHasAllergy(!hasAllergy)}
                  tintColors={{ true: '#01B3EA', false: '#777' }}
                  style={styles.check_checkbox}
                />
                <Text style={styles.check_label}>
                  알러지 여부
                </Text>
              </Pressable>
              <Pressable
                style={styles.check_box}
                onPress={() => setHasAsthma(!hasAsthma)}
              >
                <CheckBox
                  value={hasAsthma}
                  onValueChange={() => setHasAsthma(!hasAsthma)}
                  tintColors={{ true: '#01B3EA', false: '#777' }}
                  style={styles.check_checkbox}
                />
                <Text style={styles.check_label}>
                  천식 여부
                </Text>
              </Pressable>
            </View>
            
            {info_data.map((item, i) => (
              <View style={styles.element} key={item.key}>
                  <Pressable
                    style={styles.elementBox}
                    onPress={() => updateChecked(i)}
                  >
                    <CheckBox
                      value={checked[i]}
                      onValueChange={() => updateChecked(i)}
                      tintColors={{ true: '#01B3EA', false: '#777' }}
                      style={styles.checkbox}
                    />
                    <Text style={[styles.element_label, checked[i] ? styles.element_label_active : styles.element_label_inactive]}>
                      {item.key} 관리 임계치: {thresholds[i]}{item.unit}
                    </Text>
                  </Pressable>
                <View style={styles.sliderBox}>
                  {
                    checked[i]?
                    <Slider
                    style={styles.slider}
                    minimumValue={item.threshold[0]}
                    maximumValue={item.threshold[4]}
                    step={1}
                    value={thresholds[i]}
                    onValueChange={v => updateThresholds(i, v)}
                    minimumTrackTintColor="#01B3EA"
                    maximumTrackTintColor="#777777"
                    thumbTintColor="#01B3EA"
                    />:<></>
                  }
                  
                </View>
              </View>
            ))}
            <Pressable
              style={styles.confirmButton}
              android_ripple={{
                color: 'rgba(0,0,0,0.1)',
                radius: 40,
              }}
              onPress={handleSave}
            >
              <Text style={styles.confirmButton_label}>
                수정 내용 저장하기
              </Text>
            </Pressable>
          </View>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  modalWrapper: {
    justifyContent: 'flex-start',
    margin: 0 ,
  },
  modalContent: {
    backgroundColor: '#FFFFFF',
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    marginTop: 40,
    height: 800,
  },
  body: {
  },
  label: {
    fontSize: 30,
    fontWeight: 700,
    marginVertical: 16,
    marginHorizontal: 32,
  },
  content: {
    alignItems: 'center',
    marginVertical: 20,
  },
  check_box: {
    flexDirection: 'row',
  },
  check: {
    flexDirection: 'row',
    marginBottom: 30,
  },
  check_label: {
    fontSize: 20,
    fontWeight: 600,
    marginRight: 20,
  },
  check_checkbox: {

  },
  element: {
    width: 300,
    height: 80,
    marginVertical: 10,
  },
  elementBox: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  element_label: {
    fontSize: 19,
    fontWeight: 500,
    marginHorizontal: 5,
  },
  element_label_active: {
    color: '#000000',
  },
  element_label_inactive: {
    color: '#AAAAAA',
  },
  sliderBox: {
    marginHorizontal: 50,
    marginTop: 10,
    height: 30,
  },
  slider: {
    width: '100%',
    height: 30,
    transform: [{ scale: 1.5 }]
  },

  checkbox: {

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
    fontWeight: 600,
    color: '#FFFFFF'
  },
});

import React, { useState, useEffect } from 'react';
import { View, Text, Pressable, StyleSheet } from 'react-native';
import Modal from 'react-native-modal';
import MultiSlider from '@ptomasroos/react-native-multi-slider';
import CheckBox from '@react-native-community/checkbox';

type SlideModalProps = {
  isVisible: boolean;
  onClose: () => void;
  initialRange: [number, number];
  onSave: (range: [number, number]) => void;
  children?: React.ReactNode;
};
  

interface LabelProps {
  oneMarkerValue: string | number;
  twoMarkerValue: string | number;
  oneMarkerLeftPosition: number;
  twoMarkerLeftPosition: number;
}

const dayInfo = [
  {
    key: 0,
    data: '일',
  }, {
    key: 1,
    data: '월',
  }, {
    key: 2,
    data: '화',
  }, {
    key: 3,
    data: '수',
  }, {
    key: 4,
    data: '목',
  }, {
    key: 5,
    data: '금',
  }, {
    key: 6,
    data: '토',
  }, 
]

export default function SlideModal_schedule({ isVisible, initialRange, onSave, onClose, children }: SlideModalProps) {
  const [activeDay, setActiveDay] = useState(0);
  const [range, setRange] = useState<number[][]>([[8*60, 18*60], [8*60, 18*60], [8*60, 18*60], [8*60, 18*60], [8*60, 18*60], [8*60, 18*60], [8*60, 18*60], [8*60, 18*60]]);
  const [activeSchedule, setActiveSchedule] = useState<boolean[]>([true, true, true, true, true, true, true]);
  const updateSchedule = (idx: number) => {
    setActiveSchedule(prev =>
    prev.map((item, i) => i === idx ? !item : item)
  );};
  const updateRange = (val_1: number, val_2: number, idx: number) => {
    const newRange = [val_1, val_2];
    setRange(prev =>
    prev.map((item, i) => i === idx ? newRange : item)
  );};

  useEffect(() => {
    setRange(prev => {
      const newRange = [...prev];
      newRange[6] = initialRange;
      return newRange;
    });
  }, [initialRange]);

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
            스케줄 수정
          </Text>
          <View style={styles.content}>
            <View style={styles.day_selectBox}>
              {dayInfo.map((item, i) => (
                <Pressable
                  key={item.key}
                  style={[styles.day_select, activeDay === item.key ? styles.day_select_active :styles. day_select_inactive]}
                  android_ripple={{
                    color: 'rgba(0,0,0,0.1)',
                    radius: 17.5,
                  }}
                  onPress={() => setActiveDay(item.key)}
                >
                  <Text style={[styles.day_label, activeDay === item.key ? styles.day_label_active :styles. day_label_inactive]}>
                    {item.data}
                  </Text>
                </Pressable>
              ))}
            </View>
            <View style={styles.schedule_content}>
              <Pressable
                style={styles.dayWeekBox}
                onPress={() => updateSchedule(activeDay)}
              >
                <CheckBox
                  value={activeSchedule[activeDay]}
                  onValueChange={() => updateSchedule(activeDay)}
                  tintColors={{ true: '#01B3EA', false: '#777' }}
                  style={styles.checkbox}
                />
                <Text style={styles.dayWeek}>
                  {dayInfo[activeDay].data}요일
                </Text>
              </Pressable>
              {
                activeSchedule[activeDay]?
                  <MultiSlider
                    values={range[activeDay]}
                    sliderLength={270}
                    onValuesChange={vals => updateRange(vals[0], vals[1], activeDay)}
                    min={0}
                    max={60*24}
                    step={5}
                    enableLabel
                    containerStyle={styles.sliderContainer}
                    customLabel={(props: LabelProps) => (
                      <View style={styles.labelContainer}>
                        <View
                          style={[
                            styles.labelBox_one,
                            { left: props.oneMarkerLeftPosition - 120 / 2 },
                          ]}
                        >
                          <Text
                            style={styles.labelText}
                          >
                            {Number(props.oneMarkerValue)<12*60?'오전':'오후'} {Math.floor(Number(props.oneMarkerValue)/60)%12}시 {Number(props.oneMarkerValue)%60}분
                          </Text>
                        </View>

                        <View
                          style={[
                            styles.labelBox_tow,
                            { left: props.twoMarkerLeftPosition - 120 / 2 },
                          ]}
                        >
                          <Text
                            style={styles.labelText}
                          >
                            {Number(props.twoMarkerValue)<12*60?'오전':'오후'} {Math.floor(Number(props.twoMarkerValue)/60)%12}시 {Number(props.twoMarkerValue)%60}분
                          </Text>
                        </View>
                      </View>
                    )}
                    selectedStyle={{ backgroundColor: '#01B3EA' }}
                    unselectedStyle={{ backgroundColor: '#AAAAAA' }}
                    trackStyle={{ height: 5, borderRadius: 3 }}

                    markerStyle={{
                      height: 20,
                      width: 20,
                      borderRadius: 12,
                      backgroundColor: '#01B3EA',
                    }}
                    markerOffsetY={2.5}

                    touchDimensions={{
                      height: 40,
                      width: 40,
                      borderRadius: 20,
                      slipDisplacement: 40,
                    }}
                  />
                :<></>
              }
              
            </View>
            
            <Pressable
              style={styles.confirmButton}
              android_ripple={{
                color: 'rgba(0,0,0,0.1)',
                radius: 40,
              }}
              onPress={() => {onSave(range[5] as [number, number]); onClose();}}
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
  },

  day_selectBox: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginVertical: 20,
  },
  day_select: {
    width: 35,
    height: 35,
    borderRadius: 100,
    elevation: 4,
    marginHorizontal: 5,
    alignItems: 'center',
    justifyContent: 'center',
  },
  day_select_active: {
    backgroundColor: '#01B3EA'
  },
  day_select_inactive: {
    backgroundColor: '#D9D9D9'
  },
  day_label: {
    fontSize: 14,
    fontWeight: 600,
  },
  day_label_active: {
    color: '#FFFFFF'
  },
  day_label_inactive: {
    color: '#000000'
  },

  schedule_content: {
    marginVertical: 10,
    marginHorizontal: '10%',
    width: 300,
    height: 100,
  },
  dayWeekBox: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 20,
  },
  dayWeek: {
    fontSize: 25,
    fontWeight: 500,
  },
  checkbox: {
    marginTop: 5,
  },
  sliderContainer: {
    marginVertical: 25,
    marginHorizontal: 15,
  },
  labelContainer: {
    marginHorizontal: 15,
  },
  labelBox_one: {
    position: 'absolute',
    top: 0,
    width: 120,
    backgroundColor: '#DDDDDD',
    paddingHorizontal: 4,
    paddingVertical: 4,
    borderRadius: 10,
    elevation: 4,
    alignItems: 'center',
  },
  labelBox_tow: {
    position: 'absolute',
    top: 65,
    width: 120,
    backgroundColor: '#DDDDDD',
    paddingHorizontal: 4,
    paddingVertical: 4,
    borderRadius: 10,
    elevation: 4,
    alignItems: 'center',
  },
  labelText: {
    fontSize: 16,
    fontWeight: 500,
    color: '#000000',
    overflow: 'hidden',
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
    marginTop: 60,
  },
  confirmButton_label: {
    fontSize: 20,
    fontWeight: 600,
    color: '#FFFFFF'
  },
});

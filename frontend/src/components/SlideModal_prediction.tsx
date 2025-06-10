import React, { useState, useEffect } from 'react';
import { View, Text, Pressable, StyleSheet, Image, ScrollView } from 'react-native';
import Modal from 'react-native-modal';
import axios from 'axios';
import { Graph } from '../components/Graph'



type SlideModalProps = {
  isVisible: boolean;
  onClose: () => void;
  children?: React.ReactNode;
};

type DataPoint = { x: number; y: number };

type AirQualityData = {
  pm25: DataPoint[];
  pm100: DataPoint[];
  co2: DataPoint[];
  voc: DataPoint[];
};

  
const info_data = [
  {
    key: 'PM2.5',
    title: '초미세먼지',
    data: '미세먼지는 지름 10μm 이하의 입자상 물질로, 도로 주행, 산업 공정, 황사 등 다양한 원인에서 배출되는 1차 오염물질입니다. 지속적으로 흡입할 경우 기도 내 점액 분비를 촉진하여 호흡 곤란을 유발하고, 만성 호흡기·심혈관 질환의 발병 위험을 높일 수 있습니다.',
    unit: 'µg/m³',
    threshold: ['0', '5', '15', '25', '35-'],
    state: 1,
  }, 
  {
    key: 'PM10',
    title: '미세먼지',
    data: '초미세먼지는 지름 2.5μm 이하의 작은 입자로, 화석연료 연소나 발전소 배출 가스가 응결·응집하여 형성되는 2차 오염물질입니다. 입자 크기가 작아 폐포와 혈류까지 침투해 염증 반응을 일으키며, 심·뇌혈관계 질환을 악화시키고 면역 기능을 저하시킬 수 있습니다.',
    unit: 'µg/m³',
    threshold: ['0', '15', '30', '50', '70-'],
    state: 1,
  }, 
  {
    key: 'CO2',
    title: '이산화탄소',
    data:'이산화탄소는 화석연료 연소와 생물의 호흡 과정에서 발생하는 주요 온실가스로, 대기 중 농도가 상승할수록 지구 온난화를 가속화합니다. 밀폐된 실내 환경에서 과도하게 축적되면 두통·집중력 저하·졸음 등을 유발해 작업 능률과 쾌적도를 떨어뜨릴 수 있습니다.',
    unit: 'ppm',
    threshold: ['0', '400', '1000', '2000', '3000-'],
    state: 4,
  }, 
  {
    key: 'VOC',
    title: '휘발성유기화합물',
    data:'휘발성유기화합물은 페인트·접착제·세정제 등에서 쉽게 기체로 전환되는 탄화수소 계열의 화합물군입니다. 단기간 고농도로 노출되면 눈·코·목의 자극과 두통을 일으키며, 장기간 반복 노출 시 중추신경계 손상과 천식·알레르기 같은 호흡기 질환 위험을 증가시킬 수 있습니다.',
    unit: 'ppm',
    threshold: ['0', '100', '400', '1000', '2000-'],
    state: 2,
  }
]

const room_list = [
  {
    roomId: 1,
    roomName: '거실',
  },
  {
    roomId: 2,
    roomName: '방1',
  },
  {
    roomId: 3,
    roomName: '방3',
  },
]

export default function SlideModal_prediction({ isVisible, onClose, children }: SlideModalProps) {
  const [activeIndex, setActiveIndex] = useState(0);
  const [currentGraphData, setCurrentGraphData] = useState<AirQualityData[]>(
    Array(4).fill(null).map(() => ({
      pm25: [],
      pm100: [],
      co2: [],
      voc: [],
    }))
  );
  const [lastRefreshed, setLastRefreshed] = useState<string[]>(
    ["00:00 AM", "00:00 AM", "00:00 AM", "00:00 AM"]
  );

  const updateGraphData = (index: number, newData: AirQualityData) => {
    setCurrentGraphData(prev => {
      const newList = [...prev];
      newList[index] = newData;
      return newList;
    });
  };

  const transformToGraphData = (responseData: any): AirQualityData => {
    return {
      pm25: responseData.pm25_t.map((y: number, x: number) => ({ x: x + 1, y: Math.max(Math.min(y, 49), 1) })),
      pm100: responseData.pm100_t.map((y: number, x: number) => ({ x: x + 1, y: Math.max(Math.min(y, 99), 1) })),
      co2: responseData.co2.map((y: number, x: number) => ({ x: x + 1, y: Math.max(Math.min(y, 3999), 1) })),
      voc: responseData.voc.map((y: number, x: number) => ({ x: x + 1, y: Math.max(Math.min(y, 2999), 1) })),
    };
  };

  const API_BASE = 'http://18.191.176.79:8080';

  const fetchGraphData = async (i: number) => {
    try {
      const response = await axios.get(`${API_BASE}/ai/predictions/latest`);
      const formattedData = transformToGraphData(response.data.predictions[i]);
      updateGraphData(i, formattedData);
      
    } catch (e) {
    console.error('에러:', e);
    }
  };
  
  const updateLastRefreshed = (index: number, newTime: string) => {
    setLastRefreshed(prev => {
      const updated = [...prev]; // 기존 배열 복사
      updated[index] = newTime;  // 해당 인덱스 값 수정
      return updated;
    });
  };

  const handleRefresh = (i: number) => {
    const now = new Date();
    let hours = now.getHours();
    const minutes = now.getMinutes();
    const ampm = hours >= 12 ? 'PM' : 'AM';

    hours = hours % 12;
    if (hours === 0) hours = 12;

    const formatted = `${hours.toString().padStart(2, '0')}:${minutes
      .toString()
      .padStart(2, '0')} ${ampm}`;

    updateLastRefreshed(i, formatted);
  };
  
  useEffect(() => {
      fetchGraphData(0);
      fetchGraphData(1);
      fetchGraphData(2);
      fetchGraphData(3);
      handleRefresh(0);
      handleRefresh(1);
      handleRefresh(2);
      handleRefresh(3);
    }, []);

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
            방별 예측 정보
          </Text>
          <View style={styles.content}>
            <View  style={styles.indexBox}>
              {info_data.map((item, i) => (
                <Pressable
                  key={i}
                  style={[styles.forecast_index, activeIndex === i ? styles.index_active : styles.index_inactive]}
                  onPress={() => setActiveIndex(i)}
                >
                  <Text style={styles.index_label_base}>
                    {item.key}
                  </Text>
                </Pressable>
              ))}
            </View>
            <ScrollView style={styles.list} contentContainerStyle={styles.listContent}>
              {room_list.map((room, i) => (
                <View style={styles.forecast} key={room.roomId}>
                  <View style={styles.content_labelBox}>
                    <Text style={styles.content_label}>
                      예측 정보
                    </Text>
                    <Text style={styles.content_label_room}>
                      {room.roomName}
                    </Text>
                    <Text style={styles.forecast_label_time}>
                      {lastRefreshed[i]}
                    </Text>
                    <Pressable
                      style={styles.forecast_label_refresh}
                      onPress={() => {fetchGraphData(i); handleRefresh(i);}}
                    >
                      <Image
                        source={require('../assets/refresh.png')}
                        style={styles.forecast_label_refresh_image}
                      />
                    </Pressable>
                  </View>
                  
                  <View style={styles.chart}>
                    {(() => {
                      switch (activeIndex) {
                        case 0:
                          return <Graph data={currentGraphData[i === 2 ? 3 : i]['pm25']} type='pm25'></Graph>;
                        case 1:
                          return <Graph data={currentGraphData[i === 2 ? 3 : i]['pm100']} type='pm100'></Graph>;
                        case 2:
                          return <Graph data={currentGraphData[i === 2 ? 3 : i]['co2']} type='co2'></Graph>;
                        case 3:
                          return <Graph data={currentGraphData[i === 2 ? 3 : i]['voc']} type='voc'></Graph>;
                        default:
                          return null;
                      }
                    })()}
                  </View>
                </View>
              ))}
            </ScrollView>
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
  list: {
    width: '100%',
  },
  listContent: {
    alignItems: 'center',
    paddingBottom: 280,
  },

  content_labelBox: {
    height: 34,
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: '2%',
  },
  content_label: {
    fontSize: 20,
    fontWeight: 700,
    marginLeft: '6%',
  },
  content_label_room: {
    color: '#777777',
    fontSize: 17,
    fontWeight: 500,
    marginLeft: '2%',
    width: 120,
  },

  forecast: {
    backgroundColor: '#F5F9FD',
    width: '90%',
    height: 180,
    borderRadius: 15,
    marginVertical: 7,
    elevation: 4,
  },
  forecast_label_time: {
    color: '#777777',
    fontSize: 14,
    fontWeight: 500,
  },
  forecast_label_refresh: {
    width: 30,
    height: 30,
    justifyContent: 'center',
    alignItems: 'center',
  },
  forecast_label_refresh_image: {
    width: 15,
    height: 15,
  },
  chart: {
    alignSelf: 'center',
    width: 300,
    height: 125,
    marginBottom: 10,
  },

  indexBox: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  forecast_index: {
    width: 70,
    height: 30,
    backgroundColor: '#DFDFDF',
    justifyContent: 'center',
    borderRadius: 100,
    borderColor: '#DFDFDF',
    borderWidth: 1,
    alignItems: 'center',
    marginHorizontal: '1%',
    marginVertical: '2%',
    elevation: 1,
  },
  index_active: {
    backgroundColor: '#FFFFFF',
  },
  index_inactive: {
    backgroundColor: '#DFDFDF',
  },
  index_label_base: {
    color: '#000000',
    fontSize: 15,
    fontWeight: 600,
    textAlign: 'center',
  },
});

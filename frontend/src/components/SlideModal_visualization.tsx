import React, { useState, useRef, useEffect } from 'react';
import { View, Text, StyleSheet, PanResponder, GestureResponderEvent, PanResponderGestureState } from 'react-native';
import { WebView } from 'react-native-webview';
import Modal from 'react-native-modal';
import Slider from '@react-native-community/slider';
import LinearGradient from 'react-native-linear-gradient';
import axios from 'axios';

type heatmapPoint = { x: number; y: number; value: number };

type Room = {
    key: number;
    name: string;
    positionx: number;
    positiony: number;
    width: number;
    height: number;
  };
type RoomList = Room[];

type SensorMeta = {
  sensorId: number;
  roomId: number;
  xcoordinate: number;
  ycoordinate: number;
};

type SensorData = {
  sensorId: number;
  pm25: number;
  pm100: number;
  co2: number;
  voc: number;
};

type SlideModalProps = {
  isVisible: boolean;
  onClose: () => void;
  children?: React.ReactNode;
};
  
type Point = { x: number; y: number; value: number };

function makeHeatmapHTML(points: Point[], max = 100) {
  return `
    <!DOCTYPE html>
    <html>
      <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <style>
          html, body, #heatmapContainer {
            margin:0; padding:0;
            width:100%; height:100%;
            background:transparent;
          }
        </style>
        <script src="https://cdn.jsdelivr.net/npm/heatmapjs@2.0.2/heatmap.min.js"></script>
      </head>
      <body>
        <div id="heatmapContainer"></div>
        <script>
          const data = ${JSON.stringify({ max, data: points })};
          const hm = h337.create({
            container: document.getElementById('heatmapContainer'),
            radius: 80,
            maxOpacity: 0.8,
            minOpacity: 0,
            blur: 0.75,
            gradient: { 0.2:'blue', 0.5:'yellow', 0.8:'red' }
          });
          hm.setData(data);
        </script>
      </body>
    </html>
  `;
}

const info_data = [
  {
    id: 'pm25',
    key: 'PM2.5',
    title: '초미세먼지',
    data: '미세먼지는 지름 10μm 이하의 입자상 물질로, 도로 주행, 산업 공정, 황사 등 다양한 원인에서 배출되는 1차 오염물질입니다. 지속적으로 흡입할 경우 기도 내 점액 분비를 촉진하여 호흡 곤란을 유발하고, 만성 호흡기·심혈관 질환의 발병 위험을 높일 수 있습니다.',
    unit: 'µg/m³',
    threshold: [0, 5, 15, 25, 35],
  }, 
  {
    id: 'pm100',
    key: 'PM10',
    title: '미세먼지',
    data: '초미세먼지는 지름 2.5μm 이하의 작은 입자로, 화석연료 연소나 발전소 배출 가스가 응결·응집하여 형성되는 2차 오염물질입니다. 입자 크기가 작아 폐포와 혈류까지 침투해 염증 반응을 일으키며, 심·뇌혈관계 질환을 악화시키고 면역 기능을 저하시킬 수 있습니다.',
    unit: 'µg/m³',
    threshold: [0, 15, 30, 50, 70],
  }, 
  {
    id: 'co2',
    key: 'CO2',
    title: '이산화탄소',
    data:'이산화탄소는 화석연료 연소와 생물의 호흡 과정에서 발생하는 주요 온실가스로, 대기 중 농도가 상승할수록 지구 온난화를 가속화합니다. 밀폐된 실내 환경에서 과도하게 축적되면 두통·집중력 저하·졸음 등을 유발해 작업 능률과 쾌적도를 떨어뜨릴 수 있습니다.',
    unit: 'ppm',
    threshold: [0, 400, 1000, 2000, 3000],
  }, 
  {
    id: 'pm25',
    key: 'VOC',
    title: '휘발성유기화합물',
    data:'휘발성유기화합물은 페인트·접착제·세정제 등에서 쉽게 기체로 전환되는 탄화수소 계열의 화합물군입니다. 단기간 고농도로 노출되면 눈·코·목의 자극과 두통을 일으키며, 장기간 반복 노출 시 중추신경계 손상과 천식·알레르기 같은 호흡기 질환 위험을 증가시킬 수 있습니다.',
    unit: 'ppm',
    threshold: [0, 100, 400, 1000, 2000],
  }
]

export default function SlideModal_login({ isVisible, onClose, children }: SlideModalProps) {
  const [time, setTime] = useState(24);
  const [touchX, setTouchX] = useState(150);
  const [touchY, setTouchY] = useState(150);

  const [currentRoom, setCurrentRoom] = useState<RoomList>([]);
  const [currentRoomPoint, setCurrentRoomPoint] = useState<heatmapPoint[][]>([]);
  const [sensorMetaList, setSensorMetaList] = useState<SensorMeta[]>([]);
  const [sensorDataList, setSensorDataList] = useState<SensorData[]>([]);

  const [currentData, setCurrentData] = useState({
    pm25: 0,
    pm100: 0,
    co2: 0,
    voc: 0,
  });

  const API_BASE = 'http://18.191.176.79:8080';

  const fetchAvgData = async () => {
    const endTime = Date.now() - (24 - time) * 60 * 60 * 1000;
    const startTime = endTime - 3 * 60 * 1000;
    const sensorID = [48007, 44212, 44213];
    const data = [0, 0, 0, 0];
    try {
      await Promise.all(
        sensorID.map(id => 
          axios.get(`${API_BASE}/measurements/sensor/${id}?startTime=${startTime}&endTime=${endTime}`)
          .then(response => {
            data[0] += response.data[response.data.length - 1].pm25_t;
            data[1] += response.data[response.data.length - 1].pm100_t;
            data[2] += response.data[response.data.length - 1].co2;
            data[3] += response.data[response.data.length - 1].voc;
          })
        )
      );
      setCurrentData({
        pm25: Math.round(data[0] / data.length),
        pm100: Math.round(data[1] / data.length),
        co2: Math.round(data[2] / data.length),
        voc: Math.round(data[3] / data.length),
    })
    } catch (e) {
    console.error('에러:', e);
    }
  };

  const fetchAllSensorData = async () => {
  const endTime = Date.now() - (24 - time) * 60  * 60 * 1000;
  const startTime = endTime - 3 * 60 * 1000;
  const sensorIDs = [48007, 44212, 44213];

  try {
    const responses = await Promise.all(
      sensorIDs.map(id =>
        axios.get(`${API_BASE}/measurements/sensor/${id}?startTime=${startTime}&endTime=${endTime}`)
          .then(response => {
            const latest = response.data[response.data.length - 1];
            return {
              sensorId: id,
              pm25: latest.pm25_t,
              pm100: latest.pm100_t,
              co2: latest.co2,
              voc: latest.voc,
            };
          })
      )
    );

    setSensorDataList(responses); // 센서별 데이터 배열 저장
  } catch (e) {
    console.error('센서 데이터 불러오기 실패:', e);
  }
};

  const fetchSensorMetaList = async () => {
    try {
      const response = await axios.get(`${API_BASE}/sensors`);
      const data = response.data.map((item: any) => ({
        sensorId: item.sensorId,
        roomId: item.roomId,
        xcoordinate: item.xcoordinate * 3,
        ycoordinate: item.ycoordinate * 3,
      }));
      setSensorMetaList(data);
    } catch (e) {
      console.error('센서 메타데이터 가져오기 실패:', e);
    }
  };
  
  const buildHeatmapPoints = (
    sensorDataList: SensorData[],
    sensorMetaList: SensorMeta[],
    valueKey: keyof SensorData
  ) => {
    const roomPointsMap = new Map<number, heatmapPoint[]>();

    sensorMetaList.forEach(meta => {
      const matchingData = sensorDataList.find(data => data.sensorId === meta.sensorId);
      if (!matchingData) return;

      const value = matchingData[valueKey];
      const point: heatmapPoint = {
        x: meta.xcoordinate,
        y: meta.ycoordinate,
        value,
      };

      if (!roomPointsMap.has(meta.roomId)) {
        roomPointsMap.set(meta.roomId, [point]);
      } else {
        roomPointsMap.get(meta.roomId)!.push(point);
      }
    });

    const sortedRoomPoints = [...roomPointsMap.entries()]
      .sort((a, b) => a[0] - b[0])
      .map(([_, points]) => points);

    setCurrentRoomPoint(sortedRoomPoints);
  };

  const fetchRooms = async () => {
    try {
      const id = 1;
      const response = await axios.get(`${API_BASE}/homes/1/rooms`);
      const rawRooms = response.data;

      const parsedRooms: Room[] = rawRooms.map((room: any) => {
        const polygon: [number, number][] = JSON.parse(room.polygon);
        const xs = polygon.map(p => p[0]);
        const ys = polygon.map(p => p[1]);

        const minX = Math.min(...xs);
        const maxX = Math.max(...xs);
        const minY = Math.min(...ys);
        const maxY = Math.max(...ys);

        return {
          key: room.roomId,
          name: room.roomName,
          positionx: minX,
          positiony: minY,
          width: maxX - minX,
          height: maxY - minY,
        };
      });

      setCurrentRoom(parsedRooms);
    } catch (e) {
      console.error('방 목록 불러오기 실패:', e);
    }
  };

  useEffect(() => {
    fetchAvgData();
    fetchRooms();
    fetchAllSensorData();
    fetchSensorMetaList();
  }, []);

  useEffect(() => {
    if (sensorDataList.length && sensorMetaList.length) {
      buildHeatmapPoints(sensorDataList, sensorMetaList, 'pm25');
    }
  }, [sensorDataList, sensorMetaList]);

  useEffect(() => {
    fetchAvgData();
    fetchAllSensorData();
  }, [time]);
  

  const panResponder = useRef(
    PanResponder.create({
      onStartShouldSetPanResponder: () => true,
      onMoveShouldSetPanResponder: () => true,

      // 터치 시작
      onPanResponderGrant: (e: GestureResponderEvent) => {
        const { pageX, pageY } = e.nativeEvent;
        setTouchX(Math.min(Math.max(Math.round(pageX - 31), 0), 300));
        setTouchY(Math.min(Math.max(Math.round(pageY - 123), 0), 300));
      },
      // 터치 이동
      onPanResponderMove: (e: GestureResponderEvent, gesture: PanResponderGestureState) => {
        const { pageX, pageY } = e.nativeEvent;
        setTouchX(Math.min(Math.max(Math.round(pageX - 31), 0), 300));
        setTouchY(Math.min(Math.max(Math.round(pageY - 123), 0), 300));
      },
      // 터치 종료
      onPanResponderRelease: (e: GestureResponderEvent, gesture: PanResponderGestureState) => {
        const { pageX, pageY } = e.nativeEvent;
        setTouchX(Math.min(Math.max(Math.round(pageX - 31), 0), 300));
        setTouchY(Math.min(Math.max(Math.round(pageY - 123), 0), 300));
      },
      onPanResponderTerminate: () => {
      }
    })
  ).current;

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
            공기질 시각화
          </Text>
          <View style={styles.content}>
            <View 
              style={[styles.visualization_content, {backgroundColor: '#FFFFFF'}]}
              {...panResponder.panHandlers}
            >
              {currentRoom.map((item, i) => {
                const pointsForRoom = currentRoomPoint[i===0 ? 0 : i === 1 ? 1 : i === 2 ? 4 : i === 3 ? 2 : 4];
                const roomHtml = makeHeatmapHTML(pointsForRoom, 100);
                return (
                  <View 
                    key={item.key}
                    style={{
                      position: 'absolute', 
                      left: item.positionx * 3, 
                      top: item.positiony * 3,
                      width: item.width * 3,
                      height: item.height * 3,
                    }}
                  >
                    <View style={{ flex:1, borderWidth:1 }}>
                      <WebView
                        originWhitelist={['*']}
                        source={{ html: roomHtml }}
                        style={{ flex: 1, backgroundColor: '#FFFFFF' }}
                        overScrollMode="never"
                      />
                      <Text style={styles.room_label}>{item.name}</Text>
                    </View>
                  </View>
                );
              })}
            </View>
            <Text style={styles.slider_label}>
              {24 - time}시간 전
            </Text>
            <Slider
              style={styles.slider}
              minimumValue={0}
              maximumValue={24}
              step={1}
              value={time}
              onValueChange={setTime}
              minimumTrackTintColor="#01B3EA"
              maximumTrackTintColor="#777777"
              thumbTintColor="#01B3EA"
            />
            {info_data.map((item, i) => (
              <View key={item.key} style={styles.detail_container}>
                <Text style={styles.detail_label}>
                  {item.key}: {currentData[item.id as keyof typeof currentData]}{item.unit}
                </Text>
                <LinearGradient
                  colors={['#83FB80', '#D0FBD5', '#F0F0F0', '#F7DEDC', '#F7A499']}
                  start={{ x: 0, y: 0 }}
                  end={{ x: 1, y: 0 }}
                  style={styles.detail_back}
                />
                <View style={[styles.detail_value, {left: 250*(currentData[item.id as keyof typeof currentData])/item.threshold[4] }]}></View>
              </View>
            ))}
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
  
    visualization_content:{
    alignSelf: 'center',
    height: 301,
    width: 300,
    marginTop: 10,
    marginBottom: 10,
    borderWidth: 1,
  },
  room_label: {
    position: 'absolute',
    width: '100%',
    height: '100%',
    textAlign: 'center',
    textAlignVertical: 'center',
    fontSize: 18,
    fontWeight: 600,
  },

  point: {
    position: 'absolute',
    width: 20,
    height: 20,
    borderRadius: 100,
    backgroundColor: '#FFFFFF',
    borderColor: '#444444',
    borderWidth: 1,
  },

  slider: {
    width: '63%',
    height: 30,
    transform: [{ scale: 1.5 }]
  },
  slider_label: {
    fontSize: 18,
    fontWeight: 600,
  },

  detail_container: {
    marginVertical: 5,
  },
  detail_label: {
    fontSize: 18,
    fontWeight: 500,
    marginVertical: 5,
  },
  detail_back: {
    width: 270,
    height: 16,
    borderRadius: 100,
  },
  detail_value: {
    position: 'absolute',
    top: 33,
    width: 20,
    height: 20,
    borderRadius: 100,
    backgroundColor: '#FFFFFF',
    borderColor: '#777777',
    borderWidth: 1,
  }
});

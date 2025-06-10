import React, { useState, useEffect } from 'react';
import { View, Image, Pressable, Text, StyleSheet, ScrollView, ImageBackground } from 'react-native';
import { WebView } from 'react-native-webview';
import SelectDropdown from 'react-native-select-dropdown';
import SlideModal_prediction from '../components/SlideModal_prediction';
import SlideModal_visualization from '../components/SlideModal_visualization';
import SlideModal_schedule from '../components/SlideModal_schedule';
import SlideModal_userThreshold from '../components/SlideModal_userThreshold';
import SlideModal_roomThreshold from '../components/SlideModal_roomThreshold';
import SlideModal_edit from '../components/SlideModal_edit';
import CheckBox from '@react-native-community/checkbox';
import MultiSlider from '@ptomasroos/react-native-multi-slider';
import axios from 'axios';

import { Graph } from '../components/Graph'



type heatmapPoint = { x: number; y: number; value: number };

type DataPoint = { x: number; y: number };

type AirQualityData = {
  pm25: DataPoint[];
  pm100: DataPoint[];
  co2: DataPoint[];
  voc: DataPoint[];
};

type ThresholdKey = 'pm25' | 'pm100' | 'co2' | 'voc';

type UserThreshold = {
  [key in ThresholdKey]: number;
};

type RoomThreshold = {
  pm25: number;
  pm100: number;
  co2: number;
  voc: number;
};

type ThresholdList = RoomThreshold[];

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


function makeHeatmapHTML(points: heatmapPoint[], max = 100) {
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
            radius: 100,
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

const DISTRICTS = [
  '우리 집1', '우리 집2'
];
const ROOMS = [
  '거실', '방1', '방2', '방3', '주방'
]
const info_data = [
  {
    id: 'pm25',
    key: 'PM2.5',
    title: '초미세먼지',
    data: '미세먼지는 지름 10μm 이하의 입자상 물질로, 도로 주행, 산업 공정, 황사 등 다양한 원인에서 배출되는 1차 오염물질입니다. 지속적으로 흡입할 경우 기도 내 점액 분비를 촉진하여 호흡 곤란을 유발하고, 만성 호흡기·심혈관 질환의 발병 위험을 높일 수 있습니다.',
    unit: 'µg/m³',
    threshold: [0, 5, 15, 25, 35],
    value: 10,
  }, 
  {
    id: 'pm100',
    key: 'PM10',
    title: '미세먼지',
    data: '초미세먼지는 지름 2.5μm 이하의 작은 입자로, 화석연료 연소나 발전소 배출 가스가 응결·응집하여 형성되는 2차 오염물질입니다. 입자 크기가 작아 폐포와 혈류까지 침투해 염증 반응을 일으키며, 심·뇌혈관계 질환을 악화시키고 면역 기능을 저하시킬 수 있습니다.',
    unit: 'µg/m³',
    threshold: [0, 15, 30, 50, 70],
    value: 25,
  }, 
  {
    id: 'co2',
    key: 'CO2',
    title: '이산화탄소',
    data:'이산화탄소는 화석연료 연소와 생물의 호흡 과정에서 발생하는 주요 온실가스로, 대기 중 농도가 상승할수록 지구 온난화를 가속화합니다. 밀폐된 실내 환경에서 과도하게 축적되면 두통·집중력 저하·졸음 등을 유발해 작업 능률과 쾌적도를 떨어뜨릴 수 있습니다.',
    unit: 'ppm',
    threshold: [0, 400, 1000, 2000, 3000],
    value: 1000,
  }, 
  {
    id: 'voc',
    key: 'VOC',
    title: '휘발성유기화합물',
    data:'휘발성유기화합물은 페인트·접착제·세정제 등에서 쉽게 기체로 전환되는 탄화수소 계열의 화합물군입니다. 단기간 고농도로 노출되면 눈·코·목의 자극과 두통을 일으키며, 장기간 반복 노출 시 중추신경계 손상과 천식·알레르기 같은 호흡기 질환 위험을 증가시킬 수 있습니다.',
    unit: 'ppm',
    threshold: [0, 100, 400, 1000, 2000],
    value: 400,
  }
]
interface device_data {
    key: string;
    type: string;
    name: string;
    location: string;
    state: number;
    filter: number;
}
const initial_device_data = [
  {
    key: '1',
    type: '공기청정기',
    name: '공기청정기1',
    location: '방1',
    state: 1,
    filter: 98,
  },
]

interface LabelProps {
  oneMarkerValue: string | number;
  twoMarkerValue: string | number;
  oneMarkerLeftPosition: number;
  twoMarkerLeftPosition: number;
}

export const IndoorScreen = () => {
  const [selectedDistrict, setSelectedDistrict] = useState<string>(DISTRICTS[0]);
  const [selectedRoom, setSelectedRoom] = useState<string>(ROOMS[0]);
  const [modalVisible_prediction, setModalVisible_prediction] = useState(false);
  const [modalVisible_visualization, setModalVisible_visualization] = useState(false);
  const [modalVisible_schedule, setModalVisible_schedule] = useState(false);
  const [modalVisible_userThreshold, setModalVisible_userThreshold] = useState(false);
  const [modalVisible_roomThreshold, setModalVisible_roomThreshold] = useState(false);
  const [modalVisible_edit, setModalVisible_edit] = useState(false);
  const [dayChecked, setdayChecked] = useState(true);
  const [checked, setChecked] = useState<boolean[]>([true, true, true, true]);
  const [range, setRange] = useState<[number,number]>([8*60, 18*60]);
  const [currentData, setCurrentData] = useState({
    pm25: 0,
    pm100: 0,
    co2: 0,
    voc: 0,
  });
  const [currentAQI, setCurrentAQI] = useState(0);
  const [currentGraphData, setcurrentGraphData] = useState<AirQualityData>({
    pm25: [],
    pm100: [],
    co2: [],
    voc: [],
  });

  const [currentUserThreshold, setCurrentUserThreshold] = useState<UserThreshold>({
    pm25: 0,
    pm100: 0,
    co2: 0,
    voc: 0,
  });

  const [currentRoomThreshold, setCurrentRoomThreshold] = useState<ThresholdList>(
    Array(4).fill(null).map(() => ({
      pm25: 0,
      pm100: 0,
      co2: 0,
      voc: 0,
    }))
  );
  const [currentRoom, setCurrentRoom] = useState<RoomList>([]);
  const [currentRoomPoint, setCurrentRoomPoint] = useState<heatmapPoint[][]>([]);
  const [sensorMetaList, setSensorMetaList] = useState<SensorMeta[]>([]);
  const [sensorDataList, setSensorDataList] = useState<SensorData[]>([]);

  const aqi = currentAQI;
  const min = 0;
  const max = 300;
  const markerRadius = 8;
  const centerX = 70.5;
  const centerY = 71;
  const r = 62.7;
  const startAngle = (-210 * Math.PI) / 180;
  const endAngle   = (30 * Math.PI) / 180;
  const t = Math.max(0, Math.min(1, (aqi - min) / (max - min)));
  const theta = startAngle + t * (endAngle - startAngle);
  const markerX = centerX + r * Math.cos(theta);
  const markerY = centerY + r * Math.sin(theta);

  const [activeInfo, setActiveInfo] = useState(0);
  const [activePrediction, setActivePrediction] = useState(0);
  const [isAuto, setIsAuto] = useState(true);

  const activeItem = info_data[activeInfo];

  const [deviceState, setDeviceState] = useState<device_data[]>(initial_device_data);
  const [deviceManual, setDeviceManual] = useState(0);

  const handleDeviceControl = (modeIndex: number) => {
    setDeviceManual(modeIndex);

    const modeMap = ['sleep', 'windFree', 'medium', 'max'];
    const fanMode = modeMap[modeIndex] || 'medium';

    const payload = {
      commands: [
        {
          component: 'main',
          capability: 'switch',
          command: 'on',
          arguments: []
        },
        {
          component: 'main',
          capability: 'airConditionerFanMode',
          command: 'setFanMode',
          arguments: [fanMode]
        }
      ]
    };

    axios.post(
      'http://18.191.176.79:8080/api/smartthings/control/2de501b2-c15c-e647-319c-e8c5fb4a0c70',
      payload,
      {
        headers: {
          'Content-Type': 'application/json'
        }
      }
    )
    .then(() => {
      console.log('전송 완료:', fanMode);
    })
    .catch((error) => {
      console.error('전송 실패:', error);
    });
  };

  function calculateAQI(pm25: number, pm100: number): number {
  // EPA 기준 AQI 계산을 위한 브레이크포인트
  const pm25Breakpoints = [
    { concLow: 0.0, concHigh: 12.0, aqiLow: 0, aqiHigh: 50 },
    { concLow: 12.1, concHigh: 35.4, aqiLow: 51, aqiHigh: 100 },
    { concLow: 35.5, concHigh: 55.4, aqiLow: 101, aqiHigh: 150 },
    { concLow: 55.5, concHigh: 150.4, aqiLow: 151, aqiHigh: 200 },
    { concLow: 150.5, concHigh: 250.4, aqiLow: 201, aqiHigh: 300 },
    { concLow: 250.5, concHigh: 350.4, aqiLow: 301, aqiHigh: 400 },
    { concLow: 350.5, concHigh: 500.4, aqiLow: 401, aqiHigh: 500 },
  ];

  const pm100Breakpoints = [
    { concLow: 0, concHigh: 54, aqiLow: 0, aqiHigh: 50 },
    { concLow: 55, concHigh: 154, aqiLow: 51, aqiHigh: 100 },
    { concLow: 155, concHigh: 254, aqiLow: 101, aqiHigh: 150 },
    { concLow: 255, concHigh: 354, aqiLow: 151, aqiHigh: 200 },
    { concLow: 355, concHigh: 424, aqiLow: 201, aqiHigh: 300 },
    { concLow: 425, concHigh: 504, aqiLow: 301, aqiHigh: 400 },
    { concLow: 505, concHigh: 604, aqiLow: 401, aqiHigh: 500 },
  ];

  function getAQI(value: number, breakpoints: typeof pm25Breakpoints): number {
    for (let i = 0; i < breakpoints.length; i++) {
      const bp = breakpoints[i];
      if (value >= bp.concLow && value <= bp.concHigh) {
        return Math.round(
          ((bp.aqiHigh - bp.aqiLow) / (bp.concHigh - bp.concLow)) *
            (value - bp.concLow) +
            bp.aqiLow
        );
      }
    }
    return -1; // 초과값
  }

  const aqiPm25 = getAQI(pm25, pm25Breakpoints);
  const aqiPm10 = getAQI(pm100, pm100Breakpoints);

  return Math.max(aqiPm25, aqiPm10);
}


  const API_BASE = 'http://18.191.176.79:8080';

  const fetchAvgData = async () => {
    const endTime = Date.now();
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
  const endTime = Date.now();
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

  function transformToGraphData(predictions: any[]): AirQualityData {
    const count = predictions.length;
    const length = predictions[0].pm25_t.length;

    const pm25Averages: number[] = Array(length).fill(0);
    const pm100Averages: number[] = Array(length).fill(0);
    const co2Averages: number[] = Array(length).fill(0);
    const vocAverages: number[] = Array(length).fill(0);

    // 누적합 계산
    predictions.forEach(pred => {
      pred.pm25_t.forEach((v: number, i: number) => {
        pm25Averages[i] += v;
      });
      pred.pm100_t.forEach((v: number, i: number) => {
        pm100Averages[i] += v;
      });
      pred.co2.forEach((v: number, i: number) => {
        co2Averages[i] += v;
      });
      pred.voc.forEach((v: number, i: number) => {
        vocAverages[i] += v;
      });
    });

    // 평균 내고 DataPoint[]로 변환
    const pm25: DataPoint[] = pm25Averages.map((sum, i) => ({
      x: i + 1,
      y: Math.max(Math.min(Math.round(sum / count), 49), 1),
    }));

    const pm100: DataPoint[] = pm100Averages.map((sum, i) => ({
      x: i + 1,
      y: Math.max(Math.min(Math.round(sum / count), 99), 1),
    }));

    const co2: DataPoint[] = co2Averages.map((sum, i) => ({
      x: i + 1,
      y: Math.max(Math.min(Math.round(sum / count), 3999), 1),
    }));

    const voc: DataPoint[] = vocAverages.map((sum, i) => ({
      x: i + 1,
      y: Math.max(Math.min(Math.round(sum / count), 2999), 1),
    }));

    return { pm25, pm100, co2, voc };
  }

  const fetchGraphData = async () => {
    try {
      const response = await axios.get(`${API_BASE}/ai/predictions/latest`);
      const predictions = response.data.predictions;

      const graphData = transformToGraphData(predictions);
      setcurrentGraphData(graphData);
      
    } catch (e) {
    console.error('에러:', e);
    }
  };

  const [lastRefreshed, setLastRefreshed] = useState<string | null>(null);
  
  const handleRefresh = () => {
    const now = new Date();
    let hours = now.getHours();
    const minutes = now.getMinutes();
    const ampm = hours >= 12 ? 'PM' : 'AM';

    hours = hours % 12;
    if (hours === 0) hours = 12;

    const formatted = `${hours.toString().padStart(2, '0')}:${minutes
      .toString()
      .padStart(2, '0')} ${ampm}`;

    setLastRefreshed(formatted);
  };

  const [lastRefreshed_visual, setLastRefreshed_visual] = useState<string | null>(null);
  
  const handleRefresh_visual = () => {
    const now = new Date();
    let hours = now.getHours();
    const minutes = now.getMinutes();
    const ampm = hours >= 12 ? 'PM' : 'AM';

    hours = hours % 12;
    if (hours === 0) hours = 12;

    const formatted = `${hours.toString().padStart(2, '0')}:${minutes
      .toString()
      .padStart(2, '0')} ${ampm}`;

    setLastRefreshed_visual(formatted);
  };

  const applyUserThresholdFromResponse = (data: any) => {
    setCurrentUserThreshold({
      pm25: data.pm25Threshold,
      pm100: data.pm100Threshold,
      co2: data.co2Threshold,
      voc: data.vocThreshold,
    });
  };

  const fetchUserThreshold = async () => {
    try {
      const id = 1;
      const response = await axios.get(`${API_BASE}/users/${id}/preferences`);
      applyUserThresholdFromResponse(response.data);
    } catch (e) {
      console.error('에러:', e);
    }
  };

  const applyRoomThresholdFromResponse = (data: any, i: number) => {
    if(data.length === 0) return;
    const newVlaue = {
        pm25: data[data.length - 1].pm25Threshold,
        pm100: data[data.length - 1].pm100Threshold,
        co2: data[data.length - 1].co2Threshold,
        voc: data[data.length - 1].vocThreshold,
    }
    setCurrentRoomThreshold(prev => prev.map((item,idx) => idx === i ? newVlaue : item));
  };

  const fetchRoomThreshold = async (roomID: number) => {
    try {
      const response = await axios.get(`${API_BASE}/policies`);
      applyRoomThresholdFromResponse(response.data, roomID);
    } catch (e) {
      console.error('에러:', e);
    }
  };

  const handleSaveUserThreshold = async (modalData: any) => {
    try {
      const id = 1;
      const form = {
        "pm25Threshold": modalData[0],
        "pm100Threshold": modalData[1],
        "co2Threshold": modalData[2],
        "vocThreshold": modalData[3]
      };
      await axios.post(`${API_BASE}/users/${id}/preferences`, form);
      await fetchUserThreshold();

    } catch (e) {
      console.error("에러", e);
    }
  };

  const handleSaveRoomThreshold = async (modalData: any, roomID: number) => {
    try {
      const form = {
        "roomId": roomID, 
        "pm25Threshold": modalData[0],
        "pm100Threshold": modalData[1],
        "co2Threshold": modalData[2],
        "vocThreshold": modalData[3]
      };
      await axios.post(`${API_BASE}/policies`, form);
      await fetchRoomThreshold(roomID - 1);
    } catch (e) {
      console.error("에러", e);
    }
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
    fetchGraphData();
    handleRefresh();
    fetchUserThreshold();
    fetchRoomThreshold(3);
    fetchRooms();
    fetchAllSensorData();
    fetchSensorMetaList();
    handleRefresh_visual();
  }, []);

  useEffect(() => {
    setCurrentAQI(calculateAQI(currentData.pm25, currentData.pm100));
  }, [currentData]);

  useEffect(() => {
    if (sensorDataList.length && sensorMetaList.length) {
      buildHeatmapPoints(sensorDataList, sensorMetaList, 'pm25');
    }
  }, [sensorDataList, sensorMetaList]);

  return (
    <View style={styles.container}>
      <View style={styles.locationContainer}>
        <Text style={styles.location_label}>
          실내
        </Text>
        <SelectDropdown
          data={DISTRICTS}
          defaultValue={selectedDistrict}
          onSelect={(item, index) => {
            setSelectedDistrict(item);
          }}
          renderButton={(item, isOpened) => (
            <Pressable style={styles.select}>
              <Text style={styles.select_label}>
                {item}
              </Text>
              <Image
                source={isOpened ? require('../assets/arrow_up.png') : require('../assets/arrow_down.png')}
                style={styles.select_image}
              />
              
            </Pressable>
          )}
          renderItem={(item, index, isSelected) => (
            <Pressable
              style={[
                styles.item,
                isSelected && styles.selectedItem,
              ]}
            >
              <Text
                style={[
                  styles.itemText,
                  isSelected && styles.selectedItemText,
                ]}
              >
                {item}
              </Text>
            </Pressable>
          )}
          dropdownOverlayColor="transparent"
          dropdownStyle={styles.dropdown}
          showsVerticalScrollIndicator={false}
        />
      </View>
      <View style={styles.contentContainer}>
        <ScrollView style={styles.list} contentContainerStyle={styles.listContent}>
          <View style={[styles.summary, currentAQI<=200 && {backgroundColor: '#FCDFD9'}, , currentAQI<=150 && {backgroundColor: '#FCFCFC'}, , currentAQI<=100 && {backgroundColor: '#D5FCD2'}, , currentAQI<=50 && {backgroundColor: '#88FC7D'}]}>
            <View style={styles.content_labelBox}>
              <Text style={styles.content_label}>
                공기질 요약
              </Text>
            </View>
            <View style={styles.summary_content}>
              <Text style={[styles.summary_state, currentAQI<=200 && {fontSize: 50, color: '#FF4040'}, , currentAQI<=150 && {fontSize: 50, color: '#8F8F8F'}, , currentAQI<=100 && {fontSize: 50, color: '#24DA00'}, , currentAQI<=50 && {fontSize: 40, color: '#00700D'}]}>
                {currentAQI<=50 ? '매우좋음' : currentAQI<=100 ? '좋음' : currentAQI<=150 ? '보통' : currentAQI<=200 ? '나쁨' : '매우나쁨'}
              </Text>
              <View style={styles.summary_valueBox}>
                <ImageBackground
                  source={require('../assets/rainbow.png')}
                  style={styles.summary_aqi_image}
                >
                    <Text style={styles.summary_aqi_label}>AQI</Text>
                    <Text style={styles.summary_aqi_value}>{currentAQI}</Text>
                    <View
                      style={[
                        styles.marker,
                        {
                          width: markerRadius * 2,
                          height: markerRadius * 2,
                          borderRadius: markerRadius,
                          left: markerX - markerRadius,
                          top: markerY - markerRadius,
                        },
                      ]}
                    />
                </ImageBackground>
                <View style={styles.summary_detail_container}>

                  <View style={styles.summary_detailBox}>
                    <Text style={styles.summary_detail_label}>
                      PM2.5
                    </Text>
                    <View style={styles.summary_detail}>
                      <View style={[styles.summary_detail_state, currentData.pm25<=35 && {backgroundColor: '#FCDFD9'}, currentData.pm25<=25 && {backgroundColor: '#FCFCFC'}, currentData.pm25<=15 && {backgroundColor: '#D5FCD2'}, currentData.pm25<=5 && {backgroundColor: '#88FC7D'}]}></View>
                      <Text style={styles.summary_detail_value}>
                        {currentData.pm25}
                      </Text>
                    </View>
                  </View>
                  <View style={styles.summary_detailBox}>
                    <Text style={styles.summary_detail_label}>
                      PM10
                    </Text>
                    <View style={styles.summary_detail}>
                      <View style={[styles.summary_detail_state, currentData.pm100<=70 && {backgroundColor: '#FCDFD9'}, currentData.pm100<=50 && {backgroundColor: '#FCFCFC'}, currentData.pm100<=30 && {backgroundColor: '#D5FCD2'}, currentData.pm100<=15 && {backgroundColor: '#88FC7D'}]}></View>
                      <Text style={styles.summary_detail_value}>
                        {currentData.pm100}
                      </Text>
                    </View>
                  </View>
                  <View style={styles.summary_detailBox}>
                    <Text style={styles.summary_detail_label}>
                      CO2
                    </Text>
                    <View style={styles.summary_detail}>
                      <View style={[styles.summary_detail_state, currentData.co2<=3000 && {backgroundColor: '#FCDFD9'}, currentData.co2<=2000 && {backgroundColor: '#FCFCFC'}, currentData.co2<=1000 && {backgroundColor: '#D5FCD2'}, currentData.co2<=400 && {backgroundColor: '#88FC7D'}]}></View>
                      <Text style={styles.summary_detail_value}>
                        {currentData.co2}
                      </Text>
                    </View>
                  </View>
                  <View style={styles.summary_detailBox}>
                    <Text style={styles.summary_detail_label}>
                      VOC
                    </Text>
                    <View style={styles.summary_detail}>
                      <View style={[styles.summary_detail_state, currentData.voc<=2000 && {backgroundColor: '#FCDFD9'}, currentData.voc<=1000 && {backgroundColor: '#FCFCFC'}, currentData.voc<=400 && {backgroundColor: '#D5FCD2'}, currentData.voc<=100 && {backgroundColor: '#88FC7D'}]}></View>
                      <Text style={styles.summary_detail_value}>
                        {currentData.voc}
                      </Text>
                    </View>
                  </View>

                </View>
              </View>
            </View>
          </View>
          <View style={styles.prediction}>
            <View style={styles.content_labelBox}>
              <Text style={styles.content_label}>
                예측 정보
              </Text>
              <Text style={styles.content_label_hour}>
                1시간
              </Text>
              <Text style={styles.prediction_label_time}>
                {lastRefreshed}
              </Text>
              <Pressable
                style={styles.prediction_label_refresh}
                onPress={() => {fetchGraphData(); handleRefresh();}}
              >
                <Image
                  source={require('../assets/refresh.png')}
                  style={styles.prediction_label_refresh_image}
                />
              </Pressable>
            </View>
            <View  style={styles.indexBox}>
              {info_data.map((item, i) => (
                <Pressable
                  key={i}
                  style={[styles.prediction_index, activePrediction === i ? styles.index_active : styles.index_inactive]}
                  onPress={() => setActivePrediction(i)}
                >
                  <Text style={styles.index_label_base}>
                    {item.key}
                  </Text>
                </Pressable>
              ))}
            </View>
            <View style={styles.chart}>
              {(() => {
                switch (activePrediction) {
                  case 0:
                    return <Graph data={currentGraphData['pm25']} type='pm25'></Graph>;
                  case 1:
                    return <Graph data={currentGraphData['pm100']} type='pm100'></Graph>;
                  case 2:
                    return <Graph data={currentGraphData['co2']} type='co2'></Graph>;
                  case 3:
                    return <Graph data={currentGraphData['voc']} type='voc'></Graph>;
                  default:
                    return null;
                }
              })()}
            </View>
            <View style={styles.showDetail}>
              <Pressable
                style={styles.showDetail_button}
                android_ripple={{
                  color: 'rgba(0,0,0,0.1)',
                  radius: 62,
                }}
                onPress={() => setModalVisible_prediction(true)}
              >
                <Text style={styles.showDetail_label}>
                  자세히 보기
                </Text>
              </Pressable>
              <SlideModal_prediction isVisible={modalVisible_prediction} onClose={() => setModalVisible_prediction(false)}>
              </SlideModal_prediction>
            </View>
          </View>
          <View style={styles.visualization}>
            <View style={styles.content_labelBox}>
              <Text style={styles.content_label}>
                공기질 시각화
              </Text>
              <Text style={styles.visualization_label_time}>
                {lastRefreshed_visual}
              </Text>
              <Pressable
                style={styles.visualization_label_refresh}
                onPress={() => {fetchAllSensorData(); handleRefresh_visual();}}
              >
                <Image
                  source={require('../assets/refresh.png')}
                  style={styles.visualization_label_refresh_image}
                />
              </Pressable>
            </View>
            <View style={[styles.visualization_content, {backgroundColor: '#FFFFFF'}]}>
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
            <View style={styles.showDetail}>
              <Pressable
                style={styles.showDetail_button}
                android_ripple={{
                  color: 'rgba(0,0,0,0.1)',
                  radius: 62,
                }}
                onPress={() => setModalVisible_visualization(true)}
              >
                <Text style={styles.showDetail_label}>
                  자세히 보기
                </Text>
              </Pressable>
              <SlideModal_visualization isVisible={modalVisible_visualization} onClose={() => setModalVisible_visualization(false)}>
              </SlideModal_visualization>
            </View>
          </View>
          
          <View style={styles.schedule}>
            <View style={styles.content_labelBox}>
              <Text style={styles.content_label}>
                스케줄
              </Text>
            </View>
            <View style={styles.schedule_content}>
              <Pressable
                style={styles.dayWeekBox}
              >
                <CheckBox
                  value={dayChecked}
                  tintColors={{ true: '#01B3EA', false: '#777' }}
                  style={styles.checkbox}
                />
                <Text style={styles.dayWeek}>
                   금요일
                </Text>
              </Pressable>
              {
              dayChecked?
              <MultiSlider
                values={range}
                sliderLength={270}
                onValuesChange={vals => setRange([vals[0], vals[1]])}
                min={0}
                max={60*24}
                step={5}
                enabledOne={false}
                enabledTwo={false}
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
              />:<></>
              }
            </View>
            <View style={styles.edit}>
              <Pressable
                style={styles.edit_button}
                android_ripple={{
                  color: 'rgba(0,0,0,0.1)',
                  radius: 50,
                }}
                onPress={() => setModalVisible_schedule(true)}
              >
                <Text style={styles.edit_label}>
                  수정
                </Text>
              </Pressable>
              <SlideModal_schedule isVisible={modalVisible_schedule} onClose={() => {setModalVisible_schedule(false)}} initialRange={range} onSave={(newRange: [number, number]) => {setRange(newRange); setModalVisible_schedule(false);}}>
              </SlideModal_schedule>
            </View>
          </View>
          <View style={styles.personal}>
            <View style={styles.content_labelBox}>
              <Text style={styles.content_label}>
                관리 기준치
              </Text>
            </View>
            <View style={styles.personalContainer}>
              {info_data.map((item, i) => (
                <Pressable
                  key={item.key}
                  style={styles.check_box}
                >
                  <CheckBox
                    value={checked[i]}
                    tintColors={{ true: '#01B3EA', false: '#777' }}
                    style={styles.checkbox}
                  />
                  <Text style={styles.personal_label}>
                    {item.key}({item.title}): {currentUserThreshold[item.id as ThresholdKey]}{item.unit}
                  </Text>
                </Pressable>
              ))}
              <View style={styles.edit}>
                <Pressable
                  style={styles.edit_button}
                  android_ripple={{
                    color: 'rgba(0,0,0,0.1)',
                    radius: 50,
                  }}
                  onPress={() => {setModalVisible_userThreshold(true);}}
                >
                  <Text style={styles.edit_label}>
                    수정
                  </Text>
                </Pressable>
                <SlideModal_userThreshold isVisible={modalVisible_userThreshold} onSave={handleSaveUserThreshold} onClose={() => setModalVisible_userThreshold(false)} initialValues={Object.values(currentUserThreshold)}>
                </SlideModal_userThreshold>
              </View>
            </View>
          </View>

          <SelectDropdown
            data={ROOMS}
            defaultValue={selectedRoom}
            onSelect={(item, index) => {
              setSelectedRoom(item);
            }}
            renderButton={(item, isOpened) => (
              <Pressable style={styles.select_room}>
                <Text style={styles.select_label_room}>
                  {item}
                </Text>
                <Image
                  source={isOpened ? require('../assets/arrow_up.png') : require('../assets/arrow_down.png')}
                  style={styles.select_image}
                />
                
              </Pressable>
            )}
            renderItem={(item, index, isSelected) => (
              <Pressable
                style={[
                  styles.item,
                  isSelected && styles.selectedItem,
                ]}
              >
                <Text
                  style={[
                    styles.itemText,
                    isSelected && styles.selectedItemText,
                  ]}
                >
                  {item}
                </Text>
              </Pressable>
            )}
            dropdownOverlayColor="transparent"
            dropdownStyle={styles.dropdown}
            showsVerticalScrollIndicator={false}
          />


          <View style={styles.control}>
            <Pressable
              style={[styles.auto, isAuto ? styles.auto_active : styles.auto_inactive]}
              android_ripple={{
                color: 'rgba(0,0,0,0.1)',
                radius: 74,
              }}
              onPress={() =>setIsAuto(true)}
            >
              <Text style={[styles.auto_label, isAuto ? styles.auto_label_active : styles.auto_label_inactive]}>
                자동 제어
              </Text>
            </Pressable>
            <Pressable
              style={[styles.auto, isAuto ? styles.auto_inactive : styles.auto_active]}
              android_ripple={{
                color: 'rgba(0,0,0,0.1)',
                radius: 74,
              }}
              onPress={() =>setIsAuto(false)}
            >
              <Text style={[styles.auto_label, isAuto ? styles.auto_label_inactive : styles.auto_label_active]}>
                수동 제어
              </Text>
            </Pressable>
          </View>
          {isAuto &&
            <>
              <View style={styles.personal}>
                <View style={styles.content_labelBox}>
                  <Text style={styles.content_label}>
                    방별 관리 기준치
                  </Text>
                </View>
                <View style={styles.personalContainer}>
                  {info_data.map((item, i) => (
                    <Pressable
                      key={item.key}
                      style={styles.check_box}
                    >
                      <CheckBox
                        value={checked[i]}
                        tintColors={{ true: '#01B3EA', false: '#777' }}
                        style={styles.checkbox}
                      />
                      <Text style={styles.personal_label}>
                        {item.key}({item.title}): {currentRoomThreshold[ROOMS.findIndex(room => room === selectedRoom)][item.id as keyof RoomThreshold]}{item.unit}
                      </Text>
                    </Pressable>
                  ))}
                  <View style={styles.edit}>
                    <Pressable
                      style={styles.edit_button}
                      android_ripple={{
                        color: 'rgba(0,0,0,0.1)',
                        radius: 50,
                      }}
                      onPress={() => {setModalVisible_roomThreshold(true); info_data[0].value=5;info_data[1].value=15;}}
                    >
                      <Text style={styles.edit_label}>
                        수정
                      </Text>
                    </Pressable>
                    <SlideModal_roomThreshold isVisible={modalVisible_roomThreshold} onSave={(modalData) => handleSaveRoomThreshold(modalData, ROOMS.findIndex(room => room === selectedRoom) + 1)} onClose={() => setModalVisible_roomThreshold(false)} initialValues={currentRoomThreshold[ROOMS.findIndex(room => room === selectedRoom)].pm25 === 0 ? Object.values(currentUserThreshold) : Object.values(currentRoomThreshold[ROOMS.findIndex(room => room === selectedRoom)])}>
                    </SlideModal_roomThreshold>
                  </View>
                </View>
              </View>
            </>
          }
          {!isAuto && selectedRoom === '방3' &&
            <>
              {deviceState.map((item, i) => (
                <View style={styles.device} key={item.key}>
                  <View style={styles.device_labelBox}>
                    <Text style={styles.device_label}>
                      {item.name} - {item.location}
                    </Text>
                    <Text style={styles.filter}>
                      필터 소모율: 0.18%
                    </Text>
                  </View>
                  <View style={styles.stateBox}>
                    <View style={styles.backLineBox}>
                      <View style={styles.backLine}>
                      </View>
                      <View style={styles.backLine}>
                      </View>
                      <View style={styles.backLine}>
                      </View>
                    </View>
                    <Pressable style={styles.state_button_1} onPress={() => {handleDeviceControl(0); setDeviceManual(0);}}>
                      <View style={styles.state_button_view}>
                      </View>
                    </Pressable>
                    <Pressable style={styles.state_button_2} onPress={() => {handleDeviceControl(1); setDeviceManual(1);}}>
                      <View style={[styles.state_button_view, deviceManual > 0 ? styles.state_button_view_active : styles.state_button_view_inactive]}>
                      </View>
                    </Pressable>
                    <Pressable style={styles.state_button_3} onPress={() => {handleDeviceControl(2); setDeviceManual(2);}}>
                      <View style={[styles.state_button_view, deviceManual > 1 ? styles.state_button_view_active : styles.state_button_view_inactive]}>
                      </View>
                    </Pressable>
                    <Pressable style={styles.state_button_4} onPress={() => {handleDeviceControl(3); setDeviceManual(3);}}>
                      <View style={[styles.state_button_view, deviceManual > 2 ? styles.state_button_view_active : styles.state_button_view_inactive]}>
                      </View>
                    </Pressable>
                    <View style={styles.state_labelBox}>
                      <Text style={styles.state_label}>
                        꺼짐
                      </Text>
                      <Text style={styles.state_label}>
                        1단계
                      </Text>
                      <Text style={styles.state_label}>
                        2단계
                      </Text>
                      <Text style={styles.state_label}>
                        3단계
                      </Text>
                    </View>
                  </View>
                </View>
              ))}
            </>
          }
          
          <Pressable
            style={styles.setting}
            android_ripple={{
              color: 'rgba(0,0,0,0.1)',
              radius: 160,
            }}
            onPress={() => setModalVisible_edit(true)}
          >
            <Text style={styles.setting_label}>실내 구조 및 기기 관리</Text>
          </Pressable>
          <SlideModal_edit isVisible={modalVisible_edit} onClose={() => setModalVisible_edit(false)}>
          </SlideModal_edit>
          <View style={styles.information}>
            <View style={styles.content_labelBox}>
              <Text style={styles.content_label}>
                대기 정보 기준
              </Text>
            </View>
            <View style={styles.indexBox}>
              {info_data.map((item, i) => (
                <Pressable
                  key={i}
                  style={[styles.index, activeInfo === i ? styles.index_active : styles.index_inactive]}
                  onPress={() => setActiveInfo(i)}
                >
                  <Text style={styles.index_label_base}>
                    {item.key}
                  </Text>
                </Pressable>
              ))}
            </View>
            <View style={styles.information_contentBox}>
              <Text style={styles.information_title}>
                {activeItem.title}
              </Text>
              <Text style={styles.information_data}>
                {activeItem.data}
              </Text>
              <Text style={styles.guidline_unit}>
                {activeItem.unit}
              </Text>
              <View style={styles.guidlineContainer}>
                <View style={styles.guidlineBox}>
                  <View style={[styles.guidline_color, styles.color_veryGood]}>
                  </View>
                  <Text style={styles.guidline_label}>
                    {activeItem.threshold[0]}
                  </Text>
                </View>
                <View style={styles.guidlineBox}>
                  <View style={[styles.guidline_color, styles.color_good]}>
                  </View>
                  <Text style={styles.guidline_label}>
                    {activeItem.threshold[1]}
                  </Text>
                </View>
                <View style={styles.guidlineBox}>
                  <View style={[styles.guidline_color, styles.color_normal]}>
                  </View>
                  <Text style={styles.guidline_label}>
                    {activeItem.threshold[2]}
                  </Text>
                </View>
                <View style={styles.guidlineBox}>
                  <View style={[styles.guidline_color, styles.color_bad]}>
                  </View>
                  <Text style={styles.guidline_label}>
                    {activeItem.threshold[3]}
                  </Text>
                </View>
                <View style={styles.guidlineBox}>
                  <View style={[styles.guidline_color, styles.color_verybad]}>
                  </View>
                  <Text style={styles.guidline_label}>
                    {activeItem.threshold[4]}
                  </Text>
                </View>
                
              </View>
            </View>
          </View>
        </ScrollView>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F6FAFF',
  },
  locationContainer: {
    flex: 0.5,
    flexDirection: 'row',
    alignItems: 'flex-end',
  },
  contentContainer: {
    flex: 6.5,
  },
  location_label: {
    fontSize: 30,
    fontWeight: 700,

    marginLeft: '7%',
  },
  
  select: {
    height: '70%',
    flexDirection: 'row',
    alignItems: 'center',
    marginHorizontal: 5,
  },
  select_room: {
    height: 50,
    alignSelf: 'flex-start',
    flexDirection: 'row',
    alignItems: 'center',
    marginHorizontal: 10,
  },
  select_label: {
    width: 90,
    fontSize: 18,
    fontWeight: 600,
    textAlign: 'center',
  },
  select_label_room: {
    width: 90,
    fontSize: 25,
    fontWeight: 600,
    textAlign: 'center',
  },
  select_image: {
    width: 15,
    height: 15,
    resizeMode: 'contain',
    marginHorizontal: 5,
  },
  
  item: {
    paddingVertical: 12,
    paddingHorizontal: 16,
    alignItems: 'center',
  },
  itemText: {
    fontSize: 16,
  },
  selectedItem: {
    backgroundColor: '#D2E3FC',
  },
  selectedItemText: {
    color: '#2f95dc',
    fontWeight: 'bold',
  },

  dropdown: {
    backgroundColor: '#FFFFFF',
    borderRadius: 8,
    maxHeight: 200,
    marginLeft: -3,
  },

  list: {
    width: '100%',
  },
  listContent: {
    alignItems: 'center',
  },
  summary: {
    backgroundColor: '#FCA596',
    width: '90%',
    height: 170,
    borderRadius: 15,
    marginVertical: 7,
    elevation: 4,
  },
  prediction: {
    backgroundColor: '#FFFFFF',
    width: '90%',
    height: 260,
    borderRadius: 15,
    marginVertical: 7,
    elevation: 4,
  },
  visualization: {
    backgroundColor: '#FFFFFF',
    width: '90%',
    height: 410,
    borderRadius: 15,
    marginVertical: 7,
    elevation: 4,
  },
  control: {
    flexDirection: 'row',
  },
  schedule: {
    backgroundColor: '#FFFFFF',
    width: '90%',

    borderRadius: 15,
    marginVertical: 7,
    elevation: 4,
  },
  personal: {
    backgroundColor: '#FFFFFF',
    width: '90%',
    height: 275,
    borderRadius: 15,
    marginVertical: 7,
    elevation: 4,
  },
  information: {
    backgroundColor: '#FFFFFF',
    width: '90%',
    height: 295,
    borderRadius: 15,
    marginVertical: 7,
    elevation: 4,
  },
  device: {
    backgroundColor: '#FFFFFF',
    width: '90%',
    height: 150,
    borderRadius: 15,
    marginVertical: 7,
    elevation: 4,
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
  content_label_hour: {
    color: '#777777',
    fontSize: 17,
    fontWeight: 500,
    marginLeft: '2%',
    width: 120,
  },

  prediction_label_time: {
    color: '#777777',
    fontSize: 14,
    fontWeight: 500,
  },
  prediction_label_refresh: {
    width: 30,
    height: 30,
    justifyContent: 'center',
    alignItems: 'center',
  },
  prediction_label_refresh_image: {
    width: 15,
    height: 15,
  },
  chart: {
    alignSelf: 'center',
    width: 300,
    height: 125,
    marginBottom: 10,
  },

  visualization_label_time: {
    color: '#777777',
    fontSize: 14,
    fontWeight: 500,
    marginLeft: 90,
  },
  visualization_label_refresh: {
    width: 30,
    height: 30,
    justifyContent: 'center',
    alignItems: 'center',
  },
  visualization_label_refresh_image: {
    width: 15,
    height: 15,
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

  showDetail: {
    alignItems: 'center'
  },
  showDetail_button: {
    width: 130,
    height: 35,
    borderRadius: 100,
    backgroundColor: '#01B3EA',
    alignItems: 'center',
    justifyContent: 'center',
    elevation: 4,
  },
  showDetail_label: {
    fontSize: 18,
    color: '#FFFFFF',
  },

  summary_content: {
    height: '80%',
    flexDirection: 'row',
    alignItems: 'center',
  },
  summary_state: {
    color: '#780000',
    fontSize: 40,
    fontWeight: 500,
    width: 100,
    textAlign: 'center',
    marginLeft: '7%',
    marginRight: '4%',
    marginBottom: '8%',
  },
  summary_valueBox: {
    width: 170,
    alignItems: 'center',
    marginTop: -15,
  },
  summary_aqi_image: {
    width: 142,
    height: 110,
    resizeMode: 'contain',
    alignItems: 'center',
    justifyContent: 'flex-end',
  },
  summary_aqi_label: {
    fontSize: 22,
    fontWeight: 700,
    color: '#777777'
  },
  summary_aqi_value: {
    fontSize: 40,
    fontWeight: 600,
  },

  summary_detail_container: {
    width: '100%',
    flexDirection: 'row',
  },
  summary_detailBox: {
    width: '25%',
    marginBottom: 10,
    alignItems: 'center',
  },
  summary_detail_label: {
    color: '#888888',
    fontSize: 12,
    fontWeight: 600,
  },
  summary_detail: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  summary_detail_state: {
    borderColor: '#777777',
    borderWidth: 0.5,
    backgroundColor: '#FCA596',
    width: 7,
    height: 7,
    borderRadius: 100,
    marginRight: 3,
  },
  summary_detail_value: {
    fontSize: 13,
    color: '#000000',
  },
  marker: {
    position: 'absolute',
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#AAAAAA',
  },

  auto: {
    width: 155,
    height: 45,
    backgroundColor: '#D9D9D9',
    borderRadius: 100,
    marginVertical: 13,
    marginHorizontal: 5,
    alignItems: 'center',
    justifyContent: 'center',
    elevation: 4,
  },
  auto_active: {
    backgroundColor: '#01B3EA',
  },
  auto_inactive: {
    backgroundColor: '#D9D9D9',
  },
  auto_label: {
    fontSize: 25,
    fontWeight: 700,
    color: '#FFFFFF'
  },
  auto_label_active: {
    color: '#FFFFFF'
  },
  auto_label_inactive: {
    color: '#000000'
  },

  schedule_content: {
    marginVertical: 10,
  },
  dayWeekBox: {
    flexDirection: 'row',
    alignItems: 'center',
    marginHorizontal: 20,
    marginBottom: 20,
  },
  dayWeek: {
    fontSize: 19,
    fontWeight: 500,
  },
  sliderContainer: {
    marginVertical: 25,
    marginHorizontal: 30,
  },
  labelContainer: {
    marginHorizontal: 30,
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

  edit: {
    alignItems: 'center',
    marginVertical: 10,
  },
  edit_button: {
    width: 100,
    height: 35,
    borderRadius: 100,
    backgroundColor: '#01B3EA',
    alignItems: 'center',
    justifyContent: 'center',
    elevation: 4,
  },
  edit_label: {
    fontSize: 18,
    color: '#FFFFFF',
  },

  device_labelBox: {
    height: 34,
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: '2%',
  },
  device_label: {
    fontSize: 20,
    width: 180,
    fontWeight: 700,
    marginLeft: '6%',
  },
  filter: {
    color: '#777777',
    fontSize: 15,
    fontWeight: 600,
  },
  stateBox: {
    alignItems: 'center',
    marginVertical: 30,
  },
  backLineBox: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginVertical: 15,
  },
  backLine: {
    height: 3,
    width: 76,
    backgroundColor: '#777777',
    marginHorizontal: 5,
  },
  state_button: {

  },
  state_button_view: {
    width: 20,
    height: 20,
    borderColor: '#674CFF',
    borderWidth: 2,
    backgroundColor: '#674CFF',
    borderRadius: 100,
  },
  state_button_view_active: {
    borderColor: '#674CFF',
    backgroundColor: '#674CFF',
  },
  state_button_view_inactive: {
    borderColor: '#777777',
    backgroundColor: '#FFFFFF',
  },
  state_button_1: {
    position: 'absolute',
    left: 25,
    top: -4,
    width: 40,
    height: 40,
    alignItems: 'center',
    justifyContent: 'center',
  },
  state_button_2: {
    position: 'absolute',
    left: 101,
    top: -4,
    width: 40,
    height: 40,
    alignItems: 'center',
    justifyContent: 'center',
  },
  state_button_3: {
    position: 'absolute',
    left: 187,
    top: -4,
    width: 40,
    height: 40,
    alignItems: 'center',
    justifyContent: 'center',
  },
  state_button_4: {
    position: 'absolute',
    left: 273,
    top: -4,
    width: 40,
    height: 40,
    alignItems: 'center',
    justifyContent: 'center',
  },
  state_labelBox: {
    width: 305,
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginLeft: 5,
  },
  state_label: {
    fontSize: 14,
    fontWeight: 700,
    marginHorizontal: 10.
  },

  setting:{
    backgroundColor: '#01B3EA',
    width: '90%',
    height: 62,
    borderRadius: 10,
    marginVertical: 10,
    justifyContent: 'center',
    alignItems: 'center',
    elevation: 4,
  },
  setting_label:{
    fontSize: 23,
    fontWeight: 600,
    color: '#FFFFFF'
  },

  personalContainer: {
    marginVertical: 10,
    marginBottom: 20,
  },

  personal_label: {
    fontSize: 16,
    fontWeight: 600,
  },
  check_box: {
    marginVertical: 5,
    marginHorizontal: 20,
    flexDirection: 'row',
    alignItems: 'center',
  },
  checkbox: {
  },

  indexBox: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  index: {
    width: 70,
    height: 25,
    backgroundColor: '#DFDFDF',
    justifyContent: 'center',
    borderRadius: 100,
    borderColor: '#DFDFDF',
    borderWidth: 1,
    alignItems: 'center',
    marginVertical: '4%',
    marginHorizontal: '1%',
    elevation: 1,
  },
  prediction_index: {
    width: 70,
    height: 25,
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


  information_contentBox: {
    marginHorizontal: '7%',
  },
  information_title: {
    fontSize: 17,
    fontWeight: 600,
    marginBottom: '2%',
  },
  information_data: {
    fontSize: 14,
  },
  
  guidline_unit: {
    color: '#777777',
    fontSize: 13,
    marginTop: 7,
  },
  guidlineContainer: {
    width: '100%',
    flexDirection: 'row',
  },
  guidlineBox: {
    width: '19%',
    marginHorizontal: '0.5%',
  },
  guidline_color: {
    height: 7,
    width: '100%',
    borderRadius: 100,
    backgroundColor: 'red',
    
  },
  guidline_label: {
    fontSize: 13,
  },

  color_veryGood: {
    backgroundColor: '#83FB80'
  },
  color_good: {
    backgroundColor: '#D0FBD5'
  },
  color_normal: {
    backgroundColor: '#FFFFFF'
  },
  color_bad: {
    backgroundColor: '#F7DEDC'
  },
  color_verybad: {
    backgroundColor: '#F7A499'
  },

  
});

export default IndoorScreen;
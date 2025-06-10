import React, { useState, useEffect } from 'react';
import { View, Image, Pressable, Text, StyleSheet, FlatList, Dimensions } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import type { MaterialTopTabNavigationProp } from '@react-navigation/material-top-tabs';
import type { ParamList } from '../navigation/TabNavigator';
import { Graph } from '../components/Graph'
import axios from 'axios';

const { width: SCREEN_WIDTH} = Dimensions.get('window');

const slides = [
  {
    key: '1',
    image: require('../assets/weather_sunny.png')
  },
  {
    key: '2',
    image: require('../assets/leaf.png')
  },
]

const info_data = [
  {
    key: 'PM2.5',
    title: '초미세먼지',
    unit: 'µg/m³',
    threshold: ['0', '5', '15', '25', '35-'],
    state: 1,
  }, 
  {
    key: 'PM10',
    title: '미세먼지',
    unit: 'µg/m³',
    threshold: ['0', '15', '30', '50', '70-'],
    state: 1,
  }, 
  {
    key: 'CO2',
    title: '이산화탄소',
    unit: 'ppm',
    threshold: ['0', '400', '1000', '2000', '5000-'],
    state: 4,
  }, 
  {
    key: 'VOC',
    title: '휘발성유기화합물',
    unit: 'ppm',
    threshold: ['0', '100', '400', '1000', '2000-'],
    state: 2,
  }
]

type TabNavProp = MaterialTopTabNavigationProp<ParamList>;

type DataPoint = { x: number; y: number };

type AirQualityData = {
  pm25: DataPoint[];
  pm100: DataPoint[];
  co2: DataPoint[];
  voc: DataPoint[];
};

export const HomeScreen = () => {
  const [activePresent, setActivePresent] = useState<number>(0);
  const navigation = useNavigation<TabNavProp>();
  const [activePrediction, setActivePrediction] = useState(0);
  const [currentData_indoor, setCurrentData_indoor] = useState({
    pm25: 0,
    pm100: 0,
    co2: 0,
    voc: 0,
  });
  const [currentData_outdoor, setCurrentData_outdoor] = useState({
    pm25: 0,
    pm100: 0,
    o3: 0,
    no2: 0,
    tmp: 0,
  });
  const [currentAQI_indoor, setCurrentAQI_indoor] = useState(0);
  const [currentAQI_outdoor, setCurrentAQI_outdoor] = useState(0);
  const [currentGraphData, setcurrentGraphData] = useState<AirQualityData>({
    pm25: [],
    pm100: [],
    co2: [],
    voc: [],
  });

  function calculateAQI_indoor(pm25: number, pm100: number): number {
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

function calculateAQI_outdoor(pm25: number, pm100: number, o3: number, no2: number): number {
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

    const o3Breakpoints = [
      { concLow: 0, concHigh: 54, aqiLow: 0, aqiHigh: 50 },
      { concLow: 55, concHigh: 70, aqiLow: 51, aqiHigh: 100 },
      { concLow: 71, concHigh: 85, aqiLow: 101, aqiHigh: 150 },
      { concLow: 86, concHigh: 105, aqiLow: 151, aqiHigh: 200 },
      { concLow: 106, concHigh: 200, aqiLow: 201, aqiHigh: 300 },
    ];

    const no2Breakpoints = [
      { concLow: 0, concHigh: 53, aqiLow: 0, aqiHigh: 50 },
      { concLow: 54, concHigh: 100, aqiLow: 51, aqiHigh: 100 },
      { concLow: 101, concHigh: 360, aqiLow: 101, aqiHigh: 150 },
      { concLow: 361, concHigh: 649, aqiLow: 151, aqiHigh: 200 },
      { concLow: 650, concHigh: 1249, aqiLow: 201, aqiHigh: 300 },
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
      return -1;
    }

    const aqiPm25 = getAQI(pm25, pm25Breakpoints);
    const aqiPm100 = getAQI(pm100, pm100Breakpoints);
    const aqiO3 = getAQI(o3, o3Breakpoints);
    const aqiNo2 = getAQI(no2, no2Breakpoints);

    return Math.max(aqiPm25, aqiPm100, aqiO3, aqiNo2);
  }



  const API_BASE = 'http://18.191.176.79:8080';

  const fetchAvgData_indoor = async () => {
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
      setCurrentData_indoor({
        pm25: Math.round(data[0] / data.length),
        pm100: Math.round(data[1] / data.length),
        co2: Math.round(data[2] / data.length),
        voc: Math.round(data[3] / data.length),
    })
    } catch (e) {
    console.error('에러:', e);
    }
  };

  const fetchData_outdoor = async () => {
    const userID = 1;
    try {
      const response = await axios.get(`${API_BASE}/users/${userID}/regions/forecast`);
      const districts_index = 2;
      setCurrentData_outdoor({
        pm25: Math.round(response.data[districts_index].forecast[0].pm25),
        pm100: Math.round(response.data[districts_index].forecast[0].pm10),
        o3: Math.round(response.data[districts_index].forecast[0].o3),
        no2: Math.round(response.data[districts_index].forecast[0].no2),
        tmp: Math.round(response.data[districts_index].forecast[0].tmp),
      })
    } catch (e) {
    console.error('에러:', e);
    }
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

  useEffect(() => {
    fetchAvgData_indoor();
    fetchData_outdoor();
    fetchGraphData();
    handleRefresh();
  }, []);

  useEffect(() => {
    setCurrentAQI_indoor(calculateAQI_indoor(currentData_indoor.pm25, currentData_indoor.pm100));
  }, [currentData_indoor]);

  useEffect(() => {
    setCurrentAQI_outdoor(calculateAQI_outdoor(currentData_outdoor.pm25, currentData_outdoor.pm100, currentData_outdoor.o3, currentData_outdoor.no2));
  }, [currentData_outdoor]);

  return (
    <View style={styles.container}>
      <View style={styles.presentContainer}>
        <View style={styles.present}>
          <FlatList
          style={styles.present_list}
          data={slides}
          horizontal
          pagingEnabled={true}
          showsHorizontalScrollIndicator={false}
          keyExtractor={item => item.key}
          renderItem={({ item }) => (
            <View style={styles.present_container}>
              <View style={styles.present_textBox}>
                {item.key === '1' && (
                  <>
                    <View style={{flexDirection: 'row', alignItems: 'flex-end', justifyContent: 'center'}}>
                      <Image source={item.image} style={styles.present_image} />
                      <Text style={styles.present_text_temperture}>
                        {currentData_outdoor.tmp}
                      </Text>
                      <Text style={[styles.present_text_temperture, {fontSize: 40, marginLeft: 10, marginBottom: 10}]}>
                        ℃
                      </Text>
                    </View>
                    <Text style={styles.present_text_compare}>
                      어제보다 {currentData_outdoor.tmp}℃ 높아요
                    </Text>
                  </>
                )}
                {item.key === '2' && (
                <Text style={styles.present_text_energy}>
                  AI 자동 제어로{'\n'}전기 사용을{'\n'}25% 절약했어요
                </Text>
                )}
              </View>
              
            </View>
          )}
          onViewableItemsChanged={({ viewableItems }) => {
            if (viewableItems.length > 0 && viewableItems[0].index !== null) {
              setActivePresent(viewableItems[0].index);
            }
          }}
          viewabilityConfig={{ viewAreaCoveragePercentThreshold: 50 }}
        />
        </View>
        <View style={styles.present_indicator}>
          {slides.map((_, idx) => {
            if (idx === activePresent) {
              return (
                <View
                  key={idx}
                  style={[styles.present_indicator_base, styles.present_indicator_active]}
                />
              );
            } else {
              return (
                <View
                  key={idx}
                  style={[styles.present_indicator_base, styles.present_indicator_inactive]}
                />
              );
            }
          })}
        </View>
      </View>

      <View style={styles.summaryContainer}>
        <View style={styles.summary}>
          <Text style={[styles.summary_label, styles.summary_label_left]}>
            서울 광진구
          </Text>
          <Pressable
            style={[styles.summary_button, styles.summary_button_left, currentAQI_outdoor<=200 && {backgroundColor: '#FCDFD9'}, , currentAQI_outdoor<=150 && {backgroundColor: '#FCFCFC'}, , currentAQI_outdoor<=100 && {backgroundColor: '#D5FCD2'}, , currentAQI_outdoor<=50 && {backgroundColor: '#88FC7D'}]}
            android_ripple={{
              color: 'rgba(0,0,0,0.1)',
              radius: 103,
            }}
            onPress={() => navigation.jumpTo('Outdoor')}
          >
            <View style={styles.summary_button_labelBox}>
              <Text style={styles.summary_button_label}>
                실외
              </Text>
              <Image
                source={require('../assets/goto.png')}
                style={styles.gotoImage}
              />
            </View>
            <View style={styles.summary_button_stateBox}>
              <Text style={[styles.summary_state, currentAQI_outdoor<=200 && {color: '#FF4040'}, , currentAQI_outdoor<=150 && {color: '#8F8F8F'}, , currentAQI_outdoor<=100 && {color: '#24DA00'}, , currentAQI_outdoor<=50 && {color: '#00700D'}]}>
                {currentAQI_outdoor<=50 ? '매우좋음' : currentAQI_outdoor<=100 ? '좋음' : currentAQI_outdoor<=150 ? '보통' : currentAQI_outdoor<=200 ? '나쁨' : '매우나쁨'}
              </Text>
            </View>
            <View style={styles.summary_button_detailContainer}>
              <View style={styles.summary_button_detailBox}>
                <Text style={styles.summary_button_detail_label}>
                  PM2.5
                </Text>
                <View style={styles.summary_button_detail}>
                  <View style={[styles.summary_button_detail_state, currentData_outdoor.pm25<=35 && {backgroundColor: '#FCDFD9'}, currentData_outdoor.pm25<=25 && {backgroundColor: '#FCFCFC'}, currentData_outdoor.pm25<=15 && {backgroundColor: '#D5FCD2'}, currentData_outdoor.pm25<=5 && {backgroundColor: '#88FC7D'}]}></View>
                  <Text style={styles.summary_button_detail_value}>
                    {currentData_outdoor.pm25}
                  </Text>
                </View>
              </View>
              <View style={styles.summary_button_detailBox}>
                <Text style={styles.summary_button_detail_label}>
                  PM10
                </Text>
                <View style={styles.summary_button_detail}>
                  <View style={[styles.summary_button_detail_state, currentData_outdoor.pm100<=70 && {backgroundColor: '#FCDFD9'}, currentData_outdoor.pm100<=50 && {backgroundColor: '#FCFCFC'}, currentData_outdoor.pm100<=30 && {backgroundColor: '#D5FCD2'}, currentData_outdoor.pm100<=15 && {backgroundColor: '#88FC7D'}]}></View>
                  <Text style={styles.summary_button_detail_value}>
                    {currentData_outdoor.pm100}
                  </Text>
                </View>
              </View>
              <View style={styles.summary_button_detailBox}>
                <Text style={styles.summary_button_detail_label}>
                  O3
                </Text>
                <View style={styles.summary_button_detail}>
                  <View style={[styles.summary_button_detail_state, currentData_outdoor.o3<=100 && {backgroundColor: '#FCDFD9'}, currentData_outdoor.o3<=70 && {backgroundColor: '#FCFCFC'}, currentData_outdoor.o3<=60 && {backgroundColor: '#D5FCD2'}, currentData_outdoor.o3<=30 && {backgroundColor: '#88FC7D'}]}></View>
                  <Text style={styles.summary_button_detail_value}>
                    {currentData_outdoor.o3}
                  </Text>
                </View>
              </View>
              <View style={styles.summary_button_detailBox}>
                <Text style={styles.summary_button_detail_label}>
                  NO2
                </Text>
                <View style={styles.summary_button_detail}>
                  <View style={[styles.summary_button_detail_state, currentData_outdoor.no2<=40 && {backgroundColor: '#FCDFD9'}, currentData_outdoor.no2<=30 && {backgroundColor: '#FCFCFC'}, currentData_outdoor.no2<=20 && {backgroundColor: '#D5FCD2'}, currentData_outdoor.no2<=10 && {backgroundColor: '#88FC7D'}]}></View>
                  <Text style={styles.summary_button_detail_value}>
                    {currentData_outdoor.no2}
                  </Text>
                </View>
              </View>
            </View>
          </Pressable>
        </View>
        <View style={styles.summary}>
          <Text style={[styles.summary_label, styles.summary_label_right]}>
            우리 집1
          </Text>
          <Pressable
            style={[styles.summary_button, styles.summary_button_right, currentAQI_indoor<=200 && {backgroundColor: '#FCDFD9'}, , currentAQI_indoor<=150 && {backgroundColor: '#FCFCFC'}, , currentAQI_indoor<=100 && {backgroundColor: '#D5FCD2'}, , currentAQI_indoor<=50 && {backgroundColor: '#88FC7D'}]}
            android_ripple={{
              color: 'rgba(0,0,0,0.1)',
              radius: 103,
            }}
            onPress={() => navigation.jumpTo('Indoor')}
          >
            <View style={styles.summary_button_labelBox}>
              <Text style={styles.summary_button_label}>
                실내
              </Text>
              <Image
                source={require('../assets/goto.png')}
                style={styles.gotoImage}
              />
            </View>
            <View style={styles.summary_button_stateBox}>
              <Text style={[styles.summary_state, currentAQI_indoor<=200 && {color: '#FF4040'}, , currentAQI_indoor<=150 && {color: '#8F8F8F'}, , currentAQI_indoor<=100 && {color: '#24DA00'}, , currentAQI_indoor<=50 && {color: '#00700D'}]}>
                {currentAQI_indoor<=50 ? '매우좋음' : currentAQI_indoor<=100 ? '좋음' : currentAQI_indoor<=150 ? '보통' : currentAQI_indoor<=200 ? '나쁨' : '매우나쁨'}
              </Text>
            </View>
            <View style={styles.summary_button_detailContainer}>
              <View style={styles.summary_button_detailBox}>
                <Text style={styles.summary_button_detail_label}>
                  PM2.5
                </Text>
                <View style={styles.summary_button_detail}>
                  <View style={[styles.summary_button_detail_state, currentData_indoor.pm25<=35 && {backgroundColor: '#FCDFD9'}, currentData_indoor.pm25<=25 && {backgroundColor: '#FCFCFC'}, currentData_indoor.pm25<=15 && {backgroundColor: '#D5FCD2'}, currentData_indoor.pm25<=5 && {backgroundColor: '#88FC7D'}]}></View>
                  <Text style={styles.summary_button_detail_value}>
                    {currentData_indoor.pm25}
                  </Text>
                </View>
              </View>
              <View style={styles.summary_button_detailBox}>
                <Text style={styles.summary_button_detail_label}>
                  PM10
                </Text>
                <View style={styles.summary_button_detail}>
                  <View style={[styles.summary_button_detail_state, currentData_indoor.pm100<=70 && {backgroundColor: '#FCDFD9'}, currentData_indoor.pm100<=50 && {backgroundColor: '#FCFCFC'}, currentData_indoor.pm100<=30 && {backgroundColor: '#D5FCD2'}, currentData_indoor.pm100<=15 && {backgroundColor: '#88FC7D'}]}></View>
                  <Text style={styles.summary_button_detail_value}>
                    {currentData_indoor.pm100}
                  </Text>
                </View>
              </View>
              <View style={styles.summary_button_detailBox}>
                <Text style={styles.summary_button_detail_label}>
                  CO2
                </Text>
                <View style={styles.summary_button_detail}>
                  <View style={[styles.summary_button_detail_state, currentData_indoor.co2<=3000 && {backgroundColor: '#FCDFD9'}, currentData_indoor.co2<=2000 && {backgroundColor: '#FCFCFC'}, currentData_indoor.co2<=1000 && {backgroundColor: '#D5FCD2'}, currentData_indoor.co2<=400 && {backgroundColor: '#88FC7D'}]}></View>
                  <Text style={styles.summary_button_detail_value}>
                    {currentData_indoor.co2}
                  </Text>
                </View>
              </View>
              <View style={styles.summary_button_detailBox}>
                <Text style={styles.summary_button_detail_label}>
                  VOC
                </Text>
                <View style={styles.summary_button_detail}>
                  <View style={[styles.summary_button_detail_state, currentData_indoor.voc<=2000 && {backgroundColor: '#FCDFD9'}, currentData_indoor.voc<=1000 && {backgroundColor: '#FCFCFC'}, currentData_indoor.voc<=400 && {backgroundColor: '#D5FCD2'}, currentData_indoor.voc<=100 && {backgroundColor: '#88FC7D'}]}></View>
                  <Text style={styles.summary_button_detail_value}>
                    {currentData_indoor.voc}
                  </Text>
                </View>
              </View>
            </View>
          </Pressable>
        </View>
      </View>
      <View style={styles.forecastContainer}>
        <View style={styles.forecast}>
          <View style={styles.forecast_labelBox}>
            <Text style={styles.forecast_label}>
              예측 정보
            </Text>
            <Text style={styles.forecast_label_location}>
              우리 집1
            </Text>
            <Text style={styles.forecast_label_time}>
              {lastRefreshed}
            </Text>
            <Pressable
              style={styles.forecast_label_refresh}
              onPress={() => {fetchGraphData(); handleRefresh();}}
            >
              <Image
                source={require('../assets/refresh.png')}
                style={styles.forecast_label_refresh_image}
              />
            </Pressable>
          </View>
          <View  style={styles.indexBox}>
            {info_data.map((item, i) => (
              <Pressable
                key={i}
                style={[styles.index, activePrediction === i ? styles.index_active : styles.index_inactive]}
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
  presentContainer: {
    flex: 2.8,
  },
  summaryContainer: {
    flex: 2,
    flexDirection: 'row',
  },
  forecastContainer: {
    flex: 2.5,
    alignItems: 'center',
  },
  present: {
    flex: 8,
  },
  present_indicator: {
    flex: 2,
    flexDirection: 'row',
    justifyContent: 'center',
  },
  present_list: {
  },
  present_container: {
    width: SCREEN_WIDTH,
    flexDirection: 'row',
    justifyContent: 'center',
  },
  present_textBox: {
    width: SCREEN_WIDTH - 175,
    marginLeft: 16,
    justifyContent: 'center',
    alignItems: 'center',
  },
  present_text_temperture: {
    fontSize: 70,
    fontWeight: 800,
    color: '#FDB259',
  },
  present_text_compare: {
    fontSize: 24,
    fontWeight: 700,
    width: 200,
    color: '#515253',
  },
  present_text_energy: {
    fontSize: 27,
    fontWeight: 800,
    color: '#009750',
  },
  present_image: {
    width: 60,
    height: 60,
    marginBottom: 10,
    marginLeft: -15,
    resizeMode: 'contain',
  },
  present_indicator_base: {
    width: 10,
    height: 10,
    borderRadius: 10,
    marginHorizontal: 5,
  },
  present_indicator_active: { 
    backgroundColor: '#000000' 
  },
  present_indicator_inactive: {
    backgroundColor: '#AAAAAA',
  },
  summary: {
    width: '50%',
  },
  summary_label: {
    fontSize: 15,
    fontWeight: 500,
  },
  summary_label_left: {
    marginHorizontal: '16%',
  },
  summary_label_right: {
    marginHorizontal: '10%',
  },
  summary_button: {
    width: '86%',
    aspectRatio: 1,
    borderRadius: 15,
    backgroundColor: '#FCA596',
    elevation: 4,
  },
  summary_button_left: {
    marginLeft: '10%',
    marginRight: '4%',
  },
    summary_button_right: {
    marginLeft: '4%',
    marginRight: '10%',
  },

  summary_button_labelBox: {
    alignItems: 'center',
    flex: 2,
    flexDirection: 'row',
    justifyContent: 'space-between'
  },
  summary_button_stateBox: {
    flex: 3,
    alignItems: 'center',
  },
  summary_button_detailContainer: {
    flex: 2,
    flexDirection: 'row',
    marginLeft: '5%',
  },
  summary_button_label: {
    fontSize: 18,
    fontWeight: 700,
    marginHorizontal: '10%',
  },
  gotoImage: {
    width: 20,
    height: 20,
    resizeMode: 'contain',
    marginHorizontal: '8%',
  },
  summary_state: {
    color: '#780000',
    fontSize: 40,
    fontWeight: 600,
  },
  summary_button_detailBox: {
    width: '25%',
    alignItems: 'center',
  },
  summary_button_detail_label: {
    color: '#AAAAAA',
    fontSize: 12,
    fontWeight: 600,
    textAlign: 'center',
  },
  summary_button_detail: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  summary_button_detail_state: {
    borderColor: '#777777',
    borderWidth: 0.5,
    backgroundColor: '#FCA596',
    width: 7,
    height: 7,
    borderRadius: 100,
    marginRight: 3,
  },
  summary_button_detail_value: {
    fontSize: 13,
    color: '#626262',
  },

  forecast: {
    width: '90%',
    height: '90%',
    borderRadius: 15,
    backgroundColor: '#FFFFFF',
    elevation: 4,
    marginTop: 5,
  },
  forecast_labelBox: {
    height: 45,
    flexDirection: 'row',
    alignItems: 'center',
  },
  forecast_label: {
    fontSize: 19,
    fontWeight: 700,
    marginLeft: '5%',
  },
  forecast_label_location: {
    color: '#777777',
    fontSize: 16,
    fontWeight: 500,
    marginLeft: '2%',
  },
  forecast_label_time: {
    color: '#777777',
    fontSize: 14,
    fontWeight: 500,
    marginLeft: 75,
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
    marginHorizontal: '1%',
    marginBottom: '1%',
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

export default HomeScreen;
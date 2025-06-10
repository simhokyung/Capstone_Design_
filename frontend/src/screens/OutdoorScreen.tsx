import React, { useState, useEffect } from 'react';
import { View, Image, Pressable, Text, StyleSheet, ScrollView, ImageBackground } from 'react-native';
import SelectDropdown from 'react-native-select-dropdown';
import axios from 'axios';
import { Graph24 } from '../components/Graph24'

type DataPoint = { x: number; y: number };

type AirQualityData = {
  pm25: DataPoint[];
  pm100: DataPoint[];
  o3: DataPoint[];
  no2: DataPoint[];
};

const districts = [
  '서울 강동구', '성남 분당구', '서울 광진구', '고양 일산구', '서울 영등포'
]
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
    key: 'O3',
    title: '오존',
    data:'오존은 대기 중 배출된 질소산화물(NOx)과 휘발성유기화합물(VOCs)이 자외선에 의해 광화학반응을 거쳐 생성되는 2차 오염물질입니다. 고농도로 노출되면 호흡기 점막을 자극해 기침·흉통·호흡 곤란을 유발하고, 폐 기능 저하 및 기관지 과민반응을 악화시킬 수 있습니다.',
    unit: 'ppm',
    threshold: [0, 30, 60, 70, 100],
  }, 
  {
    key: 'NO2',
    title: '이산화질소',
    data:'이산화질소는 자동차 배기가스나 발전소 연소 과정에서 주로 배출되는 1차 오염물질로, 황갈색 가스 형태로 존재합니다. 단기 노출 시 눈·코·목 점막을 자극해 염증을 일으키며, 장기 노출 시 만성 기관지염과 폐 기능 저하를 초래할 수 있습니다.',
    unit: 'ppm',
    threshold: [0, 10, 20, 30, 40],
  }
]

export const OutdoorScreen = () => {
  const [selectedDistrict, setSelectedDistrict] = useState<string>(districts[2]);
  const [currentData, setCurrentData] = useState({
      pm25: 0,
      pm100: 0,
      o3: 0,
      no2: 0,
    });
  const [currentAQI, setCurrentAQI] = useState(0);
  const [currentGraphData, setcurrentGraphData] = useState<AirQualityData>({
    pm25: [],
    pm100: [],
    o3: [],
    no2: [],
  });

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
  const [activePrediction_24, setActivePrediction_24] = useState(0);
  const activeItem = info_data[activeInfo];

  function calculateAQI(pm25: number, pm100: number, o3: number, no2: number): number {
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

  const fetchData = async () => {
    const userID = 1;
    try {
      const response = await axios.get(`${API_BASE}/users/${userID}/regions/forecast`);
      const districts_index = districts.findIndex((item) => selectedDistrict===item);
      setCurrentData({
        pm25: Math.round(response.data[districts_index].forecast[0].pm25),
        pm100: Math.round(response.data[districts_index].forecast[0].pm10),
        o3: Math.round(response.data[districts_index].forecast[0].o3),
        no2: Math.round(response.data[districts_index].forecast[0].no2),
      })
      
    } catch (e) {
    console.error('에러:', e);
    }
  };

  const transformToGraphData = (responseData: any[]): AirQualityData => {
    return {
      pm25: responseData.map((item, index) => ({ x: index + 1, y: Math.max(Math.min(item.pm25, 49), 1) })),
      pm100: responseData.map((item, index) => ({ x: index + 1, y: Math.max(Math.min(item.pm10, 99), 1) })),
      o3: responseData.map((item, index) => ({ x: index + 1, y: Math.max(Math.min(item.o3, 129), 1) })),
      no2: responseData.map((item, index) => ({ x: index + 1, y: Math.max(Math.min(item.no2, 49), 1) })),
    };
  };

  const fetchGraphData = async () => {
    const userID = 1;
    const districts_index = districts.findIndex((item) => selectedDistrict===item);
    try {
      const response = await axios.get(`${API_BASE}/users/${userID}/regions/forecast`);
      const formattedData = transformToGraphData(response.data[districts_index].forecast);
      setcurrentGraphData(formattedData);
      
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
    fetchData();
    fetchGraphData();
    handleRefresh();
  }, [selectedDistrict]);

  useEffect(() => {
    setCurrentAQI(calculateAQI(currentData.pm25, currentData.pm100, currentData.o3, currentData.no2));
  }, [currentData]);

  return (
    <View style={styles.container}>
      <View style={styles.locationContainer}>
        <Text style={styles.location_label}>
          실외
        </Text>
        <SelectDropdown
          data={districts}
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
                      O3
                    </Text>
                    <View style={styles.summary_detail}>
                      <View style={[styles.summary_detail_state, currentData.o3<=100 && {backgroundColor: '#FCDFD9'}, currentData.o3<=70 && {backgroundColor: '#FCFCFC'}, currentData.o3<=60 && {backgroundColor: '#D5FCD2'}, currentData.o3<=30 && {backgroundColor: '#88FC7D'}]}></View>
                      <Text style={styles.summary_detail_value}>
                        {currentData.o3}
                      </Text>
                    </View>
                  </View>
                  <View style={styles.summary_detailBox}>
                    <Text style={styles.summary_detail_label}>
                      NO2
                    </Text>
                    <View style={styles.summary_detail}>
                      <View style={[styles.summary_detail_state, currentData.no2<=40 && {backgroundColor: '#FCDFD9'}, currentData.no2<=30 && {backgroundColor: '#FCFCFC'}, currentData.no2<=20 && {backgroundColor: '#D5FCD2'}, currentData.no2<=10 && {backgroundColor: '#88FC7D'}]}></View>
                      <Text style={styles.summary_detail_value}>
                        {currentData.no2}
                      </Text>
                    </View>
                  </View>

                </View>
              </View>
            </View>
          </View>
          <View style={styles.forecast}>
            <View style={styles.forecast_labelBox}>
              <Text style={styles.forecast_label}>
                예측 정보
              </Text>
              <Text style={styles.content_label_hour}>
                24시간
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
                  style={[styles.forecast_index, activePrediction_24 === i ? styles.index_active : styles.index_inactive]}
                  onPress={() => setActivePrediction_24(i)}
                >
                  <Text style={styles.index_label_base}>
                    {item.key}
                  </Text>
                </Pressable>
              ))}
            </View>
            <View style={styles.chart}>
              {(() => {
                switch (activePrediction_24) {
                  case 0:
                    return <Graph24 data={currentGraphData['pm25']} type='pm25'></Graph24>;
                  case 1:
                    return <Graph24 data={currentGraphData['pm100']} type='pm100'></Graph24>;
                  case 2:
                    return <Graph24 data={currentGraphData['o3']} type='o3'></Graph24>;
                  case 3:
                    return <Graph24 data={currentGraphData['no2']} type='no2'></Graph24>;
                  default:
                    return null;
                }
              })()}
            </View>
          </View>
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
  select_label: {
    width: 90,
    fontSize: 18,
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
  forecast: {
    backgroundColor: '#FFFFFF',
    width: '90%',
    height: 210,
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
    width: 130,
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
  forecast_index: {
    width: 70,
    height: 25,
    backgroundColor: '#DFDFDF',
    justifyContent: 'center',
    borderRadius: 100,
    borderColor: '#DFDFDF',
    borderWidth: 1,
    alignItems: 'center',
    marginHorizontal: '1%',
    marginBottom: '1.5%',
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
});

export default OutdoorScreen;
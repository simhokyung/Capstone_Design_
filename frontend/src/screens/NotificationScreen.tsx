import React, { useRef, useState, useEffect } from 'react';
import { View, Image, Pressable, Text, StyleSheet, ScrollView, Animated, Alert } from 'react-native';
import axios from 'axios';

const API_BASE = 'http://18.191.176.79:8080';

type NotificationType = {
  key: string;
  current: boolean;
  type: 'notification' | 'alert';
  title: string;
  date: string;
  content: string;
};

export const NotificationScreen = () => {
  const [activeTab, setActiveTab] = useState(0);
  const [notifications, setNotifications] = useState<NotificationType[]>([]);
  const underlineX = useRef(new Animated.Value(32)).current;

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    try {
      const response = await axios.get<NotificationType[]>(`${API_BASE}/notifications`);
      setNotifications(response.data);
    } catch (error: any) {
      console.error('Fetch notifications error:', error);
      Alert.alert('오류', '알림을 불러오는 중 오류가 발생했습니다.');
    }
  };

  const markAsRead = async (key: string) => {
    try {
      await axios.patch(`${API_BASE}/notifications/${key}`, { current: false });
      setNotifications(prev =>
        prev.map(item =>
          item.key === key ? { ...item, current: false } : item
        )
      );
    } catch (error: any) {
      console.error('Mark read error:', error);
      Alert.alert('오류', '알림 읽음 처리 중 오류가 발생했습니다.');
    }
  };

  const markAllAsRead = async () => {
    try {
      await axios.post(`${API_BASE}/notifications/read-all`);
      setNotifications(prev => prev.map(item => ({ ...item, current: false })));
    } catch (error: any) {
      console.error('Mark all read error:', error);
      Alert.alert('오류', '모두 읽음 처리 중 오류가 발생했습니다.');
    }
  };

  const onPressTab = (index: number) => {
    setActiveTab(index);
    Animated.timing(underlineX, {
      toValue: index * 158 + 32,
      duration: 200,
      useNativeDriver: true,
    }).start();
  };

  const allCurrentFalse = notifications.every(item => item.current === false);

  return (
    <View style={styles.container}>
      <View style={styles.tabContainer}>
        <View style={styles.tab}>
          {['현재 알림', '이전 알림'].map((label, i) => (
            <Pressable
              key={i}
              style={styles.tab_button}
              onPress={() => onPressTab(i)}
              android_ripple={{ color: 'rgba(0,0,0,0.1)', radius: 60 }}
            >
              <Text
                style={[
                  styles.tab_label_base,
                  activeTab === i ? styles.tab_label_active : styles.tab_label_inactive,
                ]}
              >
                {label}
              </Text>
            </Pressable>
          ))}
        </View>
        <Animated.View
          style={[
            styles.underline,
            { transform: [{ translateX: underlineX }] },
          ]}
        />
      </View>

      <View style={styles.listContainer}>
        <ScrollView style={styles.list} contentContainerStyle={styles.listContent}>
          {notifications.map(({ key, current, type, title, date, content }) => {
            if (activeTab === 1 || current === true) {
              return (
                <View
                  style={[
                    styles.list_box_base,
                    type === 'notification'
                      ? styles.list_box_notification
                      : styles.list_box_alert,
                  ]}
                  key={key}
                >
                  <View style={styles.list_textBox}>
                    <View style={styles.list_titleBox}>
                      <Text style={styles.list_title}>{title}</Text>
                      {activeTab === 0 && (
                        <Pressable
                          style={styles.list_cancelButton}
                          onPress={() => markAsRead(key)}
                          android_ripple={{ color: 'rgba(0,0,0,0.1)', radius: 15 }}
                        >
                          <Image
                            source={require('../assets/cancel.png')}
                            style={styles.list_cancel}
                          />
                        </Pressable>
                      )}
                    </View>
                    <Text style={styles.list_date}>{date}</Text>
                    <Text style={styles.list_content}>{content}</Text>
                  </View>
                </View>
              );
            }
          })}

          {activeTab === 0 && allCurrentFalse && (
            <Image
              source={require('../assets/check.png')}
              style={styles.list_empty}
            />
          )}
        </ScrollView>

        {activeTab === 0 && !allCurrentFalse && (
          <Pressable
            style={styles.list_cancelAll}
            onPress={markAllAsRead}
            android_ripple={{ color: 'rgba(0,0,0,0.1)', radius: 200 }}
          >
            <Text style={styles.list_cancelAll_label}>모두 읽음 처리하기</Text>
          </Pressable>
        )}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F6FAFF' },
  tabContainer: { flex: 0.7 },
  listContainer: { flex: 6.3, alignItems: 'center' },
  tab: { flexDirection: 'row', justifyContent: 'center' },
  tab_button: { width: '40%', height: '100%', justifyContent: 'center', alignItems: 'center', marginHorizontal: '2%' },
  tab_label_base: { color: '#AAAAAA', fontSize: 26, fontWeight: '700', textAlign: 'center' },
  tab_label_active: { color: '#01B3EA' },
  tab_label_inactive: { color: '#AAAAAA' },
  underline: { position: 'absolute', bottom: 10, width: 140, height: 5, borderRadius: 100, backgroundColor: '#01B3EA' },
  list: { width: '100%' },
  listContent: { alignItems: 'center', flexDirection: 'column-reverse' },
  list_box_base: { backgroundColor: '#FFFFFF', width: '90%', borderRadius: 15, elevation: 4, marginVertical: 8, alignItems: 'center' },
  list_box_notification: { backgroundColor: '#FFFFFF' },
  list_box_alert: { backgroundColor: '#DDDDDD' },
  list_textBox: { width: '90%', marginVertical: 15 },
  list_titleBox: { flexDirection: 'row' },
  list_title: { fontSize: 24, fontWeight: '600', width: '90%' },
  list_cancelButton: { width: '10%', aspectRatio: 1, justifyContent: 'center', alignItems: 'center' },
  list_cancel: { width: '55%', height: '55%', resizeMode: 'contain' },
  list_date: { fontSize: 15, color: '#AAAAAA' },
  list_content: { fontSize: 16, fontWeight: '500', marginTop: 10 },
  list_empty: { width: '65%', height: 500, resizeMode: 'contain' },
  list_cancelAll: { position: 'absolute', bottom: '4%', backgroundColor: '#01B3EA', width: '95%', height: 45, borderRadius: 100, justifyContent: 'center', alignItems: 'center', elevation: 4 },
  list_cancelAll_label: { fontSize: 20, fontWeight: '600', color: '#FFFFFF' },
});

export default NotificationScreen;

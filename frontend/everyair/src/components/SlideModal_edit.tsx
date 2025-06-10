import React, { useState, useRef } from 'react';
import { View, Text, TextInput, StyleSheet, ScrollView, Pressable, Image, PanResponder, GestureResponderEvent, PanResponderGestureState } from 'react-native';
import Modal from 'react-native-modal';


type SlideModalProps = {
  isVisible: boolean;
  onClose: () => void;
  children?: React.ReactNode;
};

// 방(Rect) 타입 정의
type Room = {
  key: number;
  name: string;
  x: number;
  y: number;
  width: number;
  height: number;
};

type Device = {
  key: number;
  name: string;
  x: number;
  y: number;
  type: string;
};

type Drawing_room = {
  x0: number;
  y0: number;
  x1: number;
  y1: number;
};

type Drawing_device = {
  x: number;
  y: number;
};

let cnt_room=0;
let cnt_device=0;

export default function SlideModal_edit({ isVisible, onClose, children }: SlideModalProps) {
  const [rooms, setRooms] = useState<Room[]>([]);
  const [drawing_room, setDrawing_room] = useState<Drawing_room | null>(null);
  const [nameModalVisible, setNameModalVisible] = useState(false);
  const [roomName, setRoomName] = useState('');
  const [editingKey_room, setEditingKey_room] = useState(0);
  const [isIndoor, setIsIndoor] = useState(true);

  const nameRoom = () => {
    setRooms(r => r.map(room => room.key === editingKey_room ? { ...room, name: roomName || '이름 없음' } : room));
    setRoomName('');
    setNameModalVisible(false);
  };
  const handleDelete_room = (key: number) => {
    setRooms(prev => prev.filter(r => r.key !== key));
  };

  const panResponder_room = useRef(
    PanResponder.create({
      onStartShouldSetPanResponder: () => true,
      onMoveShouldSetPanResponder: () => true,

      onPanResponderGrant: (e: GestureResponderEvent) => {
        const { pageX, pageY } = e.nativeEvent;
        const x = Math.min(Math.max(Math.round(pageX - 31), 0), 300);
        const y = Math.min(Math.max(Math.round(pageY - 196), 0), 300);
        setDrawing_room({ x0: x, y0: y, x1: x, y1: y });
      },
      onPanResponderMove: (e: GestureResponderEvent) => {
        const { pageX, pageY } = e.nativeEvent;
        const x = Math.min(Math.max(Math.round(pageX - 31), 0), 300);
        const y = Math.min(Math.max(Math.round(pageY - 196), 0), 300);
        setDrawing_room(prev => prev ? { x0: prev.x0, y0: prev.y0, x1: x, y1: y } : null);
      },
      onPanResponderRelease: () => {
        setDrawing_room(prev => {
          if (!prev) return null;
          const { x0, y0, x1, y1 } = prev;
          const newRoom: Room = {
            key: cnt_room,
            name: '',
            x: Math.min(x0, x1),
            y: Math.min(y0, y1),
            width: Math.abs(x1 - x0),
            height: Math.abs(y1 - y0),
          };
          setRooms(r => [...r, newRoom]);
          setEditingKey_room(cnt_room)
          cnt_room++;
          setNameModalVisible(true);
          return null;
        });
      },
      onPanResponderTerminate: () => {
        setDrawing_room(null);
      }
    })
  ).current;

  const [devices, setDevices] = useState<Device[]>([]);
  const [drawing_device, setDrawing_device] = useState<Drawing_device | null>(null);
  const [deviceName, setDeviceName] = useState('');
  const [deviceType, setDeviceType] = useState('sensor');
  const [editingKey_device, setEditingKey_device] = useState(0);

  const nameDevice = () => {
    setDevices(d => d.map(device => device.key === editingKey_device ? { ...device, name: deviceName, type: deviceType || '이름 없음' } : device));
    setDeviceName('');
    setNameModalVisible(false);
  };
  const handleDelete_device = (key: number) => {
    setDevices(prev => prev.filter(r => r.key !== key));
  };

  const panResponder_device = useRef(
    PanResponder.create({
      onStartShouldSetPanResponder: () => true,
      onMoveShouldSetPanResponder: () => true,

      onPanResponderGrant: (e: GestureResponderEvent) => {
        const { pageX, pageY } = e.nativeEvent;
        const x = Math.min(Math.max(Math.round(pageX - 31), 0), 300);
        const y = Math.min(Math.max(Math.round(pageY - 196), 0), 300);
        setDrawing_device({ x: x, y: y });
      },
      onPanResponderMove: (e: GestureResponderEvent) => {
        const { pageX, pageY } = e.nativeEvent;
        const x = Math.min(Math.max(Math.round(pageX - 31), 0), 300);
        const y = Math.min(Math.max(Math.round(pageY - 196), 0), 300);
        setDrawing_device({ x: x, y: y });
      },
      onPanResponderRelease: () => {
        setDrawing_device(prev => {
          if (!prev) return null;
          const { x, y } = prev;
          const newDevice: Device = {
            key: cnt_device,
            name: '',
            x: x,
            y: y,
            type: 'sensor'
          };
          setDevices(d => [...d, newDevice]);
          setEditingKey_device(cnt_device)
          cnt_device++;
          setNameModalVisible(true);
          return null;
        });
      },
      onPanResponderTerminate: () => {
        setDrawing_device(null);
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
            실내 구조 및 기기 관리
          </Text>
          <View style={styles.content}>
            <View style={styles.indoor}>
              <Pressable
                style={[styles.indoorButton, isIndoor ? styles.indoor_active : styles.indoor_inactive]}
                android_ripple={{
                  color: 'rgba(0,0,0,0.1)',
                  radius: 74,
                }}
                onPress={() =>setIsIndoor(true)}
              >
                <Text style={[styles.indoor_label, isIndoor ? styles.indoor_label_active : styles.indoor_label_inactive]}>
                  실내 구조 관리
                </Text>
              </Pressable>
              <Pressable
                style={[styles.indoorButton, isIndoor ? styles.indoor_inactive : styles.indoor_active]}
                android_ripple={{
                  color: 'rgba(0,0,0,0.1)',
                  radius: 74,
                }}
                onPress={() =>setIsIndoor(false)}
              >
                <Text style={[styles.indoor_label, isIndoor ? styles.indoor_label_inactive : styles.indoor_label_active]}>
                  기기 관리
                </Text>
              </Pressable>
            </View>
            {isIndoor && <>
              <View 
                style={styles.visualization_content}
                {...panResponder_room.panHandlers}
              >
                {rooms.map(r => (
                  <View
                    key={r.key}
                    style={[styles.roomRect, {
                      left: r.x,
                      top: r.y,
                      width: r.width,
                      height: r.height,
                    }]}
                  >
                    <Text style={styles.room_label}>{r.name}</Text>
                  </View>
                ))}
                {drawing_room && (
                  <View
                    style={[styles.roomRectDrawing, {
                      left: Math.min(drawing_room.x0, drawing_room.x1),
                      top: Math.min(drawing_room.y0, drawing_room.y1),
                      width: Math.abs(drawing_room.x1 - drawing_room.x0),
                      height: Math.abs(drawing_room.y1 - drawing_room.y0),
                    }]}
                  />
                )}
              </View>
              <Modal 
                isVisible={nameModalVisible}
                onBackdropPress={() => setNameModalVisible(false)}
                onBackButtonPress={() => setNameModalVisible(false)}
                backdropOpacity={0.3}
              >
                <View style={styles.nameModal}>
                  <Text style={styles.nameLabel}>방 이름을 입력하세요</Text>
                  <TextInput
                    style={styles.nameInput}
                    placeholder="거실"
                    placeholderTextColor={'#BBBBBB'}
                    value={roomName}
                    onChangeText={setRoomName}
                  />
                  <Pressable
                    style={styles.nameConfirmButton}
                    onPress={nameRoom}
                  >
                    <Text style={styles.confirmText}>확인</Text>
                  </Pressable>
                </View>
              </Modal>
              <ScrollView style={styles.list} contentContainerStyle={styles.listContent}>
                {rooms.map((item, i) => {
                  return (
                    <View key={item.key} style={styles.roomBox}>
                      <View style={styles.titleBox}>
                        <Text style={styles.roomName}>
                          {item.name}
                        </Text>
                        <Pressable
                          style={styles.cancelButton}
                          android_ripple={{
                            color: 'rgba(0,0,0,0.1)',
                            radius: 15,
                          }}
                          onPress={() => handleDelete_room(item.key)}
                        >
                          <Image
                            source={require('../assets/cancel.png')}
                            style={styles.cancel}
                          />
                        </Pressable>
                      </View>
                      
                      <Text style={styles.roomDetail}>
                        위치: {item.x}, {item.y}
                      </Text>
                      <Text style={styles.roomDetail}>
                        크기: {item.width}, {item.height}
                      </Text>

                    </View>
                  )
                })}
              </ScrollView>
              <Pressable
                style={styles.confirmButton}
                android_ripple={{
                  color: 'rgba(0,0,0,0.1)',
                  radius: 40,
                }}
                onPress={onClose}
              >
                <Text style={styles.confirmButton_label}>
                  수정 내용 저장하기
                </Text>
              </Pressable>
            </>}
            {/* ----------------------------------------------------------------------- */}
            {!isIndoor && <>
              <View 
                style={styles.visualization_content}
                {...panResponder_device.panHandlers}
              >
                {rooms.map(r => (
                  <View
                    key={r.key}
                    style={[styles.roomRect, {
                      left: r.x,
                      top: r.y,
                      width: r.width,
                      height: r.height,
                    }]}
                  >
                    <Text style={styles.room_label}>{r.name}</Text>
                  </View>
                ))}
                {devices.map(d => (
                  <View
                    key={d.key}
                    style={[
                      styles.deviceMarker,
                      { left: d.x, top: d.y },
                    ]}
                  >
                    <Text>{d.name}</Text>
                  </View>
                ))}

              </View>
              <Modal 
                isVisible={nameModalVisible}
                onBackdropPress={() => setNameModalVisible(false)}
                onBackButtonPress={() => setNameModalVisible(false)}
                backdropOpacity={0.3}
              >
                <View style={styles.nameModal}>
                  <Text style={styles.nameLabel}>기기 이름을 입력하세요</Text>
                  <TextInput
                    style={styles.nameInput}
                    placeholder="공기청정기1"
                    placeholderTextColor={'#BBBBBB'}
                    value={deviceName}
                    onChangeText={setDeviceName}
                  />

                  <Pressable
                    style={styles.nameConfirmButton}
                    onPress={nameDevice}
                  >
                    <Text style={styles.confirmText}>확인</Text>
                  </Pressable>
                </View>
              </Modal>
              <ScrollView style={styles.list} contentContainerStyle={styles.listContent}>
                {devices.map((item, i) => {
                  return (
                    <View key={item.key} style={styles.roomBox}>
                      <View style={styles.titleBox}>
                        <Text style={styles.roomName}>
                          {item.name}
                        </Text>
                        <Pressable
                          style={styles.cancelButton}
                          android_ripple={{
                            color: 'rgba(0,0,0,0.1)',
                            radius: 15,
                          }}
                          onPress={() => handleDelete_device(item.key)}
                        >
                          <Image
                            source={require('../assets/cancel.png')}
                            style={styles.cancel}
                          />
                        </Pressable>
                      </View>
                      
                      <Text style={styles.roomDetail}>
                        위치: {item.x}, {item.y}
                      </Text>
                      <Text style={styles.roomDetail}>
                        종류: {item.type}
                      </Text>

                    </View>
                  )
                })}
              </ScrollView>
              <Pressable
                style={styles.confirmButton}
                android_ripple={{
                  color: 'rgba(0,0,0,0.1)',
                  radius: 40,
                }}
                onPress={onClose}
              >
                <Text style={styles.confirmButton_label}>
                  수정 내용 저장하기
                </Text>
              </Pressable>
            </>}
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
    backgroundColor: '#F5F9FD',
    alignSelf: 'center',
    height: 301,
    width: 300,
    marginTop: 10,
    marginBottom: 10,
    borderWidth: 1,
  },

  list: {
    width: '100%',
    height: 367,
  },
  listContent: {
    alignItems: 'center',
    paddingBottom: 160,
  },

  roomRect: {
    position: 'absolute',
    borderWidth: 1,
    borderColor: 'blue',
    backgroundColor: 'rgba(0,0,255,0.1)',
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
  roomRectDrawing: {
    position: 'absolute',
    borderWidth: 1,
    borderColor: 'gray',
    backgroundColor: 'rgba(100,100,100,0.2)',
  },

  roomBox: {
    backgroundColor: '#F5F9FD',
    width: '90%',
    height: 110,
    borderRadius: 15,
    marginVertical: 7,
    elevation: 4,
    justifyContent: 'center',
  },
  titleBox: {
    height: 50,
    alignItems: 'center',
    flexDirection: 'row',
  },
  roomName: {
    marginHorizontal: 20,
    marginVertical: 5,
    width: 240,
    fontSize: 26,
    fontWeight: 700,
  },
  cancelButton: {
    width: '10%',
    aspectRatio: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  cancel: {
    width: '55%',
    height: '55%',
    resizeMode: 'contain',
  },
  roomDetail: {
    marginHorizontal: 20,
    fontSize: 18,
    fontWeight: 600,
  },
  nameModal: { 
    backgroundColor: '#fff', 
    borderRadius: 8, 
    padding: 16, 
    alignItems: 'center' 
  },
  nameLabel: { 
    fontSize: 18, 
    marginBottom: 8 
  },
  nameInput: { 
    width: '100%', 
    borderWidth: 1, 
    borderColor: '#ccc', 
    borderRadius: 4, 
    padding: 8, 
    marginBottom: 12 
  },

  nameConfirmButton: { 
    backgroundColor: '#007AFF', 
    borderRadius: 4, 
    paddingVertical: 10, 
    paddingHorizontal: 20 
  },
  confirmText: { 
    color: '#fff', 
    fontSize: 16 
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
    position: 'absolute',
    left: '5%',
    top: '80%',
  },
  confirmButton_label: {
    fontSize: 20,
    fontWeight: 600,
    color: '#FFFFFF'
  },

  indoor: {
    flexDirection: 'row',
  },
  indoorButton: {
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
  indoor_active: {
    backgroundColor: '#01B3EA',
  },
  indoor_inactive: {
    backgroundColor: '#D9D9D9',
  },
  indoor_label: {
    fontSize: 23,
    fontWeight: 700,
    color: '#FFFFFF'
  },
  indoor_label_active: {
    color: '#FFFFFF'
  },
  indoor_label_inactive: {
    color: '#000000'
  },

  deviceMarker: {
    width: 10,
    height: 10,
    borderRadius: 100,
    backgroundColor: '#FFFFFF',
  }
});

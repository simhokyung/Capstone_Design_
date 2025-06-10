
import os, glob, json
import numpy as np
import joblib
from tensorflow.keras.models import load_model
from datetime import datetime, timezone
import requests
import time
import random

# 설정
DATA_DIR = "/home/ubuntu/data_raw"
TARGET_COLS = ['voc', 'temperature', 'humidity', 'co2', 'pm25_t', 'pm100_t']
#SENSOR_IDS = [44212, 44213,48005,48007]
SENSOR_IDS = [48007, 44213]
BACKEND_URL = "http://18.191.176.79:8080/ai/predictions"
SAVE_DIR = "predicted_json"
os.makedirs(SAVE_DIR, exist_ok=True)

# 팬 모드 one-hot 인코딩
def encode_fan_mode(mode: str) -> list:
    modes = ['max', 'medium', 'sleep', 'smart', 'windfree']
    return [1 if mode == m else 0 for m in modes]

# 모델 및 스케일러 로딩
def load_files_for_sensor(sensor_id):
    return {
        "model": load_model(f"lstm_model_{sensor_id}_great.keras"),
        "x_scaler": joblib.load(f"lstm_scaler_{sensor_id}_great.pkl")
    }

# 최근 데이터 수집 + deviceId 추출
def collect_latest_data(sensor_id):
    files = sorted(glob.glob(os.path.join(DATA_DIR, "data_*.json")), reverse=True)
    buffer = []
    device_id = None

    for file in files:
        if len(buffer) >= 60:
            break
        try:
            with open(file) as f:
                data = json.load(f)
                statuses = data.get("statuses", [])
                if not statuses:
                    continue
                status = statuses[0]
                device_id = status.get("deviceId", f"unknown-{sensor_id}")

                for m in data.get("measurements", []):
                    if m.get("sensorId") == sensor_id:
                        # ✅ timestamp 정수형 처리 (ms → s 변환 후 datetime)
                        raw_ts = m.get("timestamp", status.get("statusAt", None))
                        if isinstance(raw_ts, (int, float)):
                            dt = datetime.fromtimestamp(raw_ts / 1000.0)
                        elif isinstance(raw_ts, str):
                            dt = datetime.fromisoformat(raw_ts.replace("Z", "+00:00"))
                        else:
                            continue

                        hour = dt.hour
                        dow = dt.weekday()
                        hour_sin = np.sin(2 * np.pi * hour / 24)
                        hour_cos = np.cos(2 * np.pi * hour / 24)
                        dow_sin = np.sin(2 * np.pi * dow / 7)
                        dow_cos = np.cos(2 * np.pi * dow / 7)

                        row = [
                            m.get("temperature", 0),
                            m.get("humidity", 0),
                            1 if m.get("ventilation") == "O" else 0,
                            *encode_fan_mode(status.get("fanMode", "")),
                            1 if status.get("powerState") == "on" else 0,
                            hour_sin, hour_cos, dow_sin, dow_cos,
                            m.get("co2", 0),
                            m.get("voc", 0),
                            m.get("pm25_t", 0),
                            m.get("pm100_t", 0),
                            status.get("energy", 0),
                        ]
                        buffer.append(row)
                        break
        except Exception as e:
            print(f"[⚠️ 경고] {file} 처리 중 오류: {e}")
            continue

    return (np.array(buffer[::-1]), device_id) if len(buffer) >= 60 else (None, None)

# 예측 및 전송 함수
def predict_all_sensors_and_send():
    result = {
        "timestamp": datetime.now(timezone.utc).isoformat(timespec='seconds').replace('+00:00', 'Z'),
        "predictions": []
    }

    for sensor_id in SENSOR_IDS:
        files = load_files_for_sensor(sensor_id)
        X, device_id = collect_latest_data(sensor_id)
        if X is None:
            print(f"❌ Sensor {sensor_id}: 데이터 부족")
            continue

        X_scaled = files["x_scaler"].transform(X).reshape(1, 60, -1)
        y_scaled = files["model"].predict(X_scaled)[0]  # (60, 6)
        y = files["x_scaler"].inverse_transform(
            np.hstack([y_scaled, np.zeros((60, X.shape[1] - len(TARGET_COLS)))])
        )[:, :len(TARGET_COLS)]

        result["predictions"].append({
            "sensorId": sensor_id,
            "deviceId": device_id,
            "prediction": {col: y[:, i].round(3).tolist() for i, col in enumerate(TARGET_COLS)}
        })

    # 저장
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    filename = os.path.join(SAVE_DIR, f"predict_all_{timestamp}.json")
    with open(filename, "w") as f:
        json.dump(result, f, indent=2)
    print(f"✅ 예측 결과 저장 완료 → {filename}")

    # 전송
    try:
        res = requests.post(BACKEND_URL, json=result, timeout=10)
        print(f"✅ 전송 성공: {res.status_code}")
    except Exception as e:
        print("❌ 예측 전송 실패:", e)

# 1분마다 실행
if __name__ == "__main__":
    while True:
        predict_all_sensors_and_send()
        time.sleep(60)


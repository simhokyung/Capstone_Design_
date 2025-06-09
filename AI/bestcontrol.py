import threading 
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
import os, glob, json, numpy as np
from datetime import datetime, timezone
from itertools import product
import requests, time, random

# ✅ 기본값 (예외:사용자 기준치가 없을 시 사용)
DEFAULT_THRESHOLDS = {
    "pm25_t": 5,
    "pm100_t": 15,
    "co2": 1000,
    "voc": 400
}

FAN_MODES_SHORT = ['max', 'medium', 'sleep', 'smart', 'windfree', 'off']
FAN_COST = {
    'max':      17,
    'medium':   13,
    'sleep':    12,
    'smart':    12,
    'windfree': 14,
    'off' :0
}
# 가중치 조정 (스케일 조정): 공기질 오차와 에너지 비용이 비슷한 범위의 값을 가지도록 

# ✅ 강화된 FAN_EFFECT (공기질 변화 더 크게 반영)
FAN_EFFECT = {
    'max':      {"pm25_t": -3.5, "pm100_t": -4.1, "co2": -2.0, "voc": -18.5},
    'medium':   {"pm25_t": -2.2, "pm100_t": -2.6, "co2": -1.7, "voc": -11.2},
    'sleep':    {"pm25_t": -1.0, "pm100_t": -1.1, "co2": -1.4, "voc": -6.7},
    'smart':    {"pm25_t": -3.1, "pm100_t": -3.8, "co2": -2.8, "voc": -16.3},
    'windfree': {"pm25_t": -0.9, "pm100_t": -1.2, "co2": -0.93, "voc": -3.5},
    'off':      {"pm25_t":  0.0, "pm100_t":  0.0, "co2":   0.0, "voc":  0.0}
}

ALL_10MIN_SCHEDULES = list(product(FAN_MODES_SHORT, repeat=6))
PREDICT_DIR = "/home/ubuntu/predicted_json/"
USER_STANDARD_DIR = "/home/ubuntu/userstandard"
POST_URL = "http://18.191.176.79:8080/ai/control"


# ✅ 사용자 기준값 불러오기
def get_user_thresholds(device_id, path=USER_STANDARD_DIR):
    try:
        files = sorted(glob.glob(os.path.join(path, "*.json")), reverse=True)
        if not files:
            raise FileNotFoundError("No userstandard file found.")
        
        with open(files[0]) as f:
            data = json.load(f)

        if device_id not in data["deviceIds"]:
            raise ValueError(f"Device ID {device_id} not found in userstandard.")

        mapping = {"pm25": "pm25_t", "pm100": "pm100_t", "co2": "co2", "voc": "voc"}
        thresholds = {mapping[k]: v for k, v in data["thresholds"].items() if k in mapping}
        return thresholds

    except Exception as e:
        print(f"⚠️ 사용자 기준값 불러오기 실패: {e}")
        print("➡️ 기본 기준값을 사용합니다.")
        return DEFAULT_THRESHOLDS


# ✅ 가장 최근 예측 불러오기
def get_latest_prediction(sensor_id=48007):
    files = sorted(glob.glob(os.path.join(PREDICT_DIR, "predict_all_*.json")), reverse=True)
    if not files:
        raise FileNotFoundError("No prediction file found.")
    with open(files[0]) as f:
        data = json.load(f)
    for p in data["predictions"]:
        if p["sensorId"] == sensor_id:
            return p["prediction"], p["deviceId"], data["timestamp"]
    raise ValueError(f"No prediction found for sensorId={sensor_id}")


# ✅ 10분 단위 평균
def segment_average(pred, key, start, end):
    segment = pred[key][start:end]
    return sum(segment) / len(segment) if segment else 0.0

# 가중치 설정
alpha = 8.0
beta = 1.0

# ✅ 스케줄 평가
def evaluate_10min_schedule(pred, schedule_6steps, thresholds):
    score_total = 0
    control_result = []
    mode_changes = sum(schedule_6steps[i] != schedule_6steps[i + 1] for i in range(5))
    if mode_changes < 1:
        return float('inf'), []

    for i, mode in enumerate(schedule_6steps):
        start, end = i * 10, (i + 1) * 10
        score_step = 0
        avg_vals = {}
        exceeds = False

        for key in thresholds:
            avg = segment_average(pred, key, start, end)
            # ✅ 예측값이 기준값보다 높고 fanMode가 off라면 강제 탈락
            if mode == 'off' and avg > thresholds[key]:
                return float('inf'), []
            
            reduction = FAN_EFFECT[mode][key] * max(avg - thresholds[key], 0)
            adjusted_avg = avg - reduction
            avg_vals[key] = adjusted_avg
            if adjusted_avg > thresholds[key]:
                exceeds = True
            err = (adjusted_avg - thresholds[key]) ** 2
            cost = FAN_COST[mode] ** 2
            score_step += alpha*err + beta*cost

        control_result.append({
            "startMinute": start,
            "endMinute": end,
            "airPurifier": "on" if mode != 'off' and exceeds else "off",
            "fanMode": mode if mode != 'off' and exceeds else "off",
            "ventilation": avg_vals["co2"] > thresholds["co2"]
        })
        score_total += score_step

    return score_total, control_result


# ✅ 제어 JSON 생성

def create_control_json(sensor_id=48007):
    pred, deviceId, ts = get_latest_prediction(sensor_id)
    thresholds = get_user_thresholds(deviceId)

    print(f" 적용된 기준값 ({deviceId}): {thresholds}")

    # 모든 스케줄 평가
    all_controls = []
    for schedule in ALL_10MIN_SCHEDULES:
        score, control = evaluate_10min_schedule(pred, schedule, thresholds)
        if control:  # 유효한 제어만 저장
            all_controls.append((score, control))

    if not all_controls:
        raise ValueError("⚠️ 유효한 제어 시나리오 없음")

    best_control = min(all_controls, key=lambda x: x[0])[1]

    return {
        "timestamp": datetime.now(timezone.utc).isoformat(timespec='seconds').replace('+00:00', 'Z'),
        "deviceId": deviceId,
        "control_result": best_control
    }

# ✅ 제어 생성 및 전송
def save_and_post_control():
    control_json = create_control_json()
    save_path = os.path.join(PREDICT_DIR, f"control_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json")
    with open(save_path, "w") as f:
        json.dump(control_json, f, indent=2)
    print(f" 저장 완료: {save_path}")
    try:
        res = requests.post(POST_URL, json=control_json, timeout=10)
        print(" 백엔드 전송 성공:", res.status_code)
    except Exception as e:
        print(" 백엔드 전송 실패:", e)

# ✅ Thread 1: 주기적 실행 루프
def run_every_hour():
    while True:
        print(" 1시간 주기 제어 실행")
        save_and_post_control()
        time.sleep(3600)

# ✅ Thread 2: 정책 파일 변경 감시
class UserStandardHandler(FileSystemEventHandler):
    def on_created(self, event):
        if event.src_path.endswith(".json"):
            print(f" 새로운 userstandard 감지됨: {event.src_path}")
            try:
                save_and_post_control()
            except Exception as e:
                print(f" 제어 생성 실패: {e}")

def watch_userstandard(path=USER_STANDARD_DIR):
    event_handler = UserStandardHandler()
    observer = Observer()
    observer.schedule(event_handler, path=path, recursive=False)
    observer.start()
    print(f" 기준값 디렉토리 감시 : {path}")
    observer.join()

# ✅ 실행부 : 사용자 정책이 바뀔때마다 최적제어 새로 생성 
if __name__ == "__main__":
    # 각각의 기능을 별도 스레드에서 실행
    t1 = threading.Thread(target=run_every_hour)
    t2 = threading.Thread(target=watch_userstandard)

    t1.start()
    t2.start()

    t1.join()
    t2.join()

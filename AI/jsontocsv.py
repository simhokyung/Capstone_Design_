import os
import json
import glob
import pandas as pd
from datetime import datetime, timedelta, time, timezone
import time as time_module

# ✅ 기본 설정
base_folder = "/home/ubuntu/data_raw_by_date"
output_path = "/home/ubuntu/train_0603.csv"
KST = timezone(timedelta(hours=9))

# ✅ 환기 규칙
def ventilation_rule(row_time: datetime):
    t = row_time.time()
    def in_range(t1, t2): return t1 <= t < t2
    if in_range(time(19, 0), time(19, 10)): return 1
    if in_range(time(20, 30), time(20, 40)): return 1
    if in_range(time(22, 0), time(22, 10)): return 1
    if in_range(time(23, 30), time(23, 40)): return 1
    if in_range(time(19, 10), time(19, 40)): return 0
    if in_range(time(20, 40), time(21, 10)): return 0
    if in_range(time(22, 10), time(22, 40)): return 0
    if in_range(time(23, 40), time(0, 10)): return 0
    return 0

# ✅ 날짜 폴더 리스트
# ✅ 원하는 날짜 폴더만 선택 (예: 20250520, 20250521, 20250522)
target_dates = ["2020520", "20250521",'20250522','20250523','202050524','20250525',"2020526", "20250527",'20250528','20250529','202050530','20250531']  # 원하는 날짜만 골라서 실행
date_folders = [os.path.join(base_folder, d) for d in target_dates]
batch_size = 100
first_chunk = True

# ✅ 날짜별 반복
for date_folder in date_folders:
    json_files = sorted(glob.glob(os.path.join(date_folder, "data_*.json")))
    
    for i in range(0, len(json_files), batch_size):
        batch = json_files[i:i + batch_size]
        measurement_rows = []
        status_rows = []

        for file in batch:
            try:
                filename = os.path.basename(file)
                file_dt = datetime.strptime(filename.replace("data_", "").replace(".json", ""), "%Y%m%d_%H%M%S")
            except:
                continue

            try:
                with open(file, 'r') as f:
                    content = json.load(f)
            except:
                continue

            for m in content.get("measurements", []):
                ts_ms = m.get("timestamp")
                if ts_ms:
                    dt_kst = datetime.fromtimestamp(ts_ms / 1000, tz=timezone.utc).astimezone(KST)
                    m["timestamp"] = dt_kst
                    measurement_rows.append(m)

            for s in content.get("statuses", []):
                fetched = s.get("fetchedAt", s.get("statusAt"))
                if fetched:
                    dt_kst = datetime.fromtimestamp(fetched, tz=timezone.utc).astimezone(KST)
                    s["fetchedAt"] = dt_kst
                    status_rows.append(s)

        if not measurement_rows or not status_rows:
            continue

        df_m = pd.DataFrame(measurement_rows)
        df_s = pd.DataFrame(status_rows)

        df_m["time_min"] = df_m["timestamp"].dt.floor("min")
        df_s["time_min"] = df_s["fetchedAt"].dt.floor("min")

        df_merge = pd.merge(df_m, df_s, on="time_min", how="inner")
        df_merge["ventilation"] = 0

        # 규칙 적용 (KST 기준)
        mask_1 = (df_merge["time_min"] >= pd.Timestamp("2025-05-09 19:50", tz=KST)) & \
                 (df_merge["time_min"] <= pd.Timestamp("2025-05-09 20:00", tz=KST))
        df_merge.loc[mask_1, "ventilation"] = 1

        mask_2 = (df_merge["time_min"] >= pd.Timestamp("2025-05-11 00:51", tz=KST)) & \
                 (df_merge["time_min"] <= pd.Timestamp("2025-05-20 08:08", tz=KST))
        df_merge.loc[mask_2, "ventilation"] = df_merge.loc[mask_2, "time_min"].apply(ventilation_rule)

        # 숫자 및 시간 형식 처리
        # 시간열 리스트
        datetime_cols = ["timestamp", "fetchedAt", "time_min"]
        exclude_from_numeric = ["timestamp", "fetchedAt", "time_min", "deviceId", "powerState", "fanMode"]
        
        # 숫자형으로 변환할 열만 따로 처리
        for col in df_merge.columns:
            if col not in exclude_from_numeric:
                df_merge[col] = pd.to_numeric(df_merge[col], errors='coerce')

        for col in datetime_cols:
            if pd.api.types.is_datetime64_any_dtype(df_merge[col]):
                df_merge[col] = df_merge[col].dt.strftime("%Y-%m-%d %H:%M:%S")
            else:
                print(f"⚠️ {col} is not datetime. Skipping formatting.")


        # 저장 (첫 배치만 header 포함)
        df_merge.to_csv(output_path, mode="a", index=False, header=first_chunk)
        first_chunk = False
        print(f"✅ {date_folder} - {i + len(batch)}개 파일 처리 완료")

        # 과부하 방지 sleep
        time_module.sleep(1)

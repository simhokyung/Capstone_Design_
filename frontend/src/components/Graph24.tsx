import React from 'react';
import { StyleSheet, View } from 'react-native';
import { CartesianChart, Line } from 'victory-native';
import { useFont, Rect } from '@shopify/react-native-skia';
import inter from "../assets/fonts/Pretendard-Medium.ttf";

type DataPoint = { x: number; y: number };

interface GraphProps {
  data: DataPoint[];
  type: string;
}

type Zone = { min: number; max: number; color: string };

const zoneList: Record<string, Zone[]> = {
  'pm25': [
    { min: 0,  max: 5, color: '#83FB8088' },
    { min: 5, max: 15, color: '#D0FBD588' },
    { min: 15, max: 25, color: '#F5F9FD88' },
    { min: 25, max: 35, color: '#F7DEDC88' },
    { min: 35, max: 50, color: '#F7A49988' },
  ],
  'pm100': [
    { min: 0,  max: 15, color: '#83FB8088' },
    { min: 15, max: 30, color: '#D0FBD588' },
    { min: 30, max: 50, color: '#F5F9FD88' },
    { min: 50, max: 70, color: '#F7DEDC88' },
    { min: 70, max: 100, color: '#F7A49988' },
  ],
  'co2': [
    { min: 0,  max: 400, color: '#83FB8088' },
    { min: 400, max: 1000, color: '#D0FBD588' },
    { min: 1000, max: 2000, color: '#F5F9FD88' },
    { min: 2000, max: 3000, color: '#F7DEDC88' },
    { min: 3000, max: 4000, color: '#F7A49988' },
  ],
  'voc': [
    { min: 0,  max: 100, color: '#83FB8088' },
    { min: 100, max: 400, color: '#D0FBD588' },
    { min: 400, max: 1000, color: '#F5F9FD88' },
    { min: 1000, max: 2000, color: '#F7DEDC88' },
    { min: 2000, max: 3000, color: '#F7A49988' },
  ],
  'o3': [
    { min: 0,  max: 30, color: '#83FB8088' },
    { min: 30, max: 60, color: '#D0FBD588' },
    { min: 60, max: 70, color: '#F5F9FD88' },
    { min: 70, max: 100, color: '#F7DEDC88' },
    { min: 100, max: 130, color: '#F7A49988' },
  ],
  'no2': [
    { min: 0,  max: 10, color: '#83FB8088' },
    { min: 10, max: 20, color: '#D0FBD588' },
    { min: 20, max: 30, color: '#F5F9FD88' },
    { min: 30, max: 40, color: '#F7DEDC88' },
    { min: 40, max: 50, color: '#F7A49988' },
  ],
}



export const Graph24: React.FC<GraphProps> =({data, type}) => {
  const font=useFont(inter, 14);
  const DOMAIN_Y_MAX = zoneList[type][4].max;
  return (
    <View style={styles.chartWrapper}>
      <CartesianChart
        xKey="x"
        yKeys={['y']}
        domain={{ y: [0, DOMAIN_Y_MAX] }}
        data={data}
        padding={{left: 5, right: 5, top: 10, bottom: 0,}}
        domainPadding={{left: 0, right: 20, top: 0, bottom: 0,}}
        frame={{
          lineColor: '#AAAAAA',
          lineWidth: 1.5,
        }}
        xAxis={{
          font: font,
          tickValues: [0, 4, 8, 12, 16, 20, 24],
          lineWidth: 0.5,
          formatXLabel: (v: number) => `${v}시`,
        }}
        yAxis={[
          {
            font: font,
            tickCount: 5,
            lineWidth: 1,
          },
        ]}
      >
        {({ points, chartBounds }) => {
          const { left, top, right, bottom } = chartBounds;
          const width  = right - left;
          const height = bottom - top;

          const toYPixel = (val: number) =>
            bottom - (val / DOMAIN_Y_MAX) * height;

          return (
            <>
              {zoneList[type].map((zone, i) => {
                const yHigh = toYPixel(zone.max);
                const yLow  = toYPixel(zone.min);
                return (
                  <Rect
                    key={i}
                    x={left}
                    y={yHigh}
                    width={width}
                    height={yLow - yHigh}
                    color={zone.color}
                  />
                );
              })}

              <Line
                points={points.y}
                color="#01B3EA"
                curveType="linear"
                strokeWidth={2.5}
              />
            </>
          );
        }}
      </CartesianChart>
    </View>
  );
}

const styles = StyleSheet.create({
  chartWrapper: {
    width: '100%',
    height: '100%',
  },
});
